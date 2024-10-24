package com.redis.jwt.filter;

import com.redis.jwt.exception.GeneralException;
import com.redis.jwt.security.JwtProvider;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.redis.jwt.utils.Global.resolveToken;

@Slf4j
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (StringUtils.isNotBlank(token) && !token.equalsIgnoreCase("null")) {
            switch (jwtProvider.validate(token)) {
                case EXPIRED -> request.setAttribute("error", "EXPIRED");
                case INVALID -> request.setAttribute("error", "INVALID");
                case ERROR -> request.setAttribute("error", "ERROR");
            }
        }
        filterChain.doFilter(request, response);
    }
}
