package com.example.itday.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private boolean success;
    private String code;
    private String message;
    private LocalDateTime timestamp;
    private List<FieldErrorDetail> errors;

    @Getter
    @Builder
    public static class FieldErrorDetail {

        private String field;
        private String rejectedValue;
        private String message;
    }
}
