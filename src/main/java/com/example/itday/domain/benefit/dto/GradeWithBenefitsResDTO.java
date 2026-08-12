package com.example.itday.domain.benefit.dto;

import java.util.List;

public record GradeWithBenefitsResDTO(
        String telecom,
        String telecomGrade,
        List<BenefitResDTO> benefits
) {
}
