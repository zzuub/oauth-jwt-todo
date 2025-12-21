package com.domain.todo.security;

import com.domain.todo.exception.ExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        HttpSession session = request.getSession(false); // 세션 새로 생성 안 함

        ExceptionCode code;
        if (session == null) {
            // 처음 방문 (세션 자체가 없음)
            code = ExceptionCode.AUTH_REQUIRED; // AUTH_002
        } else if (session.getAttribute("SPRING_SECURITY_CONTEXT") == null) {
            // 세션은 있는데 인증 정보가 없음 = 세션 만료
            code = ExceptionCode.SESSION_EXPIRED; // AUTH_005
        } else {
            // 기타 (혹시 모를 경우)
            code = ExceptionCode.AUTH_REQUIRED; // AUTH_002
        }

        response.setStatus(code.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        String body = """
            {
              "success": false,
              "code": "%s",
              "message": "%s"
            }
            """.formatted(code.getCode(), code.getMessage());

        response.getWriter().write(body);
    }
}
