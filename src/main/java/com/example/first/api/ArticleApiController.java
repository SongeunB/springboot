package com.example.first.api;

import com.example.first.dto.ArticleForm;
import com.example.first.entity.Article;
import com.example.first.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController()
@Slf4j
public class ArticleApiController {
    @Autowired
    private ArticleService articleService;

    // GET
    @GetMapping( "/api/articles")
    public List<Article> index() {
        return articleService.index();
    }

    @GetMapping( "/api/articles/{id}")
    public Article show(@PathVariable Long id) {
        return articleService.show(id);
    }

    // POST
    @PostMapping("/api/articles")
    public ResponseEntity<Article> create(@RequestBody ArticleForm articleForm) {
        Article article = articleService.create(articleForm);
        return (article != null) ? ResponseEntity.status(HttpStatus.OK).body(article) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    // PATCH
    @PatchMapping("/api/articles/{id}")
    public ResponseEntity<Article> update(@PathVariable Long id,
                                         @RequestBody ArticleForm articleForm) {
        Article updated = articleService.update(id, articleForm);
        return (updated != null) ? ResponseEntity.status(HttpStatus.OK).body(updated) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

    }

    // DELETE
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Article> delete(@PathVariable Long id) {
        Article deleted = articleService.delete(id);
        return (deleted != null) ? ResponseEntity.status(HttpStatus.OK).body(deleted) :
                ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PostMapping("/api/transaction-test")
    public ResponseEntity<List<Article>> transactionTest(
            @RequestBody List<ArticleForm> articleForms) {
        List<Article> articles = articleService.createArticles(articleForms);
        return (articles != null) ? ResponseEntity.status(HttpStatus.OK).body(articles)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
