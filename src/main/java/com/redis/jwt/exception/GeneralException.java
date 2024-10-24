package com.redis.jwt.exception;

import com.redis.jwt.constrant.ErrorCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException{

    private final ErrorCode errorCode;
    private final Object[] args;

    public GeneralException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        this.args = null;
    }

    public GeneralException(String message) {
        super(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        this.args = null;
    }

    public GeneralException(String message, Throwable cause) {
        super(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(), cause);
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        this.args = null;
    }

    public GeneralException(Throwable cause) {
        super(ErrorCode.INTERNAL_SERVER_ERROR.getMessage(cause));
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        this.args = null;
    }

    public GeneralException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.args = null;
    }

    public GeneralException(ErrorCode errorCode, String message) {
        super(errorCode.getMessage(message));
        this.errorCode = errorCode;
        this.args = null;
    }

    public GeneralException(ErrorCode errorCode, String message, Object[] args) {
        super(errorCode.getMessage(message));
        this.errorCode = errorCode;
        this.args = args;
    }

    public GeneralException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode.getMessage(message), cause);
        this.errorCode = errorCode;
        this.args = null;
    }

    public GeneralException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(cause));
        this.errorCode = errorCode;
        this.args = null;
    }
}
