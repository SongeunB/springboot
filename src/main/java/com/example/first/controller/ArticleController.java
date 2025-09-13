package com.example.first.controller;

import com.example.first.dto.ArticleDto;
import com.example.first.entity.Article;
import com.example.first.entity.User;
import com.example.first.service.ArticleService;
import com.example.first.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ArticleController {

    private final ArticleService articleService;
    private final UserService userService;

    /**
     * 게시글 목록 페이지
     */
    @GetMapping("/articles")
    public String index(Model model,
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String type) {

        List<ArticleDto> articles;

        if (search != null && !search.trim().isEmpty()) {
            // 검색 처리
            switch (type != null ? type : "all") {
                case "title":
                    articles = articleService.searchByTitle(search);
                    break;
                case "content":
                    articles = articleService.searchByContent(search);
                    break;
                default:
                    articles = articleService.searchByTitleOrContent(search);
                    break;
            }
            model.addAttribute("search", search);
            model.addAttribute("type", type);
        } else {
            // 전체 목록
            articles = articleService.indexAsDto();
        }

        model.addAttribute("articles", articles);
        return "articles/index";
    }

    /**
     * 게시글 상세 페이지
     */
    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id,
                       Model model,
                       @AuthenticationPrincipal UserDetails currentUserDetails) {

        // 게시글 조회 (조회수 증가 포함)
        ArticleDto article = articleService.showWithViewCount(id);
        if (article == null) {
            log.error("게시글을 찾을 수 없습니다: ID={}", id);
            return "redirect:/articles";
        }

        // 현재 사용자 정보
        User currentUser = null;
        if (currentUserDetails != null) {
            currentUser = userService.findByUsername(currentUserDetails.getUsername());
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("isAuthor", article.isAuthor(currentUser));
        }

        model.addAttribute("article", article);
        return "articles/show";
    }

    /**
     * 새 게시글 작성 페이지
     */
    @GetMapping("/articles/new")
    public String newArticleForm(Model model) {
        model.addAttribute("articleDto", new ArticleDto());
        return "articles/new";
    }

    /**
     * 게시글 생성 처리
     */
    @PostMapping("/articles")
    public String create(@Valid @ModelAttribute ArticleDto articleDto,
                         BindingResult result,
                         @AuthenticationPrincipal UserDetails currentUserDetails,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        log.info("게시글 생성 요청: 제목={}", articleDto.getTitle());

        // 로그인 확인
        if (currentUserDetails == null) {
            return "redirect:/login";
        }

        // 유효성 검사
        if (result.hasErrors()) {
            log.error("게시글 생성 유효성 검사 실패: {}", result.getAllErrors());
            return "articles/new";
        }

        try {
            // 현재 사용자 조회
            User currentUser = userService.findByUsername(currentUserDetails.getUsername());

            // 게시글 생성
            Article savedArticle = articleService.create(articleDto, currentUser);

            if (savedArticle != null) {
                log.info("게시글 생성 성공: ID={}", savedArticle.getId());
                redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 작성되었습니다!");
                return "redirect:/articles/" + savedArticle.getId();
            } else {
                model.addAttribute("errorMessage", "게시글 작성에 실패했습니다.");
                return "articles/new";
            }

        } catch (Exception e) {
            log.error("게시글 생성 중 오류 발생", e);
            model.addAttribute("errorMessage", "게시글 작성 중 오류가 발생했습니다.");
            return "articles/new";
        }
    }

    /**
     * 게시글 수정 페이지
     */
    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id,
                       Model model,
                       @AuthenticationPrincipal UserDetails currentUserDetails) {

        // 로그인 확인
        if (currentUserDetails == null) {
            return "redirect:/login";
        }

        // 게시글 조회
        ArticleDto article = articleService.showAsDto(id);
        if (article == null) {
            log.error("게시글을 찾을 수 없습니다: ID={}", id);
            return "redirect:/articles";
        }

        // 작성자 권한 확인
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        if (!article.isAuthor(currentUser)) {
            log.error("게시글 수정 권한이 없습니다: 게시글ID={}, 사용자={}", id, currentUser.getUsername());
            return "redirect:/articles/" + id;
        }

        model.addAttribute("articleDto", article);
        return "articles/edit";
    }

    /**
     * 게시글 수정 처리
     */
    @PostMapping("/articles/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute ArticleDto articleDto,
                         BindingResult result,
                         @AuthenticationPrincipal UserDetails currentUserDetails,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        log.info("게시글 수정 요청: ID={}", id);

        // 로그인 확인
        if (currentUserDetails == null) {
            return "redirect:/login";
        }

        // 유효성 검사
        if (result.hasErrors()) {
            log.error("게시글 수정 유효성 검사 실패: {}", result.getAllErrors());
            model.addAttribute("articleDto", articleDto);
            return "articles/edit";
        }

        try {
            // 현재 사용자 조회
            User currentUser = userService.findByUsername(currentUserDetails.getUsername());

            // 게시글 수정
            Article updatedArticle = articleService.update(id, articleDto, currentUser);

            if (updatedArticle != null) {
                log.info("게시글 수정 성공: ID={}", updatedArticle.getId());
                redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다!");
                return "redirect:/articles/" + updatedArticle.getId();
            } else {
                model.addAttribute("errorMessage", "게시글 수정에 실패했습니다.");
                model.addAttribute("articleDto", articleDto);
                return "articles/edit";
            }

        } catch (Exception e) {
            log.error("게시글 수정 중 오류 발생", e);
            model.addAttribute("errorMessage", "게시글 수정 중 오류가 발생했습니다.");
            model.addAttribute("articleDto", articleDto);
            return "articles/edit";
        }
    }

    /**
     * 게시글 삭제 처리
     */
    @PostMapping("/articles/{id}/delete")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal UserDetails currentUserDetails,
                         RedirectAttributes redirectAttributes) {

        log.info("게시글 삭제 요청: ID={}", id);

        // 로그인 확인
        if (currentUserDetails == null) {
            return "redirect:/login";
        }

        try {
            // 현재 사용자 조회
            User currentUser = userService.findByUsername(currentUserDetails.getUsername());

            // 게시글 삭제
            Article deletedArticle = articleService.delete(id, currentUser);

            if (deletedArticle != null) {
                log.info("게시글 삭제 성공: ID={}", id);
                redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 삭제되었습니다!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "게시글 삭제에 실패했습니다.");
            }

        } catch (Exception e) {
            log.error("게시글 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "게시글 삭제 중 오류가 발생했습니다.");
        }

        return "redirect:/articles";
    }

    /**
     * 내 게시글 목록
     */
    @GetMapping("/my-articles")
    public String myArticles(Model model,
                             @AuthenticationPrincipal UserDetails currentUserDetails) {

        // 로그인 확인
        if (currentUserDetails == null) {
            return "redirect:/login";
        }

        // 현재 사용자의 게시글 조회
        User currentUser = userService.findByUsername(currentUserDetails.getUsername());
        List<ArticleDto> articles = articleService.findByAuthor(currentUser);

        model.addAttribute("articles", articles);
        model.addAttribute("currentUser", currentUser);
        return "articles/my-articles";
    }

    /**
     * 게시글 검색 API (AJAX용)
     */
    @GetMapping("/api/articles/search")
    @ResponseBody
    public List<ArticleDto> searchArticles(@RequestParam String keyword,
                                           @RequestParam(defaultValue = "all") String type) {

        log.info("게시글 검색 요청: keyword={}, type={}", keyword, type);

        switch (type) {
            case "title":
                return articleService.searchByTitle(keyword);
            case "content":
                return articleService.searchByContent(keyword);
            default:
                return articleService.searchByTitleOrContent(keyword);
        }
    }

    /**
     * 인기 게시글 조회 (조회수 기준)
     */
    @GetMapping("/articles/popular")
    public String popularArticles(Model model) {
        // 이 기능은 Repository에 메서드가 추가된 후 구현 가능
        // List<ArticleDto> popularArticles = articleService.getPopularArticles();
        // model.addAttribute("articles", popularArticles);

        // 현재는 전체 목록으로 대체
        return "redirect:/articles";
    }

    /**
     * 최근 게시글 조회
     */
    @GetMapping("/articles/recent")
    public String recentArticles(Model model) {
        List<ArticleDto> articles = articleService.indexAsDto();
        model.addAttribute("articles", articles);
        model.addAttribute("isRecent", true);
        return "articles/index";
    }
}