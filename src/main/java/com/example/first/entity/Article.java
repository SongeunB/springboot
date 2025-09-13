package com.example.first.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@ToString(exclude = "author") // author 필드는 toString에서 제외 (순환 참조 방지)
@NoArgsConstructor
@Getter
@Setter
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 ID 자동 생성
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    // 작성자와의 관계 (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    // 작성일시
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 수정일시
    private LocalDateTime updatedAt;

    // 조회수
    @Column(nullable = false)
    private Long viewCount = 0L;

    // 생성자 (작성자와 함께)
    public Article(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0L;
    }

    // JPA 라이프사이클 콜백
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (viewCount == null) {
            viewCount = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 기존 patch 메서드 유지
    public void patch(Article article) {
        if (article.title != null && !article.title.trim().isEmpty()) {
            this.title = article.title;
        }
        if (article.content != null && !article.content.trim().isEmpty()) {
            this.content = article.content;
        }
        this.updatedAt = LocalDateTime.now();
    }

    // 조회수 증가 메서드
    public void incrementViewCount() {
        this.viewCount++;
    }

    // 작성자 확인 메서드 (권한 체크용)
    public boolean isAuthor(User user) {
        return author != null && author.getId().equals(user.getId());
    }
}