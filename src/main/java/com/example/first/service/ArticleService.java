package com.example.first.service;

import com.example.first.dto.ArticleDto;
import com.example.first.entity.Article;
import com.example.first.repository.ArticleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    public List<Article> index() {
        return articleRepository.findAll();
    }

    public Article show(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public Article create(ArticleDto articleForm) {
        if(articleForm.getId() != null) return null;

        Article article = articleForm.toEntity();
        return articleRepository.save(article);
    }

    public Article update(Long id, ArticleDto articleForm) {
        // DTO를 엔티티로 변환
        Article article = articleForm.toEntity();
        log.info("articleForm={}", articleForm);
        // DB조회하기
        Article target = articleRepository.findById(article.getId()).orElse(null);
        // 잘못된 요청 처리
        if (target == null || id != target.getId()) {
            log.info("잘못된 요청 articleForm={}", articleForm);
            return null;
        }
        // DB업데이트
        target.patch(article);
        return articleRepository.save(target);
    }

    public Article delete(Long id) {
        // DB 조회
        Article target = articleRepository.findById(id).orElse(null);
        // 잘못된 요청 처리
        if (target == null || id != target.getId()) {
            log.info("잘못된 요청 id={}", id);
            return null;
        }
        // 대상 삭제
        articleRepository.delete(target);
        return target;
    }

    @Transactional
    public List<Article> createArticles(List<ArticleDto> articleForms) {
        // DTO 묶음을 엔티티 묶음으로 변환
        List<Article> articleList = articleForms.stream()
                .map(articleForm -> articleForm.toEntity())
                .collect(Collectors.toList());

        // 엔티티 묶음을 저장
        articleList.stream()
                .forEach(article -> articleRepository.save(article));

        // 강제 예외 발생
        articleRepository.findById(-1L)
                .orElseThrow(() -> new IllegalArgumentException("Fail to create article"));

        // 결과 반환
        return articleList;
    }
}
