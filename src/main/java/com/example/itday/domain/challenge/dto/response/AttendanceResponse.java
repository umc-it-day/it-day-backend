package com.example.itday.domain.challenge.dto.response;

import java.time.LocalDate;

public record AttendanceResponse(

        Long attendanceId,
        LocalDate attendanceDate,
        int consecutiveDays,
        int dailyPoint,
        int bonusPoint,
        int earnedPoint,
        int monthlyAttendanceCount,
        int monthlyEarnedPoint,
        boolean sevenDayBonusReceived,
        boolean fifteenDayBonusReceived,
        boolean thirtyDayBonusReceived
) {
}
