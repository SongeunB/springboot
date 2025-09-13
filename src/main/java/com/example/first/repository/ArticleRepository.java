package com.example.first.repository;

import com.example.first.entity.Article;
import com.example.first.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // 기본 조회 (작성일 내림차순)
    List<Article> findAllByOrderByCreatedAtDesc();

    // 특정 사용자의 게시글 조회 (작성일 내림차순)
    List<Article> findByAuthorOrderByCreatedAtDesc(User author);

    // 제목으로 검색 (대소문자 무시, 작성일 내림차순)
    List<Article> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title);

    // 내용으로 검색 (대소문자 무시, 작성일 내림차순)
    List<Article> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String content);

    // 제목 또는 내용으로 검색 (대소문자 무시, 작성일 내림차순)
    List<Article> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(
            String title, String content);

    // 페이징된 게시글 조회 (작성일 내림차순)
    Page<Article> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 특정 사용자의 게시글 페이징 조회
    Page<Article> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    // 제목으로 검색 (페이징)
    Page<Article> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title, Pageable pageable);

    // 내용으로 검색 (페이징)
    Page<Article> findByContentContainingIgnoreCaseOrderByCreatedAtDesc(String content, Pageable pageable);

    // 제목 또는 내용으로 검색 (페이징)
    Page<Article> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCaseOrderByCreatedAtDesc(
            String title, String content, Pageable pageable);

    // 조회수 Top N 게시글
    List<Article> findTop5ByOrderByViewCountDesc();

    // 최근 게시글 Top N
    List<Article> findTop5ByOrderByCreatedAtDesc();

    // 커스텀 쿼리: 제목, 내용, 작성자 닉네임으로 통합 검색
    @Query("SELECT a FROM Article a JOIN a.author u WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY a.createdAt DESC")
    List<Article> searchByKeyword(@Param("keyword") String keyword);

    // 커스텀 쿼리: 통합 검색 (페이징)
    @Query("SELECT a FROM Article a JOIN a.author u WHERE " +
            "LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY a.createdAt DESC")
    Page<Article> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 특정 기간 내 게시글 수 조회
    @Query("SELECT COUNT(a) FROM Article a WHERE a.createdAt >= :startDate")
    long countArticlesSince(@Param("startDate") java.time.LocalDateTime startDate);

    // 사용자별 게시글 수 조회
    long countByAuthor(User author);
}