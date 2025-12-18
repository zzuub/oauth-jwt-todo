package com.domain.todo.auth.controller;

import com.domain.todo.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) {
            return Map.of("success", false, "message", "로그인이 필요합니다.");
        }

        String providerId = principal.getAttribute("sub");
        String provider = "GOOGLE"; // CustomOAuth2UserService에서 동적 처리

        return authService.getCurrentUser(provider, providerId);
    }
}