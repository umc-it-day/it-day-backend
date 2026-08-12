package com.example.itday.domain.map.dto;

import com.example.itday.domain.benefit.entity.Benefit;
import com.example.itday.domain.membership.entity.Membership;

import java.time.LocalDate;

public record BenefitResponse(
        Long benefitId,
        String title,
        String description,
        String telecom,
        String telecomGrade,
        LocalDate validFrom,
        LocalDate validTo
) {

    public static BenefitResponse from(Benefit benefit) {
        String telecom = null;
        String telecomGrade = null;
        Membership membership = benefit.getMembership();

        if (membership != null) {
            telecom = membership.getTelecom().name();
            telecomGrade = membership.getTelecomGrade().name();
        }

        return new BenefitResponse(
                benefit.getId(),
                benefit.getTitle(),
                benefit.getDescription(),
                telecom,
                telecomGrade,
                benefit.getValidFrom(),
                benefit.getValidTo()
        );
    }
}
