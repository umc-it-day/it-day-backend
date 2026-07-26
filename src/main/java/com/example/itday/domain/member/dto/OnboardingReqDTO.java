package com.example.itday.domain.member.dto;

import java.util.List;

public record OnboardingReqDTO(
    List<TermAgreementDTO> termAgreements,
    Long membershipId,
    List<Long> preferredBrandIds
) {
    public record TermAgreementDTO(
            Long termId,
            boolean isAgree
    ) {
    }
}
