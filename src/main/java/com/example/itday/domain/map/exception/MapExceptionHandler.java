package com.example.itday.domain.map.exception;

import com.example.itday.global.apiPayload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MapExceptionHandler {

    @ExceptionHandler(KakaoMapApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleKakaoMapApiException(
            KakaoMapApiException exception
    ) {
        ApiResponse<Void> response = ApiResponse.onFailure(exception.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(StoreNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleStoreNotFoundException(
            StoreNotFoundException exception
    ) {
        ApiResponse<Void> response = ApiResponse.onFailure(exception.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
