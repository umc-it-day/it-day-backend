package com.example.itday.domain.point.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointReason {
    ATTENDANCE("출석체크"),
    STREAK_7_DAYS("7일 연속 출석 보너스"),
    STREAK_15_DAYS("15일 연속 출석 보너스"),
    MONTHLY_PERFECT("한 달 개근 보너스");

    private final String label;
}
