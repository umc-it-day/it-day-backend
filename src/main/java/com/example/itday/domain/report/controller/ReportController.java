package com.example.itday.domain.report.controller;

import com.example.itday.global.response.ApiResponse;
import com.example.itday.domain.report.entity.ReportPeriodType;
import com.example.itday.domain.report.dto.request.ReportCreateRequest;
import com.example.itday.domain.report.dto.response.ReportDetailResponse;
import com.example.itday.domain.report.dto.response.ReportSummaryResponse;
import com.example.itday.domain.report.service.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 리포트 생성
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReportDetailResponse> createReport(
            @RequestHeader("X-USER-ID") Long userId,

            @Valid
            @RequestBody
            ReportCreateRequest request
    ) {
        ReportDetailResponse response =
                reportService.createReport(
                        userId,
                        request
                );

        return ApiResponse.success(
                "사용자 리포트가 생성되었습니다.",
                response
        );
    }

    /**
     * 내 전체 리포트 목록 조회
     */
    @GetMapping
    public ApiResponse<List<ReportSummaryResponse>>
    getMyReports(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        List<ReportSummaryResponse> response =
                reportService.getMyReports(userId);

        return ApiResponse.success(response);
    }

    /**
     * 기간 유형별 리포트 조회
     *
     * 예:
     * /api/reports/period?periodType=WEEKLY
     * /api/reports/period?periodType=MONTHLY
     */
    @GetMapping("/period")
    public ApiResponse<List<ReportSummaryResponse>>
    getMyReportsByPeriod(
            @RequestHeader("X-USER-ID") Long userId,
            @RequestParam ReportPeriodType periodType
    ) {
        List<ReportSummaryResponse> response =
                reportService.getMyReportsByPeriod(
                        userId,
                        periodType
                );

        return ApiResponse.success(response);
    }

    /**
     * 리포트 상세 조회
     */
    @GetMapping("/{reportId}")
    public ApiResponse<ReportDetailResponse> getMyReport(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long reportId
    ) {
        ReportDetailResponse response =
                reportService.getMyReport(
                        userId,
                        reportId
                );

        return ApiResponse.success(response);
    }
}
