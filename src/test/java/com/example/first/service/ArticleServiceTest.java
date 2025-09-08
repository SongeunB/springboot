package com.example.first.service;

import com.example.first.dto.ArticleForm;
import com.example.first.entity.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ArticleServiceTest {
    @Autowired
    ArticleService articleService;

    @Test
    void index() {
        // 예상 데이터
        Article a = new Article(1L, "가가가", "111");
        Article b = new Article(2L, "나나나", "222");
        Article c = new Article(3L, "다다다", "333");
        List<Article> expected = new ArrayList<Article>(Arrays.asList(a, b, c));
        // 실제 데이터
        List<Article> articles = articleService.index();
        // 비교 및 검증
        assertEquals(expected.toString(), articles.toString());
    }

    @Test
    void showSuccess() {
        // 예상 데이터
        Long id = 1L;
        Article expected = new Article(1L, "가가가", "111");
        // 실제 데이터
        Article actual = articleService.show(id);
        // 비교 및 검증
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    void showFail() {
        // 예상 데이터
        Long id = -1L;
        Article expected = null;
        // 실제 데이터
        Article actual = articleService.show(id);
        // 비교 및 검증
        assertEquals(expected, actual);
    }

    @Test
    @Transactional
    void createSuccess() {
        // 예상 데이터
        String title = "라라라";
        String content = "444";
        ArticleForm articleForm = new ArticleForm(null, title, content);
        Article expected = new Article(4L, title, content);
        // 실제 데이터
        Article actual = articleService.create(articleForm);
        // 비교 및 검증
        assertEquals(expected.toString(), actual.toString());
    }

    @Test
    @Transactional
    void createFail() {
        // 예상 데이터
        Long id = 4L;
        String title = "라라라";
        String content = "444";
        ArticleForm articleForm = new ArticleForm(id, title, content);
        Article expected = null;
        // 실제 데이터
        Article actual = articleService.create(articleForm);
        // 비교 및 검증
        assertEquals(expected, actual);
    }
}