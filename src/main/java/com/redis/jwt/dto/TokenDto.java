package com.redis.jwt.dto;

import java.time.LocalDateTime;

public record TokenDto(String accessToken,
                       LocalDateTime accessTokenExpired,
                       String refreshToken,
                       LocalDateTime refreshTokenExpired) {
}
