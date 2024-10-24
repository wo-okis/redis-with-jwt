package com.redis.jwt.security;

import com.redis.jwt.constrant.ErrorCode;
import com.redis.jwt.constrant.JwtValid;
import com.redis.jwt.dto.PrincipalDetails;
import com.redis.jwt.dto.TokenDto;
import com.redis.jwt.exception.GeneralException;
import com.redis.jwt.service.RedisJwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static io.micrometer.common.util.StringUtils.isBlank;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider implements InitializingBean {

    @Value("${spring.jwt.secret}")
    private String secret;

    @Value("${spring.jwt.accessExpired}")
    private long accessExpired;

    @Value("${spring.jwt.refreshExpired}")
    private long refreshExpired;

    private final UserDetailsService userDetailsService;
    private final RedisJwtService redisJwtService;
    private static final int KEY_SIZE = 256;
    private static final String TAG = "refresh:";
    private Key key;

    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = new byte[KEY_SIZE / 8];
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(secretBytes, 0, keyBytes, 0, Math.min(secretBytes.length, keyBytes.length));
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateToken(String username) {
        String redisKey = TAG + username;

        redisJwtService.deleteToken(redisKey);

        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        long nowMillis = now.getTime();
        long accessExpiredTime = nowMillis + accessExpired;
        long refreshExpiredTime = nowMillis + refreshExpired;

        String accessToken = Jwts.builder()
                .setClaims(Jwts.claims().setSubject(username))
                .setIssuedAt(now)
                .setExpiration(new Date(accessExpiredTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(new Date(refreshExpiredTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        redisJwtService.saveToken(redisKey, accessToken, refreshToken, refreshExpired);

        return new TokenDto(
                accessToken,
                toLocalDateTime(accessExpiredTime),
                refreshToken,
                toLocalDateTime(refreshExpiredTime)
        );
    }


    public JwtValid validate(String token) {
        try{
            String username = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody().getSubject();

            if (isBlank(username)) {
                log.error("(JwtTokenProvider.getAuthentication) [토큰 검증 실패] 사용자 정보가 일치하지 않습니다");
                throw new GeneralException(ErrorCode.USER_ID_ERROR, "사용자 정보가 일치하지 않습니다. 다시 확인해주세요.");
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String redisKey = TAG + username;
            String accessToken = redisJwtService.getAccessToken(redisKey);
            if (!accessToken.equals(token)) {
                throw new GeneralException(ErrorCode.TOKEN_INVALID_ERROR, "토큰이 일치하지 않습니다.");
            }

            //인증 정보 저장
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return JwtValid.SUCCESS;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException | GeneralException e) {
            log.error("(JwtTokenProvider.validateToken) [토큰 유효성 검증] 잘못된 토큰입니다.");
            return JwtValid.INVALID;
        } catch (ExpiredJwtException e) {
            log.error("(JwtTokenProvider.validateToken) [토큰 유효성 검증] 만료된 토큰입니다.");
            return JwtValid.EXPIRED;
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            log.error("(JwtTokenProvider.validateToken) [토큰 유효성 검증] 토큰 오류 발생");
            return JwtValid.ERROR;
        }
    }
    
    public TokenDto refresh(String username, String refreshToken) {
        String redisKey = TAG + username;
        String storedRefreshToken  = redisJwtService.getRefreshToken(redisKey);
        if (!refreshToken.equals(storedRefreshToken)) {
            log.error("(JwtTokenProvider.validateAccessToken) [토큰 검증 실패] 사용자의 리프레시 토큰 정보가 일치하지 않습니다");
            throw new GeneralException(ErrorCode.TOKEN_INVALID_ERROR, "사용자 정보가 일치하지 않습니다. 다시 확인해주세요.");
        }

        return generateToken(username);
    }

    public void logout(String username) {
        String redisKey = TAG + username;
        redisJwtService.deleteToken(redisKey);
    }


    // Date -> LocalDateTime 변환 메서드
    private LocalDateTime toLocalDateTime(long millis) {
        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
