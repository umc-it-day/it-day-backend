package com.example.itday.domain.barcode.exception;

import com.example.itday.global.apiPayload.code.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateBarcodeException extends RuntimeException {

    private final ErrorCode errorCode;

    public DuplicateBarcodeException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
