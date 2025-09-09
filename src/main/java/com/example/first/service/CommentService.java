package com.example.first.service;

import com.example.first.dto.CommentDto;
import com.example.first.repository.ArticleRepository;
import com.example.first.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ArticleRepository articleRepository;

    // 댓글 조회
    public static List<CommentDto> comments(Long articleId) {
    }

    // 댓글 생성

    // 댓글 수정

    // 댓글 삭제


}
