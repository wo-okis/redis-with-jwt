package com.redis.jwt.service;

import com.redis.jwt.domain.Member;
import com.redis.jwt.dto.PrincipalDetails;
import com.redis.jwt.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("(UserDetailsServiceImpl.loadUserByUsername) [회원 조회 실패] 회원을 찾을 수 없습니다 => {}", username);
                    return new UsernameNotFoundException(username);
                });
        return new PrincipalDetails(member);
    }
}
