package com.redis.jwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.jwt.dto.api.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper mapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("(JwtAuthenticationEntryPoint) [인증 실패]");

        // 인증 실패 시, 에러 타입을 request에 담아서 전달
        String error = (String) request.getAttribute("error");

        ApiErrorResponse apiErrorResponse;
        int status;

        if (response.getStatus() == HttpStatus.NOT_FOUND.value()) {
            status = HttpStatus.NOT_FOUND.value();
            apiErrorResponse = ApiErrorResponse.of(false, 404, "요청하신 페이지를 찾을 수 없습니다.");
        } else {
            String message = "로그인이 필요한 서비스입니다.";
            if (error != null) {
                switch (error) {
                    case "EXPIRED" ->  message = "로그인 정보가 만료되었습니다. 다시 로그인 해주세요.";
                    case "INVALID" -> message = "로그인 정보가 올바르지 않습니다. 다시 로그인 해주세요.";
                    case "ERROR" -> message = "인증 오류. 다시 로그인 해주세요.";
                }
            }
            status = HttpStatus.OK.value();
            apiErrorResponse = ApiErrorResponse.of(false, 403, message);
        }

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String result = mapper.writeValueAsString(apiErrorResponse);
        response.getWriter().write(result);
    }
}
