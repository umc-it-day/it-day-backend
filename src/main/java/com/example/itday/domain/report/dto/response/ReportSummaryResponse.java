package com.example.itday.domain.report.dto.response;

import com.example.itday.domain.report.entity.Report;
import com.example.itday.domain.report.entity.ReportPeriodType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReportSummaryResponse(

        Long reportId,

        ReportPeriodType periodType,

        LocalDate startDate,

        LocalDate endDate,

        int attendanceCount,

        int visitCount,

        int totalEarnedPoint,

        String summary

) {
    public static ReportSummaryResponse from(
            Report report
    ) {
        return new ReportSummaryResponse(
                report.getId(),
                report.getPeriodType(),
                report.getStartDate(),
                report.getEndDate(),
                report.getAttendanceCount(),
                report.getVisitCount(),
                report.getTotalEarnedPoint(),
                report.getSummary()
        );
    }
}
