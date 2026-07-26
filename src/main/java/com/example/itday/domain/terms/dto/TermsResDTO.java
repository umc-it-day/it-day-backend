package com.example.itday.domain.terms.dto;

public record TermsResDTO(
        Long termsId,
        String title,
        String content,
        boolean isRequired
) {}
