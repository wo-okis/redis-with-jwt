package com.redis.jwt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.jwt.constrant.BasicError;
import com.redis.jwt.constrant.ErrorCode;
import com.redis.jwt.dto.api.ApiErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ApiErrorController implements ErrorController {

    private final ObjectMapper mapper;     // JSON 변환을 위한 ObjectMapper

    @RequestMapping("/error")
    public ResponseEntity<String>  error(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Throwable ex = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        ApiErrorResponse apiErrorResponse;

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == 404) {
                log.error("HTTP 오류 - 404 [NoHandlerFoundException] -> ", ex);
                apiErrorResponse = ApiErrorResponse.of(
                        false,
                        BasicError.NOT_FOUND_ERROR,
                        BasicError.NOT_FOUND_ERROR.getMessage()
                );
                return createEncryptedResponse(apiErrorResponse, BasicError.NOT_FOUND_ERROR.getHttpStatus());
            }
        }

        log.error("HTTP 오류 - 500 [InternalServerError] -> ", ex);
        apiErrorResponse = ApiErrorResponse.of(
                false,
                ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
        );
        return createEncryptedResponse(apiErrorResponse, ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus());
    }

    private ResponseEntity<String> createEncryptedResponse(ApiErrorResponse apiErrorResponse, HttpStatus status) {
        try {
            // JSON으로 변환
            String jsonResponse = mapper.writeValueAsString(apiErrorResponse);
            // 암호화된 데이터를 반환
            return ResponseEntity.status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);
        } catch (JsonProcessingException e) {
            log.error("(ApiErrorController.createEncryptedResponse) [JSON 파싱 오류]", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
