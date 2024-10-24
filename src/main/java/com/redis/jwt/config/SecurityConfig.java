package com.redis.jwt.config;

import com.redis.jwt.filter.SecurityFilter;
import com.redis.jwt.security.JwtAccessDeniedHandler;
import com.redis.jwt.security.JwtAuthenticationEntryPoint;
import com.redis.jwt.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //아래의 클래스들은 2번에서 만들 예정
    private final JwtProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * Password 암호화를 위한 Encoder 설정
     */
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain httpFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable) //테스트 용
                .csrf(AbstractHttpConfigurer::disable) //테스트 용
                .headers(AbstractHttpConfigurer::disable) //테스트 용
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) //인증 실패 처리
                        .accessDeniedHandler(jwtAccessDeniedHandler) //접근 거부 처리
                )
                //세션을 사용하지 않는 Stateless 정책 설정
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/auth/**").authenticated() //'/auth/**' 경로는 인증 필요
                        .anyRequest().permitAll() // 그 외 모든 요청은 허용
                )
                .addFilterBefore(
                        new SecurityFilter(jwtTokenProvider), // 사용자 정의 필터
                        UsernamePasswordAuthenticationFilter.class
                );
        return http.build();
    }
}
