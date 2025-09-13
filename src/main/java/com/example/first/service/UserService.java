package com.example.first.service;

import com.example.first.dto.UserRegistrationDto;
import com.example.first.entity.User;
import com.example.first.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // @Lazy 어노테이션을 사용하여 순환 참조 방지
    @Lazy
    private final PasswordEncoder passwordEncoder;

    /**
     * Spring Security에서 사용자 인증을 위해 호출하는 메서드
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("사용자 인증 시도: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("사용자를 찾을 수 없습니다: {}", username);
                    return new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
                });

        // 디버깅을 위한 상세 로그
        log.info("사용자 조회 성공: {}", username);
        log.info("사용자 ID: {}", user.getId());
        log.info("사용자 이메일: {}", user.getEmail());
        log.info("사용자 닉네임: {}", user.getNickname());
        log.info("사용자 역할: {}", user.getRole());
        log.info("계정 활성화: {}", user.isEnabled());
        log.info("계정 만료되지 않음: {}", user.isAccountNonExpired());
        log.info("계정 잠기지 않음: {}", user.isAccountNonLocked());
        log.info("자격증명 만료되지 않음: {}", user.isCredentialsNonExpired());
        log.info("저장된 비밀번호 해시 (앞 20자): {}",
                user.getPassword() != null ? user.getPassword().substring(0, Math.min(20, user.getPassword().length())) + "..." : "null");

        // 권한 정보 로그
        log.info("사용자 권한: {}", user.getAuthorities());

        return user;
    }

    /**
     * 비밀번호 검증 테스트 메서드 (디버깅용)
     */
    public boolean testPasswordMatch(String username, String rawPassword) {
        try {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null) {
                log.error("사용자를 찾을 수 없음: {}", username);
                return false;
            }

            boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
            log.info("비밀번호 검증 테스트 - 사용자: {}, 입력된 비밀번호: {}, 일치 여부: {}",
                    username, rawPassword, matches);

            return matches;
        } catch (Exception e) {
            log.error("비밀번호 검증 중 오류 발생", e);
            return false;
        }
    }

    /**
     * 회원가입 처리
     */
    @Transactional
    public User registerUser(UserRegistrationDto registrationDto) {
        log.info("회원가입 시도: {}", registrationDto.getUsername());

        // 중복 검사
        validateUserRegistration(registrationDto);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(registrationDto.getPassword());
        log.info("회원가입 시 암호화된 비밀번호 (앞 20자): {}", encodedPassword.substring(0, 20) + "...");

        // User 엔티티 생성
        User user = new User(
                registrationDto.getUsername(),
                encodedPassword,
                registrationDto.getEmail(),
                registrationDto.getNickname()
        );

        User savedUser = userRepository.save(user);
        log.info("회원가입 완료: {}", savedUser.getUsername());

        return savedUser;
    }

    /**
     * 회원가입 시 중복 검사
     */
    private void validateUserRegistration(UserRegistrationDto registrationDto) {
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (userRepository.existsByNickname(registrationDto.getNickname())) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
    }

    /**
     * 사용자 ID로 사용자 조회
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id));
    }

    /**
     * 사용자명으로 사용자 조회
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + username));
    }

    /**
     * 사용자명 중복 확인
     */
    public boolean isUsernameAvailable(String username) {
        return !userRepository.existsByUsername(username);
    }

    /**
     * 이메일 중복 확인
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * 닉네임 중복 확인
     */
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }
}