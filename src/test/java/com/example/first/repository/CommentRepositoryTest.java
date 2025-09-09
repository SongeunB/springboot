package com.example.first.repository;

import com.example.first.entity.Article;
import com.example.first.entity.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("특정 게시글의 모든 댓글 조회")
    void findByArticleId() {
        /* case1. 4번 게시글의 모든 댓글 조회 */
//        {
//            // 입력 데이터 준비
//            Long articleId = 4L;
//            // 실제 데이터
//            List<Comment> comments = commentRepository.findByArticleId(articleId);
//            // 예상 데이터
//            Article article = new Article(4L, "당신의 인생 영화는?", "댓글 고");
//            Comment a = new Comment(1L, article, "Park", "Ironman");
//            Comment b = new Comment(2L, article, "Kim", "Batman");
//            Comment c = new Comment(3L, article, "Choi", "Captain America");
//            List<Comment> expected = Arrays.asList(a, b, c);
//            // 비교 및 검증
//            assertEquals(expected.toString(), comments.toString(), "4번 글의 모든 댓글을 출력");
//        }
        /* case2. 1번 게시글의 모든 댓글 조회 */
        {
            // 입력 데이터 준비
            Long articleId = 1L;
            // 실제 데이터
            List<Comment> comments = commentRepository.findByArticleId(articleId);
            // 예상 데이터
            Article article = new Article(1L, "가가가", "111");
            List<Comment> expected = Arrays.asList();
            // 비교 및 검증
            assertEquals(expected.toString(), comments.toString(), "1번 글은 댓글 없음");
        }
    }

    @Test
    @DisplayName("특정 닉네임의 모든 댓글 조회")
    void findByNickname() {
        /* case1. Park의 모든 댓글 조회 */
        {
            // 입력 데이터 준비
            String nickname = "Park";
            // 실제 데이터
            List<Comment> comments = commentRepository.findByNickname(nickname);
            // 예상 데이터
            Comment a = new Comment(1L, new Article(4L, "당신의 인생 영화는?", "댓글 고"), nickname, "Ironman");
            Comment b = new Comment(4L, new Article(5L, "당신의 소울 푸드는?", "댓글 고고"), nickname, "Sushi");
            Comment c = new Comment(7L, new Article(6L, "당신의 취미는?", "댓글 고고고"), nickname, "Yoga");
            List<Comment> expected = Arrays.asList(a, b, c);
            // 비교 및 검증
            assertEquals(expected.toString(), comments.toString(), "Park의 모든 댓글 출력");
        }
    }
}