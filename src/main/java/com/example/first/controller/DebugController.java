package com.example.first.controller;

import com.example.first.entity.User;
import com.example.first.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 디버깅용 컨트롤러 (운영환경에서는 제거해야 함)
 */
@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
@Slf4j
public class DebugController {

    private final UserService userService;

    @Lazy
    private final PasswordEncoder passwordEncoder;

    /**
     * 사용자 정보 확인
     */
    @GetMapping("/user-info")
    public Map<String, Object> getUserInfo(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();

        try {
            User user = userService.findByUsername(username);
            result.put("success", true);
            result.put("username", user.getUsername());
            result.put("email", user.getEmail());
            result.put("nickname", user.getNickname());
            result.put("role", user.getRole());
            result.put("enabled", user.isEnabled());
            result.put("accountNonExpired", user.isAccountNonExpired());
            result.put("accountNonLocked", user.isAccountNonLocked());
            result.put("credentialsNonExpired", user.isCredentialsNonExpired());
            result.put("passwordHash", user.getPassword().substring(0, 20) + "...");
            result.put("authorities", user.getAuthorities());

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            log.error("사용자 정보 조회 실패: {}", username, e);
        }

        return result;
    }

    /**
     * 비밀번호 검증 테스트
     */
    @GetMapping("/test-password")
    public Map<String, Object> testPassword(@RequestParam String username,
                                            @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        try {
            User user = userService.findByUsername(username);
            boolean matches = passwordEncoder.matches(password, user.getPassword());

            result.put("success", true);
            result.put("username", username);
            result.put("inputPassword", password);
            result.put("matches", matches);
            result.put("storedHashPrefix", user.getPassword().substring(0, 20) + "...");

            log.info("비밀번호 테스트 - 사용자: {}, 비밀번호: {}, 일치: {}", username, password, matches);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            log.error("비밀번호 테스트 실패: {}", username, e);
        }

        return result;
    }

    /**
     * 새로운 비밀번호 해시 생성
     */
    @GetMapping("/generate-hash")
    public Map<String, Object> generatePasswordHash(@RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        try {
            String hash = passwordEncoder.encode(password);
            result.put("success", true);
            result.put("originalPassword", password);
            result.put("generatedHash", hash);

            // 검증해보기
            boolean matches = passwordEncoder.matches(password, hash);
            result.put("verification", matches);

            log.info("비밀번호 해시 생성 - 원본: {}, 해시: {}, 검증: {}", password, hash, matches);

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            log.error("비밀번호 해시 생성 실패", e);
        }

        return result;
    }

    /**
     * 여러 비밀번호 후보 테스트
     */
    @GetMapping("/test-multiple-passwords")
    public Map<String, Object> testMultiplePasswords(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();

        try {
            User user = userService.findByUsername(username);
            String[] candidates = {"password123", "password", "admin", "123456", "test", "qwerty"};

            Map<String, Boolean> testResults = new HashMap<>();

            for (String candidate : candidates) {
                boolean matches = passwordEncoder.matches(candidate, user.getPassword());
                testResults.put(candidate, matches);
                log.info("비밀번호 후보 테스트 - {}: {}", candidate, matches);
            }

            result.put("success", true);
            result.put("username", username);
            result.put("testResults", testResults);
            result.put("storedHashPrefix", user.getPassword().substring(0, 20) + "...");

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            log.error("다중 비밀번호 테스트 실패: {}", username, e);
        }

        return result;
    }
}