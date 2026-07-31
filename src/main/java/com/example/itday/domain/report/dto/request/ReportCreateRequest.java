package com.example.itday.domain.report.dto.request;

import com.example.itday.domain.report.entity.ReportPeriodType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ReportCreateRequest(

        @NotNull(message = "리포트 기간 유형은 필수입니다.")
        ReportPeriodType periodType,

        @NotNull(message = "리포트 시작일은 필수입니다.")
        LocalDate startDate,

        @NotNull(message = "리포트 종료일은 필수입니다.")
        LocalDate endDate,

        @NotNull(message = "혜택 사용 횟수는 필수입니다.")
        @PositiveOrZero(message = "혜택 사용 횟수는 0 이상이어야 합니다.")
        Integer benefitUsageCount,

        @NotNull(message = "총 절약 금액은 필수입니다.")
        @PositiveOrZero(message = "총 절약 금액은 0 이상이어야 합니다.")
        Long totalSavingAmount,

        @NotNull(message = "방문 매장 수는 필수입니다.")
        @PositiveOrZero(message = "방문 매장 수는 0 이상이어야 합니다.")
        Integer visitedStoreCount,

        @Size(
                max = 50,
                message = "선호 카테고리는 50자 이하여야 합니다."
        )
        String favoriteCategory
) {
}

