package com.redis.jwt.constrant;

import com.redis.jwt.exception.GeneralException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    OK(200,  HttpStatus.OK,"OK"),

    //GENERAL
    DATA_INVALID_ERROR(1001, HttpStatus.OK, "올바른 값을 입력해주세요."),
    DATA_NOT_FOUND(1002, HttpStatus.OK, "입력 값을 찾을 수 없습니다."),
    DATA_DUPLICATE_ERROR(1003, HttpStatus.OK, "이미 존재하는 값입니다."),
    DATA_PERMISSION_ERROR(1004, HttpStatus.OK, "권한이 없습니다."),

    //Client User Login  Error Code
    USER_LOGIN_ERROR(2001, HttpStatus.OK, "로그인 실패. 다시 시도해주세요."),
    USER_ID_ERROR(2002, HttpStatus.OK, "아이디가 일치하지 않습니다. 다시 확인해주세요."),
    USER_PASSWORD_ERROR(2003, HttpStatus.OK, "비밀번호가 일치하지 않습니다. 다시 확인해주세요."),
    USER_LOGIN_COUNT_ERROR(2004, HttpStatus.OK, "로그인 횟수 초과. 관리자에게 문의해주세요."),
    USER_DISABLED_ERROR(2005, HttpStatus.OK, "로그인 할 수 없는 계정입니다. 관리자에게 문의해주세요."),
    USER_WITHDRAW_ERROR(2006, HttpStatus.OK, "탈퇴 신청된 계정입니다. 관리자에게 문의해주세요."),
    USER_PERMISSION_ERROR(2007, HttpStatus.OK, "사용자 권한이 없습니다. 관리자에게 문의해주세요."),


    //Security Error
    TOKEN_INVALID_ERROR(3001, HttpStatus.OK, "유효하지 않은 값입니다. 다시 로그인해주세요."),
    TOKEN_UNAUTHORIZED_ERROR(3002, HttpStatus.OK, "인증되지 않은 사용자 입니다. 다시 로그인해주세요."),
    TOKEN_FORBIDDEN_ERROR(3003, HttpStatus.OK, "권한이 없습니다. 다시 확인해주세요."),
    TOKEN_EXPIRED_ERROR(3004, HttpStatus.OK, "로그인 정보가 만료되었습니다. 다시 로그인해주세요."),

    //Encrypt Error
    ENCRYPT_ERROR(4001, HttpStatus.OK, "암호화 중 오류가 발생했습니다."),
    DECRYPT_ERROR(4002, HttpStatus.OK, "복호화 중 오류가 발생했습니다."),

    //SMS ERROR
    SMS_SEND_TIME_ERROR(5001, HttpStatus.OK, "1분 후 다시 시도해주세요."),
    SMS_SEND_COUNT_ERROR(5002, HttpStatus.OK, "인증 횟수 초과. 1일 5회까지 가능합니다."),
    SMS_VERIFY_TIME_ERROR(5003, HttpStatus.OK, "인증 시간 초과. 다시 시도해주세요."),
    SMS_CODE_MISMATCH_ERROR(5004, HttpStatus.OK, "인증번호가 일치하지 않습니다. 다시 확인해주세요."),

    //IAMPORT API ERROR
    IAMPORT_400_ERROR(8001, HttpStatus.OK, "요청이 잘못되었습니다. 입력하신 정보를 다시 확인해주세요."),
    IAMPORT_401_ERROR(8002, HttpStatus.OK, "인증이 실패했습니다. 올바른 인증 정보를 입력해주세요."),
    IAMPORT_404_ERROR(8003, HttpStatus.OK, "계좌 정보를 찾을 수 없습니다. 다시 확인해주세요."),

    //IP ACCESS ERROR
    IP_ACCESS_ERROR(9991, HttpStatus.OK, "접근이 허용되지 않은 IP입니다. 관리자에게 문의해주세요."),
    //Server Error
    INTERNAL_SERVER_ERROR(9999,  HttpStatus.INTERNAL_SERVER_ERROR,"서버에서 오류가 발생했습니다.\n 다시 시도해주세요.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;

    public static ErrorCode valueOf(Integer errorCode) {
        if (errorCode == null) throw new GeneralException(DATA_INVALID_ERROR, "ErrorCode is required.");

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
