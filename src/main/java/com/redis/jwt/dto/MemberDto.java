package com.redis.jwt.dto;

import com.redis.jwt.domain.Member;
import jakarta.validation.constraints.NotBlank;

public record MemberDto(@NotBlank String username,
                        @NotBlank String password) {
}
