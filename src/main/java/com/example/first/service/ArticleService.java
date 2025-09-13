package com.example.first.service;

import com.example.first.dto.ArticleDto;
import com.example.first.entity.Article;
import com.example.first.entity.User;
import com.example.first.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    /**
     * 모든 게시글 조회
     */
    public List<Article> index() {
        return articleRepository.findAll();
    }

    /**
     * 모든 게시글을 DTO로 조회
     */
    public List<ArticleDto> indexAsDto() {
        return articleRepository.findAll()
                .stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 게시글 조회
     */
    public Article show(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    /**
     * 특정 게시글 조회 (DTO)
     */
    public ArticleDto showAsDto(Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        return article != null ? ArticleDto.from(article) : null;
    }

    /**
     * 게시글 조회 (조회수 증가 포함)
     */
    @Transactional
    public ArticleDto showWithViewCount(Long id) {
        Article article = articleRepository.findById(id).orElse(null);
        if (article != null) {
            article.incrementViewCount();
            article = articleRepository.save(article);
            return ArticleDto.from(article);
        }
        return null;
    }

    /**
     * 게시글 생성 (사용자 정보 포함)
     */
    @Transactional
    public Article create(ArticleDto articleDto, User author) {
        log.info("게시글 생성 시도: 제목={}, 작성자={}", articleDto.getTitle(), author.getUsername());

        // 새 게시글인지 확인
        if (articleDto.getId() != null) {
            log.error("새 게시글에 ID가 존재합니다: {}", articleDto.getId());
            return null;
        }

        // DTO를 엔티티로 변환
        Article article = articleDto.toEntity(author);
        Article savedArticle = articleRepository.save(article);

        log.info("게시글 생성 완료: ID={}", savedArticle.getId());
        return savedArticle;
    }

    /**
     * 게시글 수정
     */
    @Transactional
    public Article update(Long id, ArticleDto articleDto, User currentUser) {
        log.info("게시글 수정 시도: ID={}, 사용자={}", id, currentUser.getUsername());

        // 기존 게시글 조회
        Article target = articleRepository.findById(id).orElse(null);
        if (target == null) {
            log.error("게시글을 찾을 수 없습니다: ID={}", id);
            return null;
        }

        // 작성자 권한 확인
        if (!target.isAuthor(currentUser)) {
            log.error("게시글 수정 권한이 없습니다: 게시글ID={}, 현재사용자={}, 작성자={}",
                    id, currentUser.getUsername(), target.getAuthor().getUsername());
            return null;
        }

        // 게시글 수정
        Article updateArticle = new Article();
        updateArticle.setTitle(articleDto.getTitle());
        updateArticle.setContent(articleDto.getContent());

        target.patch(updateArticle);
        Article savedArticle = articleRepository.save(target);

        log.info("게시글 수정 완료: ID={}", savedArticle.getId());
        return savedArticle;
    }

    /**
     * 게시글 삭제
     */
    @Transactional
    public Article delete(Long id, User currentUser) {
        log.info("게시글 삭제 시도: ID={}, 사용자={}", id, currentUser.getUsername());

        // 기존 게시글 조회
        Article target = articleRepository.findById(id).orElse(null);
        if (target == null) {
            log.error("게시글을 찾을 수 없습니다: ID={}", id);
            return null;
        }

        // 작성자 또는 관리자 권한 확인
        if (!target.isAuthor(currentUser) && !isAdmin(currentUser)) {
            log.error("게시글 삭제 권한이 없습니다: 게시글ID={}, 현재사용자={}, 작성자={}",
                    id, currentUser.getUsername(), target.getAuthor().getUsername());
            return null;
        }

        // 게시글 삭제
        articleRepository.delete(target);
        log.info("게시글 삭제 완료: ID={}", id);
        return target;
    }

    /**
     * 특정 사용자의 게시글 조회
     */
    public List<ArticleDto> findByAuthor(User author) {
        return articleRepository.findByAuthorOrderByCreatedAtDesc(author)
                .stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 검색 (제목으로)
     */
    public List<ArticleDto> searchByTitle(String keyword) {
        return articleRepository.findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword)
                .stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 검색 (내용으로)
     */
    public List<ArticleDto> searchByContent(String keyword) {
        return articleRepository.findByContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword)
                .stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 검색 (제목 또는 내용)
     */
    public List<ArticleDto> searchByTitleOrContent(String keyword) {
        return articleRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(keyword, keyword)
                .stream()
                .map(ArticleDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 관리자 권한 확인
     */
    private boolean isAdmin(User user) {
        return user.getRole() == User.Role.ADMIN;
    }

    /**
     * 기존 메서드들 (호환성 유지, 하지만 deprecated)
     */
    @Deprecated
    public Article create(ArticleDto articleForm) {
        log.warn("create(ArticleDto) 메서드는 더 이상 사용되지 않습니다. create(ArticleDto, User)를 사용해주세요.");
        throw new UnsupportedOperationException("작성자 정보가 필요합니다.");
    }

    @Deprecated
    public Article update(Long id, ArticleDto articleForm) {
        log.warn("update(Long, ArticleDto) 메서드는 더 이상 사용되지 않습니다. update(Long, ArticleDto, User)를 사용해주세요.");
        throw new UnsupportedOperationException("사용자 정보가 필요합니다.");
    }

    @Deprecated
    public Article delete(Long id) {
        log.warn("delete(Long) 메서드는 더 이상 사용되지 않습니다. delete(Long, User)를 사용해주세요.");
        throw new UnsupportedOperationException("사용자 정보가 필요합니다.");
    }

    /**
     * 벌크 생성 (테스트용)
     */
    @Transactional
    public List<Article> createArticles(List<ArticleDto> articleForms, User author) {
        List<Article> articleList = articleForms.stream()
                .map(articleForm -> articleForm.toEntity(author))
                .collect(Collectors.toList());

        articleList.forEach(article -> articleRepository.save(article));

        return articleList;
    }
}