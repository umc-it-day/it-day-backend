package com.example.itday.domain.challenge.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceRewardType {

    DAILY(1, 10),
    CONSECUTIVE_7_DAYS(7, 30),
    CONSECUTIVE_15_DAYS(15, 30),
    CONSECUTIVE_30_DAYS(30, 100);

    private final int requiredDays;
    private final int rewardPoint;
}
