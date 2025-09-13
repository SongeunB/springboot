package com.example.first.dto;

import com.example.first.entity.Article;
import com.example.first.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class ArticleDto {
    private Long id;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(min = 1, max = 100, message = "제목은 1-100자 사이여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    @Size(min = 1, max = 5000, message = "내용은 1-5000자 사이여야 합니다.")
    private String content;

    // 작성자 정보 (읽기 전용)
    private Long authorId;
    private String authorUsername;
    private String authorNickname;

    // 날짜 정보 (읽기 전용)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long viewCount;

    // 생성자 (게시글 작성용 - 제목과 내용만)
    public ArticleDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // 생성자 (ID, 제목, 내용만 - 기존 호환성 유지)
    public ArticleDto(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    /**
     * DTO → Entity 변환 (새 게시글 생성용)
     * 작성자 정보가 필요하므로 별도 메서드로 분리
     */
    public Article toEntity(User author) {
        return new Article(title, content, author);
    }

    /**
     * 기존 toEntity() 메서드는 deprecated 처리
     * 작성자 정보 없이는 Article 엔티티를 생성할 수 없음
     */
    @Deprecated
    public Article toEntity() {
        // 기존 코드 호환성을 위해 유지하지만, 실제로는 사용하지 않는 것을 권장
        throw new UnsupportedOperationException("작성자 정보가 필요합니다. toEntity(User author)를 사용해주세요.");
    }

    /**
     * Entity → DTO 변환 (정적 팩토리 메서드)
     */
    public static ArticleDto from(Article article) {
        return new ArticleDto(
                article.getId(),
                article.getTitle(),
                article.getContent(),
                article.getAuthor().getId(),
                article.getAuthor().getUsername(),
                article.getAuthor().getNickname(),
                article.getCreatedAt(),
                article.getUpdatedAt(),
                article.getViewCount()
        );
    }

    /**
     * 날짜를 포맷팅해서 반환
     */
    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public String getFormattedUpdatedAt() {
        if (updatedAt == null) return "";
        return updatedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    /**
     * 수정 여부 확인
     */
    public boolean isUpdated() {
        return updatedAt != null;
    }

    /**
     * 현재 사용자가 작성자인지 확인
     */
    public boolean isAuthor(User currentUser) {
        if (currentUser == null || authorId == null) {
            return false;
        }
        return authorId.equals(currentUser.getId());
    }

    /**
     * 게시글 요약 (미리보기용)
     */
    public String getContentPreview(int maxLength) {
        if (content == null) return "";
        if (content.length() <= maxLength) return content;
        return content.substring(0, maxLength) + "...";
    }

    /**
     * 기본 미리보기 (100자)
     */
    public String getContentPreview() {
        return getContentPreview(100);
    }
}