package com.redis.jwt.controller.advice;

import com.redis.jwt.constrant.BasicError;
import com.redis.jwt.constrant.ErrorCode;
import com.redis.jwt.dto.api.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class DefaultExceptionHandlerAdvice {

    /**
     * 400 MethodArgumentNotValidException Handler
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> methodArgumentNotValidExHandler(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("HTTP 오류 - 400 [MethodArgumentNotValidException] -> ",ex);

        String message = ex.getBindingResult().getAllErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("올바른 값을 입력해주세요.");

        return new ResponseEntity<>(
                ApiErrorResponse.of(false, ErrorCode.DATA_INVALID_ERROR, message),
                ErrorCode.DATA_INVALID_ERROR.getHttpStatus()
        );
    }

    /**
     * 400  HttpMessageNotReadableException Handler
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> httpMessageNotReadableExHandler(HttpMessageNotReadableException ex) {
        log.error("HTTP 오류 - 400 [HttpMessageNotReadableException] -> ",ex);
        return new ResponseEntity<>(
                ApiErrorResponse.of(false, ErrorCode.DATA_INVALID_ERROR, "요청한 데이터를 확인할 수 없습니다. 올바른 값을 입력해주세요."),
                ErrorCode.DATA_INVALID_ERROR.getHttpStatus()
        );
    }

    /**
     * 400  ConstraintViolationException Handler
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> constraintViolationExHandler(ConstraintViolationException ex) {
        log.error("HTTP 오류 - 400 [ConstraintViolationException] -> ",ex);

        //제약 조건 위반 메시지 중 첫 번째 메시지 추출
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("올바른 값을 입력해주세요.");

        return new ResponseEntity<>(
                ApiErrorResponse.of(false, ErrorCode.DATA_INVALID_ERROR, errorMessage),
                ErrorCode.DATA_INVALID_ERROR.getHttpStatus()
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> missingServletRequestParameterExHandler(MissingServletRequestParameterException ex) {
        log.error("HTTP 오류 - 400 [MissingServletRequestParameterException] -> ",ex);
        String errorMessage = String.format("필수 요청 파라미터 '%s'이(가) 누락되었습니다. (요청 파라미터 타입: %s)", ex.getParameterName(), ex.getParameterType());
        return new ResponseEntity<>(
                ApiErrorResponse.of(false, ErrorCode.DATA_INVALID_ERROR, errorMessage),
                ErrorCode.DATA_INVALID_ERROR.getHttpStatus()
        );
    }

    /**
     * 401 Unauthorized Exception Handler
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> unauthorizedExHandler(AuthenticationException ex) {
        log.error("HTTP 오류 - 401 [AuthenticationException] -> ",ex);
        return new ResponseEntity<>(
                ApiErrorResponse.of(false, BasicError.SC_UNAUTHORIZED_ERROR, BasicError.SC_UNAUTHORIZED_ERROR.getMessage()),
                BasicError.SC_UNAUTHORIZED_ERROR.getHttpStatus()
        );
    }

    /**
     * 403 Forbidden Exception Handler
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> accessDeniedExHandler(AccessDeniedException ex) {
        log.error("HTTP 오류 - 403 [AccessDeniedException] -> ",ex);
        return new ResponseEntity<>(
                ApiErrorResponse.of(false, BasicError.SC_FORBIDDEN_ERROR, BasicError.SC_FORBIDDEN_ERROR.getMessage()),
                BasicError.SC_FORBIDDEN_ERROR.getHttpStatus()
        );
    }

    /**
     * 404 Not Found Exception Handler
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> noHandlerFoundExHandler(NoHandlerFoundException ex) {
        log.error("HTTP 오류 - 404 [NoHandlerFoundException] -> ",ex);
        return new ResponseEntity<>(
                ApiErrorResponse.of(false, BasicError.NOT_FOUND_ERROR, BasicError.NOT_FOUND_ERROR.getMessage()),
                BasicError.NOT_FOUND_ERROR.getHttpStatus()
        );
    }

    /**
     * 405 HttpRequestMethodNotSupportedException Handler
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> notAllowedHandlerFoundExHandler(HttpRequestMethodNotSupportedException ex) {
        log.error("HTTP 오류 - 405 [HttpRequestMethodNotSupportedException] -> ",ex);
        return new ResponseEntity<>(
                ApiErrorResponse.of(false, BasicError.NOT_ALLOWED_ERROR, BasicError.NOT_ALLOWED_ERROR.getMessage()),
                BasicError.NOT_ALLOWED_ERROR.getHttpStatus()
        );
    }

    /**
     * Data Access Exception Handler
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> dataAccessExHandler(DataAccessException ex) {
        log.error("데이터베이스 오류 - [DataAccessException] -> ",ex);
        return new ResponseEntity<>(
                ApiErrorResponse.of(false, BasicError.DATA_BASE_ERROR, BasicError.DATA_BASE_ERROR.getMessage()),
                BasicError.DATA_BASE_ERROR.getHttpStatus()
        );
    }
}
