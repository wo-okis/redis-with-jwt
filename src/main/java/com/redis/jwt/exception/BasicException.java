package com.redis.jwt.exception;

import com.redis.jwt.constrant.BasicError;
import lombok.Getter;

@Getter
public class BasicException extends RuntimeException{

    private final BasicError errorCode;

    public BasicException(BasicError errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BasicException(BasicError errorCode, String message) {
        super(errorCode.getMessage(message));
        this.errorCode = errorCode;
    }

    public BasicException(BasicError errorCode, String message, Throwable cause) {
        super(errorCode.getMessage(message), cause);
        this.errorCode = errorCode;
    }

    public BasicException(BasicError errorCode, Throwable cause) {
        super(errorCode.getMessage(cause));
        this.errorCode = errorCode;
    }
}
