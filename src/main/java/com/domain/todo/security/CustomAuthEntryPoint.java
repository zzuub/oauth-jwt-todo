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

        String uri = request.getRequestURI();

        if (uri.equals("/login.html")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (uri.startsWith("/api/")) {
            HttpSession session = request.getSession(false);

            ExceptionCode code;
            if (session == null) {
                code = ExceptionCode.AUTH_REQUIRED;
            } else if (session.getAttribute("SPRING_SECURITY_CONTEXT") == null) {
                code = ExceptionCode.SESSION_EXPIRED;
            } else {
                code = ExceptionCode.AUTH_REQUIRED;
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
            return;
        }

        response.sendRedirect("/login.html");
    }
}
