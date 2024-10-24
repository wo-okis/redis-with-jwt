package com.redis.jwt.constrant;

import com.redis.jwt.exception.BasicException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum BasicError {

    //    SC_FORBIDDEN 403, SC_UNAUTHORIZED 401
    METHOD_ARG_ERROR(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, "올바른 값을 입력해주세요."),
    SC_UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스 입니다. 로그인 후 이용해주세요."),
    SC_FORBIDDEN_ERROR(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN, "접근 권한이 없습니다. 관리자에게 문의해주세요."),
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, "페이지를 찾을 수 없습니다."),
    NOT_ALLOWED_ERROR( HttpStatus.METHOD_NOT_ALLOWED.value(), HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메소드입니다."),
    DATA_BASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, "데이터 저장 중 오류가 발생했습니다. 관리자에게 문의해주세요."),

    INTERNAL_SERVER_ERROR(9999,  HttpStatus.INTERNAL_SERVER_ERROR,"서버에서 오류가 발생했습니다.\n 다시 시도해주세요.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;


    public static BasicError valueOf(Integer errorCode) {
        if (errorCode == null) throw new BasicException(INTERNAL_SERVER_ERROR, "ErrorCode is required.");

        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(errorCode))
                .findFirst()
                .orElse(INTERNAL_SERVER_ERROR);
    }

    public String getMessage(Throwable e) {
        return this.getMessage(this.getMessage() + " - " + e.getMessage());
    }

    public String getMessage(String message) {
        return Optional.ofNullable(message)
                .filter(Predicate.not(String::isBlank))
                .orElse(this.getMessage());
    }
}
