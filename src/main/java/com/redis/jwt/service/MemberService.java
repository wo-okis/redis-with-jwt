package com.redis.jwt.service;

import com.redis.jwt.constrant.ErrorCode;
import com.redis.jwt.domain.Member;
import com.redis.jwt.dto.MemberDto;
import com.redis.jwt.dto.TokenDto;
import com.redis.jwt.exception.GeneralException;
import com.redis.jwt.repository.MemberRepository;
import com.redis.jwt.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public void join(MemberDto memberDto) {
        memberRepository.findByUsername(memberDto.username())
                        .ifPresentOrElse(
                                m -> {
                                    log.error("(MemberService.join) [회원가입 실패] 이미 존재하는 회원입니다 => {}", memberDto.username());
                                    throw new GeneralException(ErrorCode.USER_ID_ERROR, "이미 존재하는 회원입니다.");
                                },
                                () -> {
                                    memberRepository.save(
                                            Member.builder()
                                                    .username(memberDto.username())
                                                    .password(passwordEncoder.encode(memberDto.password()))
                                                    .role("ROLE_USER")
                                                    .build()
                                    );
                                }
                        );
    }

    public TokenDto login(MemberDto memberDto) {
        Member member = memberRepository.findByUsername(memberDto.username())
                .filter(m -> {
                    if(!passwordEncoder.matches(memberDto.password(), m.getPassword())) {
                        throw new GeneralException(ErrorCode.USER_LOGIN_ERROR, "로그인 정보가 잘못되었습니다.");
                    }
                    return true;
                })
                .orElseThrow(() -> {
                    log.error("(MemberService.login) [로그인 실패] 회원을 찾을 수 없습니다 => {}", memberDto.username());
                    return new GeneralException(ErrorCode.USER_LOGIN_ERROR, "로그인 정보가 잘못되었습니다.");
                });
        return jwtProvider.generateToken(member.getUsername());
    }

}
