package com.redis.jwt.controller;

import com.redis.jwt.dto.api.ApiDataResponse;
import com.redis.jwt.dto.MemberDto;
import com.redis.jwt.dto.TokenDto;
import com.redis.jwt.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;

    @PostMapping("/join")
    public ApiDataResponse join(@Validated @RequestBody MemberDto memberDto) {
        memberService.join(memberDto);
        return ApiDataResponse.empty();
    }

    @PostMapping("/login")
    public ApiDataResponse<TokenDto> login(@Validated @RequestBody MemberDto memberDto) {
        TokenDto login = memberService.login(memberDto);
        return ApiDataResponse.of(login);
    }

}
