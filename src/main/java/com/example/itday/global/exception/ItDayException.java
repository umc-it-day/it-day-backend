package com.example.itday.global.exception;

import lombok.Getter;

@Getter
public class ItDayException extends RuntimeException {

    private final ErrorCode errorCode;

    public ItDayException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ItDayException(
            ErrorCode errorCode,
            String message
    ) {
        super(message);
        this.errorCode = errorCode;
    }

    public ItDayException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
