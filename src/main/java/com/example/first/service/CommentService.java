package com.example.first.service;

import com.example.first.dto.CommentDto;
import com.example.first.entity.Article;
import com.example.first.entity.Comment;
import com.example.first.repository.ArticleRepository;
import com.example.first.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ArticleRepository articleRepository;

    // 댓글 조회
    public List<CommentDto> comments(Long articleId) {
        /*// 댓글 조회
        List<Comment> comments = commentRepository.findByArticleId(articleId);
        // 엔티티를 DTO로 변환
        List<CommentDto> commentDtos = new ArrayList<CommentDto>();
        comments.forEach(comment -> {
            CommentDto commentDto = CommentDto.createDto(comment);
            commentDtos.add(commentDto);
        });
        // 결과 반환
        return commentDtos;*/
        return commentRepository.findByArticleId(articleId)
                .stream()
                .map(comment -> CommentDto.createDto(comment))
                .collect(Collectors.toList());

    }

    // 댓글 생성
    @Transactional
    public CommentDto create(Long articleId, CommentDto commentDto) {
        // 게시글 조회 및 예외 발생
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패 " + articleId
                + " 게시글을 찾을 수 없습니다."));
        // 댓글 엔티티 생성
        Comment comment = Comment.create(commentDto, article);
        // 댓글 엔티티를 DB에 저장
        Comment savedComment = commentRepository.save(comment);
        // DTO로 변환해 반환
        return CommentDto.createDto(savedComment);
    }

    // 댓글 수정
    @Transactional
    public CommentDto update(Long id, CommentDto commentDto) {
        // 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 수정 실패. 대상 댓글이 없습니다."));
        // 댓글 수정
        target.patch(commentDto);
        // DB 갱신
        Comment updatedComment = commentRepository.save(target);
        // 댓글 엔티티를 DTO로 변환해 반환
        return CommentDto.createDto(updatedComment);
    }

    // 댓글 삭제
    @Transactional
    public CommentDto delete(Long id) {
        // 댓글 조회 및 예외 발생
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 삭제 실패. 대상이 없습니다."));
        // 댓글 삭제
        commentRepository.delete(comment);
        // 삭제 댓글을 DTO로 변환해 반환
        return CommentDto.createDto(comment);
    }
}
