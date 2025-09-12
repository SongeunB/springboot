package com.example.first.entity;

import com.example.first.dto.CommentDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="article_id")
    private Article article;
    @Column
    private String nickname;
    @Column
    private String body;

    public static Comment create(CommentDto commentDto, Article article) {
        // 예외 발생
        if (commentDto.getId() != null)
            throw new IllegalArgumentException("댓글 생성 실패. 댓글의 ID가 없어야 합니다.");
        if (commentDto.getArticleId() != article.getId())
            throw new IllegalArgumentException("댓글 생성 실패. 게시글의 ID가 잟못되었습니다.");
        // 객체 생성
        return new Comment(
                commentDto.getId(),
                article,
                commentDto.getNickname(),
                commentDto.getBody()
        );
    }

    public void patch(CommentDto commentDto) {
        // 예외 발생
        if(this.id != commentDto.getId())
            throw  new IllegalArgumentException("댓글 수정 실패. 잘못된 댓글 ID");
        // 객체 갱신
        if(commentDto.getNickname() != null) {
            this.nickname = commentDto.getNickname();
        }
        if (commentDto.getBody() != null) {
            this.body = commentDto.getBody();
        }
    }
}


