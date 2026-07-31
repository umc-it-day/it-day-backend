package com.example.itday.domain.report.service;

import com.example.itday.domain.challenge.dto.response.PointBalanceResponse;
import com.example.itday.domain.challenge.entity.PointReason;
import com.example.itday.domain.challenge.entity.TowerProgress;
import com.example.itday.domain.challenge.entity.VisitChallengeProgress;
import com.example.itday.domain.challenge.repository.*;
//import com.example.itday.domain.challenge.repository.VisiRecordRepository;
import com.example.itday.global.exception.ErrorCode;
import com.example.itday.global.exception.ItDayException;
import com.example.itday.domain.report.entity.Report;
import com.example.itday.domain.report.entity.ReportPeriodType;
import com.example.itday.domain.report.dto.request.ReportCreateRequest;
import com.example.itday.domain.report.dto.response.ReportDetailResponse;
import com.example.itday.domain.report.dto.response.ReportSummaryResponse;
import com.example.itday.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private static final List<PointReason> ATTENDANCE_REASONS =
            List.of(
                    PointReason.DAILY_ATTENDANCE,
                    PointReason.ATTENDANCE_7_DAYS,
                    PointReason.ATTENDANCE_15_DAYS,
                    PointReason.ATTENDANCE_30_DAYS
            );

    private static final List<PointReason> VISIT_REASONS =
            List.of(
                    PointReason.VISIT_5_TIMES,
                    PointReason.VISIT_10_TIMES,
                    PointReason.VISIT_15_TIMES,
                    PointReason.VISIT_20_TIMES
            );

    private final ReportRepository reportRepository;

    private final AttendanceRepository attendanceRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final VisitRecordRepository visitRecordRepository;
    private final VisitChallengeProgressRepository
            visitChallengeProgressRepository;
    private final TowerProgressRepository towerProgressRepository;

    @Transactional
    public ReportDetailResponse createReport(
            Long userId,
            ReportCreateRequest request
    ) {
        validateDateRange(request);

        boolean alreadyExists =
                reportRepository
                        .existsByUserIdAndPeriodTypeAndStartDateAndEndDate(
                                userId,
                                request.periodType(),
                                request.startDate(),
                                request.endDate()
                        );

        if (alreadyExists) {
            throw new ItDayException(
                    ErrorCode.REPORT_ALREADY_EXISTS
            );
        }

        LocalDate startDate = request.startDate();
        LocalDate endDate = request.endDate();

        LocalDateTime startDateTime =
                startDate.atStartOfDay();

        LocalDateTime endDateTimeExclusive =
                endDate.plusDays(1).atStartOfDay();

        /*
         * 출석 집계
         */
        int attendanceCount =
                Math.toIntExact(
                        attendanceRepository
                                .countByUserIdAndAttendanceDateBetween(
                                        userId,
                                        startDate,
                                        endDate
                                )
                );

        int maxConsecutiveDays =
                attendanceRepository
                        .findMaxConsecutiveDays(
                                userId,
                                startDate,
                                endDate
                        );

        int attendancePoint =
                pointHistoryRepository
                        .sumByUserIdAndReasonsAndPeriod(
                                userId,
                                ATTENDANCE_REASONS,
                                startDateTime,
                                endDateTimeExclusive
                        );

        /*
         * 방문 집계
         */
        int visitCount =
                Math.toIntExact(
                        visitRecordRepository
                                .countByUserIdAndVisitedAtGreaterThanEqualAndVisitedAtLessThan(
                                        userId,
                                        startDateTime,
                                        endDateTimeExclusive
                                )
                );

        int visitRewardPoint =
                pointHistoryRepository
                        .sumByUserIdAndReasonsAndPeriod(
                                userId,
                                VISIT_REASONS,
                                startDateTime,
                                endDateTimeExclusive
                        );

        /*
         * 현재 누적 방문 단계
         *
         * 주의:
         * 이 값은 리포트 기간 종료 당시 단계가 아니라
         * 리포트를 생성하는 현재 시점의 단계입니다.
         */
        int currentVisitStage =
                visitChallengeProgressRepository
                        .findByUserId(userId)
                        .map(
                                VisitChallengeProgress::getCurrentStage
                        )
                        .orElse(0);

        /*
         * 전체 획득 포인트
         */
        int totalEarnedPoint =
                pointHistoryRepository
                        .sumByUserIdAndPeriod(
                                userId,
                                startDateTime,
                                endDateTimeExclusive
                        );

        /*
         * 카피바라 탑
         */
        int unlockedFloor =
                towerProgressRepository
                        .findByUserId(userId)
                        .map(
                                TowerProgress::getHighestUnlockedFloor
                        )
                        .orElse(1);

        String summary =
                createSummary(
                        attendanceCount,
                        maxConsecutiveDays,
                        visitCount,
                        currentVisitStage,
                        totalEarnedPoint
                );

        Report report =
                Report.builder()
                        .userId(userId)
                        .periodType(request.periodType())
                        .startDate(startDate)
                        .endDate(endDate)
                        .attendanceCount(attendanceCount)
                        .maxConsecutiveDays(maxConsecutiveDays)
                        .attendancePoint(attendancePoint)
                        .visitCount(visitCount)
                        .visitStage(currentVisitStage)
                        .visitRewardPoint(visitRewardPoint)
                        .totalEarnedPoint(totalEarnedPoint)
                        .unlockedFloor(unlockedFloor)
                        .summary(summary)
                        .build();

        Report savedReport =
                reportRepository.save(report);

        return ReportDetailResponse.from(savedReport);
    }

    public List<ReportSummaryResponse> getMyReports(
            Long userId
    ) {
        return reportRepository
                .findAllByUserIdOrderByStartDateDesc(userId)
                .stream()
                .map(ReportSummaryResponse::from)
                .toList();
    }

    public List<ReportSummaryResponse> getMyReportsByPeriod(
            Long userId,
            ReportPeriodType periodType
    ) {
        return reportRepository
                .findAllByUserIdAndPeriodTypeOrderByStartDateDesc(
                        userId,
                        periodType
                )
                .stream()
                .map(ReportSummaryResponse::from)
                .toList();
    }

    public ReportDetailResponse getMyReport(
            Long userId,
            Long reportId
    ) {
        Report report =
                reportRepository
                        .findByIdAndUserId(
                                reportId,
                                userId
                        )
                        .orElseThrow(() ->
                                new ItDayException(
                                        ErrorCode.REPORT_NOT_FOUND
                                )
                        );

        return ReportDetailResponse.from(report);
    }

    private void validateDateRange(
            ReportCreateRequest request
    ) {
        if (request.endDate()
                .isBefore(request.startDate())) {

            throw new ItDayException(
                    ErrorCode.REPORT_DATE_INVALID
            );
        }
    }

    private String createSummary(
            int attendanceCount,
            int maxConsecutiveDays,
            int visitCount,
            int currentVisitStage,
            int totalEarnedPoint
    ) {
        if (attendanceCount == 0
                && visitCount == 0
                && totalEarnedPoint == 0) {

            return "이번 기간에는 출석 및 방문 기록이 없습니다. "
                    + "출석과 제휴 매장 방문을 통해 포인트를 모아 보세요.";
        }

        return String.format(
                "이번 기간 동안 %d일 출석했고, "
                        + "최대 %d일 연속 출석했습니다. "
                        + "제휴 매장은 %d회 방문했으며, "
                        + "현재 방문 도전과제는 %d단계입니다. "
                        + "총 %,dP를 획득했습니다.",
                attendanceCount,
                maxConsecutiveDays,
                visitCount,
                currentVisitStage,
                totalEarnedPoint
        );
    }
}
