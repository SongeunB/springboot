package com.example.first.controller;

import com.example.first.dto.ArticleDto;
import com.example.first.dto.CommentDto;
import com.example.first.entity.Article;
import com.example.first.repository.ArticleRepository;
import com.example.first.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@Slf4j
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentService commentService;

    @GetMapping("/articles/new")
    public String newArticleForm(){
        return "articles/new";
    }

    @PostMapping("/articles/create")
    public String CreateArticle(ArticleDto articleForm){
        log.info(articleForm.toString());
        // DTO를 Entity로 변환
        Article article = articleForm.toEntity();
        log.info(article.toString());

        // Repository로 Entity를 DB에 저장
        Article saved = articleRepository.save(article);
        log.info(saved.toString());
        return "redirect:/articles/"+saved.getId();
    }

    @GetMapping("/articles/{id}")
    public String show(@PathVariable Long id, Model model){
        log.info("id: {}", id.toString());
        // id를 조회해 데이터 가져오기
        Article article = articleRepository.findById(id).orElse(null);
        List<CommentDto> commentDtos = commentService.comments(id);
        //모델에 데이터 등록하기
        model.addAttribute("article", article);
        model.addAttribute("commentDtos", commentDtos);
        //뷰 페이지 반환하기
        return "articles/show";
    }

    @GetMapping("/articles")
    public String index(Model model){
        // 모든 데이터 가져오기
        List<Article> articles = articleRepository.findAll();

        // 모델에 데이터 등록하기
        model.addAttribute("articleList", articles);

        // 뷰페이지 설정하기
        return "articles/index";
    }

    @GetMapping("/articles/{id}/edit")
    public String edit(@PathVariable Long id, Model model) {
        // 수정할 데이터 가져오기
        Article article = articleRepository.findById(id).orElse(null);

        // 모델에 데이터 등록하기
        model.addAttribute("article", article);

        // 뷰 페이지 설정하기
        return "articles/edit";
    }

    @PostMapping("/articles/update")
    public String update(ArticleDto articleForm){
        log.info(articleForm.toString());
        // DTO를 엔티티로 변환하기
        Article article = articleForm.toEntity();

        // 엔티티를 DB에 저장하기
        Article target = articleRepository.findById(article.getId()).orElse(null);
        if(target != null) articleRepository.save(article);

        // 수정 결과 페이지로 리다이렉트 하기
        return "redirect:/articles/"+target.getId();
    }

    @GetMapping("/articles/{id}/delete")
        public String delete(@PathVariable Long id, RedirectAttributes rttr){
        log.info("Delete article {}", id);
        // 삭제할 대상 가져오기
        Article target = articleRepository.findById(id).orElse(null);
        log.info("target {}", target.toString());

        // 대상 엔티티 삭제하기
        if(target != null) {
            articleRepository.delete(target);
            rttr.addFlashAttribute("message", "article deleted successfully");
        }

        // 결과 페이지로 리다이렉트 하기
        return "redirect:/articles";
    }
}
