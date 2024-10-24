package com.redis.jwt.controller.advice;

import com.redis.jwt.constrant.ErrorCode;
import com.redis.jwt.dto.api.ApiErrorResponse;
import com.redis.jwt.exception.GeneralException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice("com.redis.jwt.controller")
public class ExceptionHandlerAdvice {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiErrorResponse> generalExHandler(GeneralException ex, HttpServletRequest request) {
        log.error("[General Exception] e -> {}",ex.getMessage());
        return new ResponseEntity<>(
                ApiErrorResponse.of(false, ex.getErrorCode(), ex.getMessage()),
                ex.getErrorCode().getHttpStatus()
        );
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiErrorResponse> numberFormatExHandler(NumberFormatException ex) {
        log.error("[Number Format Exception] e -> ",ex);
        return new ResponseEntity<>(
                ApiErrorResponse.of(false, ErrorCode.DATA_INVALID_ERROR, "Invalid Number Format"),
                ErrorCode.DATA_INVALID_ERROR.getHttpStatus()
        );
    }

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ApiErrorResponse> arithmeticExHandler(ArithmeticException ex) {
        log.error("[Arithmetic Exception] e -> ",ex);
        return new ResponseEntity<>(
                ApiErrorResponse.of(false, ErrorCode.DATA_INVALID_ERROR, "Maximum value exceeded"),
                ErrorCode.DATA_INVALID_ERROR.getHttpStatus()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> runtimeExHandler(RuntimeException ex) {
        log.error("[Runtime Exception] e -> ",ex);

        if (ex instanceof GeneralException generalException) {
            return new ResponseEntity<>(
                    ApiErrorResponse.of(false, generalException.getErrorCode(), generalException.getMessage()),
                    generalException.getErrorCode().getHttpStatus()
            );
        }

        return new ResponseEntity<>(
                ApiErrorResponse.of(false, ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.getMessage()),
                ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus()
        );
    }
}
