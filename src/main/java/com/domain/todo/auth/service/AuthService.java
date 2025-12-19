package com.domain.todo.auth.service;

import com.domain.todo.auth.mapper.AuthMapper;
import com.domain.todo.exception.ApiException;
import com.domain.todo.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthMapper authMapper;

    public Map<String,Object> getCurrentUser(String provider,String providerId){
        if (providerId == null) {
            throw new ApiException(ExceptionCode.AUTH_REQUIRED);
        }

        Map<String, Object> user = authMapper.findByProviderAndId(provider, providerId);
        if(user == null){
            throw new ApiException(ExceptionCode.USER_NOT_FOUND);
        }
        return user;
    }


    public Map<String,Object> syncAuthUser(String provider,String providerId, String email, String displayName){
        Map<String, Object> existingUser = authMapper.findByProviderAndId(provider, providerId);
        if (existingUser != null && !existingUser.isEmpty()) {
            return existingUser;
        }

        String userId = generateUserId(provider, providerId);
        Map<String, Object> param= Map.of(
                "user_id",userId,
                "email", email,
                "display_name", displayName != null ? displayName : "USER",
                "provider", provider,
                "provider_id", providerId,
                "created_by","SYSTEM"
        );

        int result = authMapper.insertUser(param);
        if (result == 0) {
            throw new ApiException(ExceptionCode.USER_REGISTRATION_FAILED);
        }

        return param;
    }

    private String generateUserId(String provider, String providerId) {
        String userId = provider + "_" + providerId;
        if (userId.length() > 50) {
            userId = userId.substring(0, 50);
        }
        return userId;
    }
}
