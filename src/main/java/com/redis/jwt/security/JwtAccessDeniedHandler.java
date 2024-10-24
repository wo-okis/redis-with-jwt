package com.redis.jwt.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.jwt.constrant.BasicError;
import com.redis.jwt.dto.api.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("(JwtAccessDeniedHandler) [엑세스 거부] 접근 권한이 없는 사용자가 접근하였습니다");
        ApiErrorResponse apiErrorResponse = ApiErrorResponse.of(false, BasicError.SC_FORBIDDEN_ERROR, "접근 권한이 없습니다. 관리자에게 문의해주세요.");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String result = mapper.writeValueAsString(apiErrorResponse);
        response.getWriter().write(result);
    }
}
