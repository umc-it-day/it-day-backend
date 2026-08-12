package com.example.itday.domain.report.dto.response;

import com.example.itday.domain.report.entity.Report;
import com.example.itday.domain.report.entity.ReportPeriodType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReportDetailResponse(

        Long reportId,

        ReportPeriodType periodType,

        LocalDate startDate,

        LocalDate endDate,

        int attendanceCount,

        int maxConsecutiveDays,

        int attendancePoint,

        int visitCount,

        int visitStage,

        int visitRewardPoint,

        int totalEarnedPoint,

        int unlockedFloor,

        String summary
) {

    public static ReportDetailResponse from(Report report) {
        return new ReportDetailResponse(
                report.getId(),
                report.getPeriodType(),
                report.getStartDate(),
                report.getEndDate(),
                report.getAttendanceCount(),
                report.getMaxConsecutiveDays(),
                report.getAttendancePoint(),
                report.getVisitCount(),
                report.getVisitStage(),
                report.getVisitRewardPoint(),
                report.getTotalEarnedPoint(),
                report.getUnlockedFloor(),
                report.getSummary()
        );
    }
}
