package com.domain.todo.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final AuthService authService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "google"
        String provider = registrationId.toUpperCase(); // GOOGLE

        String providerId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 🔹 여기서 DB 저장/조회
        authService.syncAuthUser(provider, providerId, email, name);

        // ⭐ provider를 OAuth2User에 추가!
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("provider", registrationId);

        DefaultOAuth2User userWithProvider = new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "sub"
        );
        return userWithProvider;
    }
}