package com.redis.jwt.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

public class Global {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    public static String resolveToken(HttpServletRequest request) {
        //Header에 담겨있는 AccessToken을 기준으로 조회
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
