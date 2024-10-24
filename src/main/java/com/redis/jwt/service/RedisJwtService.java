package com.redis.jwt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RedisJwtService {

    private final RedisTemplate<String, String> redisTemplate;

    public void saveToken(String key, String accessToken, String refreshToken, Long expiredTime) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        ops.put(key, "accessToken", accessToken);
        ops.put(key, "refreshToken", refreshToken);
        redisTemplate.expire(key, expiredTime, TimeUnit.MILLISECONDS);
    }

    public void deleteToken(String key) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
        }
    }

    public String getAccessToken(String key) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        return ops.get(key, "accessToken");
    }

    public String getRefreshToken(String key) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        return ops.get(key, "refreshToken");
    }
}
