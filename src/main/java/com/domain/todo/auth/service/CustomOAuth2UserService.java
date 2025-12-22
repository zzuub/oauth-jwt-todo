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

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google or kakao
        System.out.println("registrationId = " + registrationId);

        String provider   = registrationId;
        String providerId;
        String email;
        String name;

        // 1) provider별로 프로필에서 값 꺼내기
        if ("google".equals(registrationId)) {

            // 구글은 sub / email / name 이 루트에 있음
            providerId = oAuth2User.getAttribute("sub");
            email      = oAuth2User.getAttribute("email");
            name       = oAuth2User.getAttribute("name");

        } else if ("kakao".equals(registrationId)) {
            // id 는 Long 이라서 무조건 문자열로 변환해서 사용
            Object idObj = oAuth2User.getAttribute("id");
            providerId   = (idObj != null) ? String.valueOf(idObj) : null;

            Object accountObj = oAuth2User.getAttribute("kakao_account");
            Map<String, Object> account = (accountObj instanceof Map)
                    ? (Map<String, Object>) accountObj
                    : null;

            Map<String, Object> profile = null;
            if (account != null) {
                Object profileObj = account.get("profile");
                if (profileObj instanceof Map) {
                    profile = (Map<String, Object>) profileObj;
                }
            }

            // email 은 선택 동의라 null 일 수 있음
            email = account != null ? (String) account.get("email") : null;
            name  = profile != null ? (String) profile.get("nickname") : null;

        } else {
            // 혹시 나중에 다른 provider 추가할 때 기본 처리
            Object idObj = oAuth2User.getAttribute("id");
            providerId   = (idObj != null) ? String.valueOf(idObj) : null;
            email        = oAuth2User.getAttribute("email");
            name         = oAuth2User.getAttribute("name");
        }

        // 2) DB 동기화
        authService.syncAuthUser(provider, providerId, email, name);

        // 3) 세션에 넣어줄 attributes 확장 (provider / providerId 같이 넣어둠)
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("provider",   provider);
        attributes.put("providerId", providerId);

        // 4) principal name 으로 쓸 key (구글: sub, 카카오: id)
        String nameAttributeKey = "sub";
        if ("kakao".equals(registrationId)) {
            nameAttributeKey = "id";
        }

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                nameAttributeKey
        );
    }
}
