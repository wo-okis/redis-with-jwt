package com.redis.jwt.controller;

import com.redis.jwt.dto.api.ApiDataResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @RequestMapping("/test")
    public ApiDataResponse<String> test() {
        return ApiDataResponse.of("test");
    }

}
