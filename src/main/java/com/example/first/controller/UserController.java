package com.example.first.controller;

import com.example.first.dto.UserRegistrationDto;
import com.example.first.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * 홈 페이지 (로그인 전 랜딩 페이지)
     */
    @GetMapping({"/", "/home"})
    public String homePage() {
        return "home";
    }

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "사용자명 또는 비밀번호가 잘못되었습니다.");
        }

        if (logout != null) {
            model.addAttribute("logoutMessage", "성공적으로 로그아웃되었습니다.");
        }

        return "users/login";
    }

    /**
     * 회원가입 페이지
     */
    @GetMapping("/users/register")
    public String registerPage(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "users/register";
    }

    /**
     * 회원가입 처리
     */
    @PostMapping("/users/register")
    public String registerUser(@Valid @ModelAttribute UserRegistrationDto userRegistrationDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        log.info("회원가입 요청: {}", userRegistrationDto.getUsername());

        // 유효성 검사 실패
        if (result.hasErrors()) {
            log.error("회원가입 유효성 검사 실패: {}", result.getAllErrors());
            return "users/register";
        }

        // 비밀번호 확인
        if (!userRegistrationDto.isPasswordMatching()) {
            result.rejectValue("passwordConfirm", "error.passwordConfirm",
                    "비밀번호가 일치하지 않습니다.");
            return "users/register";
        }

        try {
            // 회원가입 처리
            userService.registerUser(userRegistrationDto);

            log.info("회원가입 성공: {}", userRegistrationDto.getUsername());
            redirectAttributes.addFlashAttribute("successMessage",
                    "회원가입이 완료되었습니다. 로그인해주세요.");

            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            log.error("회원가입 실패: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            return "users/register";
        }
    }

}