# Spring Boot Study Project

## 📋 프로젝트 개요
Spring Boot 학습을 위한 기본적인 게시판 애플리케이션입니다. 
Spring Boot의 핵심 기능들을 익히고 웹 애플리케이션의 기본적인 CRUD 기능을 구현하는 것이 목표입니다.

## 🛠 기술 스택
- **Java**: 21
- **Spring Boot**: 3.5.5
- **Spring Data JPA**: 데이터베이스 접근
- **H2 Database**: 인메모리 데이터베이스
- **Mustache**: 템플릿 엔진
- **Lombok**: 보일러플레이트 코드 자동 생성
- **Gradle**: 빌드 도구

## 📁 프로젝트 구조
```
src/main/java/com/example/first/
├── FirstApplication.java          # 메인 애플리케이션 클래스
├── controller/                    # 컨트롤러 레이어
│   ├── ArticleController.java    # 게시글 관련 컨트롤러
│   └── FirstController.java      # 기본 컨트롤러
├── entity/                        # 엔티티 클래스
│   ├── Article.java              # 게시글 엔티티
│   └── Comment.java              # 댓글 엔티티
├── dto/                          # 데이터 전송 객체
├── repository/                   # 데이터 접근 레이어
├── service/                      # 서비스 레이어
└── api/                          # REST API 컨트롤러

src/main/resources/
├── application.properties        # 애플리케이션 설정
├── data.sql                     # 초기 데이터
├── templates/                   # Mustache 템플릿
└── static/                      # 정적 리소스
```

## 🚀 주요 기능
### Article (게시글) 관리
- **생성**: 새 게시글 작성
- **조회**: 게시글 목록 보기 및 상세 조회
- **수정**: 기존 게시글 편집
- **삭제**: 게시글 삭제

### 기술적 특징
- **MVC 패턴**: 계층화된 아키텍처 적용
- **JPA**: 객체-관계 매핑으로 데이터베이스 연동
- **H2 Database**: 개발 및 테스트용 인메모리 데이터베이스
- **Mustache**: 서버사이드 템플릿 렌더링
- **Lombok**: 코드 간소화 및 가독성 향상

## 🏃‍♂️ 실행 방법

### 1. 필요 사항
- Java 21 이상
- Git

### 2. 프로젝트 클론
```bash
git clone https://github.com/SongeunB/springboot.git
cd springboot
```

### 3. 애플리케이션 실행
```bash
# Windows
./gradlew.bat bootRun

# macOS/Linux
./gradlew bootRun
```

### 4. 애플리케이션 접속
브라우저에서 `http://localhost:8080` 접속

## 🌐 주요 엔드포인트
- `GET /articles/new` - 새 게시글 작성 폼
- `POST /articles/create` - 게시글 생성
- `GET /articles` - 전체 게시글 목록
- `GET /articles/{id}` - 특정 게시글 상세 조회
- `GET /articles/{id}/edit` - 게시글 수정 폼
- `POST /articles/update` - 게시글 업데이트
- `GET /articles/{id}/delete` - 게시글 삭제

## 💾 데이터베이스
- **타입**: H2 (인메모리)
- **접속**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **사용자명**: `sa`
- **비밀번호**: (없음)

## 📚 학습 목표
- Spring Boot 프로젝트 구조 이해
- MVC 패턴 적용
- Spring Data JPA를 이용한 데이터 처리
- 템플릿 엔진(Mustache) 사용
- RESTful API 설계 원칙 적용
- 기본적인 웹 애플리케이션 CRUD 구현

## 🔧 개발 환경
- **IDE**: IntelliJ IDEA / VS Code 권장
- **Java Version**: 21
- **Spring Boot Version**: 3.5.5
- **Build Tool**: Gradle

## 📝 추후 개선 계획
- [ ] 페이징 기능 추가
- [ ] 검색 기능 구현
- [ ] 사용자 인증/인가 기능
- [ ] REST API 확장
- [ ] 프론트엔드 개선 (Bootstrap, JavaScript)
- [ ] 단위 테스트 코드 작성

## 📄 라이선스
이 프로젝트는 학습 목적으로 만들어졌습니다.

---
**Created by**: SongeunB  
**Last Updated**: 2025.09  
**Project Type**: Spring Boot Study Project