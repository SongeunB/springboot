package com.example.first.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 로그인 성공 핸들러
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            log.info("=== 로그인 성공 ===");
            log.info("사용자: {}", authentication.getName());
            log.info("권한: {}", authentication.getAuthorities());
            log.info("Principal: {}", authentication.getPrincipal());
            log.info("리다이렉트 URL: /articles");

            response.sendRedirect("/articles");
        };
    }

    /**
     * 로그인 실패 핸들러
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            log.error("=== 로그인 실패 ===");
            log.error("사용자명: {}", request.getParameter("username"));
            log.error("실패 원인: {}", exception.getMessage());
            log.error("예외 타입: {}", exception.getClass().getSimpleName());

            if (exception.getCause() != null) {
                log.error("상세 원인: {}", exception.getCause().getMessage());
            }

            response.sendRedirect("/login?error=true");
        };
    }

    /**
     * Spring Security 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 (개발 단계에서는 비활성화)
                .csrf(csrf -> csrf.disable())

                // URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스와 공개 페이지는 인증 없이 접근 가능
                        .requestMatchers("/", "/home", "/login", "/register",
                                "/css/**", "/js/**", "/images/**",
                                "/debug/**").permitAll()  // 디버깅 URL 추가
                        // H2 콘솔 접근 허용 (개발용)
                        .requestMatchers("/h2-console/**").permitAll()
                        // API 엔드포인트 중 공개적으로 접근 가능한 것들
                        .requestMatchers("/api/articles/search").permitAll()
                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // 폼 로그인 설정
                .formLogin(form -> form
                        .loginPage("/login")              // 로그인 페이지 URL
                        .loginProcessingUrl("/login")     // 로그인 폼 제출 URL
                        .usernameParameter("username")    // 사용자명 파라미터명
                        .passwordParameter("password")    // 비밀번호 파라미터명
                        .successHandler(authenticationSuccessHandler())  // 성공 핸들러
                        .failureHandler(authenticationFailureHandler())  // 실패 핸들러
                        .permitAll()
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/logout")             // 로그아웃 URL
                        .logoutSuccessUrl("/login?logout=true")  // 로그아웃 성공 시 리다이렉트 URL
                        .invalidateHttpSession(true)      // 세션 무효화
                        .deleteCookies("JSESSIONID")      // 쿠키 삭제
                        .permitAll()
                )

                // H2 콘솔을 위한 설정 (개발용)
                .headers(headers -> headers.frameOptions().disable());

        return http.build();
    }

    /**
     * AuthenticationManager 빈 등록
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}