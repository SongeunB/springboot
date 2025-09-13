-- 사용자 데이터 (비밀번호는 BCrypt로 암호화된 "password123")
INSERT INTO users (username, password, email, nickname, role, enabled, account_non_expired, account_non_locked, credentials_non_expired) VALUES
     ('admin', '$2a$10$p2QTj1SudPP/G/5PWEc8JOogiss9k0L7hvGAtNpGpmiX9xEGXGRuO', 'admin@example.com', '관리자', 'ADMIN', true, true, true, true),
     ('user1', '$2a$10$p2QTj1SudPP/G/5PWEc8JOogiss9k0L7hvGAtNpGpmiX9xEGXGRuO', 'user1@example.com', '사용자1', 'USER', true, true, true, true),
     ('user2', '$2a$10$p2QTj1SudPP/G/5PWEc8JOogiss9k0L7hvGAtNpGpmiX9xEGXGRuO', 'user2@example.com', '사용자2', 'USER', true, true, true, true);

-- 게시글 데이터 (author_id는 위에서 생성된 사용자들의 ID를 참조)
INSERT INTO article (title, content, author_id, created_at, view_count) VALUES
    ('첫 번째 게시글', '안녕하세요! 첫 번째 게시글입니다. Spring Boot로 만든 게시판에 오신 것을 환영합니다!', 1, CURRENT_TIMESTAMP, 0),
    ('Spring Boot 학습 후기', 'Spring Boot를 학습하면서 느낀 점들을 공유해보려고 합니다. 정말 편리한 프레임워크네요!', 2, CURRENT_TIMESTAMP, 0),
    ('JPA와 데이터베이스', 'JPA를 사용하면 데이터베이스 작업이 정말 편해집니다. 특히 CRUD 작업이 간단해지네요.', 2, CURRENT_TIMESTAMP, 0),
    ('웹 개발 팁 공유', '웹 개발을 하면서 유용했던 팁들을 정리해보았습니다. 많은 도움이 되었으면 좋겠네요!', 3, CURRENT_TIMESTAMP, 0),
    ('Spring Security 적용기', 'Spring Security를 프로젝트에 적용해보았는데, 생각보다 복잡하지만 보안에는 확실히 도움이 됩니다.', 1, CURRENT_TIMESTAMP, 0);

-- 댓글 데이터 (기존 Comment 엔티티가 있다면)
INSERT INTO comment (nickname, body, article_id) VALUES
    ('댓글러1', '좋은 글이네요! 감사합니다.', 1),
    ('댓글러2', '도움이 많이 되었습니다!', 1),
    ('댓글러3', '저도 Spring Boot 공부 중인데 참고하겠습니다.', 2);