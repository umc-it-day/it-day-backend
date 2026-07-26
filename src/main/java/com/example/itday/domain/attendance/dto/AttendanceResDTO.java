package com.example.itday.domain.attendance.dto;

import java.time.LocalDate;
import java.util.List;

public record AttendanceResDTO(
        int totalPointThisMonth,
        int currentStreak,
        BonusInfo streak7Bonus,
        BonusInfo streak15Bonus,
        BonusInfo monthlyBonus,
        List<LocalDate> attendedDates
) {
    public record BonusInfo(
            boolean received,
            LocalDate receivedDate
    ) {}
}
