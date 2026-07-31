package com.example.itday.domain.challenge.service;


import com.example.itday.domain.challenge.dto.response.AttendanceResponse;
import com.example.itday.domain.challenge.entity.Attendance;
import com.example.itday.domain.challenge.entity.PointHistory;
import com.example.itday.domain.challenge.entity.PointReason;
import com.example.itday.domain.challenge.repository.AttendanceRepository;
import com.example.itday.domain.challenge.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private static final int DAILY_ATTENDANCE_POINT = 10;

    private final AttendanceRepository attendanceRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public AttendanceResponse checkIn(
            Long userId,
            LocalDate today
    ) {
        validateDuplicateAttendance(userId, today);

        int consecutiveDays = calculateConsecutiveDays(userId, today);

        int bonusPoint = calculateAndSaveBonus(
                userId,
                today,
                consecutiveDays
        );

        Attendance attendance = Attendance.builder()
                .userId(userId)
                .attendanceDate(today)
                .consecutiveDays(consecutiveDays)
                .dailyPoint(DAILY_ATTENDANCE_POINT)
                .bonusPoint(bonusPoint)
                .build();

        attendanceRepository.save(attendance);

        saveDailyPoint(userId, today);

        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate monthEnd = today.withDayOfMonth(
                today.lengthOfMonth()
        );

        int monthlyAttendanceCount = Math.toIntExact(
                attendanceRepository.countByUserIdAndAttendanceDateBetween(
                        userId,
                        monthStart,
                        monthEnd
                )
        );

        int monthlyEarnedPoint = pointHistoryRepository
                .sumPointByUserIdAndMonth(
                        userId,
                        monthStart,
                        monthEnd
                );

        return new AttendanceResponse(
                attendance.getId(),
                attendance.getAttendanceDate(),
                attendance.getConsecutiveDays(),
                attendance.getDailyPoint(),
                attendance.getBonusPoint(),
                attendance.getTotalPoint(),
                monthlyAttendanceCount,
                monthlyEarnedPoint,
                hasMonthlyReward(
                        userId,
                        PointReason.ATTENDANCE_7_DAYS,
                        monthStart
                ),
                hasMonthlyReward(
                        userId,
                        PointReason.ATTENDANCE_15_DAYS,
                        monthStart
                ),
                hasMonthlyReward(
                        userId,
                        PointReason.ATTENDANCE_30_DAYS,
                        monthStart
                )
        );
    }

    private void validateDuplicateAttendance(
            Long userId,
            LocalDate today
    ) {
        if (attendanceRepository.existsByUserIdAndAttendanceDate(
                userId,
                today
        )) {
            throw new IllegalStateException(
                    "오늘은 이미 출석했습니다."
            );
        }
    }

    private int calculateConsecutiveDays(
            Long userId,
            LocalDate today
    ) {
        return attendanceRepository
                .findTopByUserIdOrderByAttendanceDateDesc(userId)
                .map(lastAttendance -> {
                    LocalDate lastDate =
                            lastAttendance.getAttendanceDate();

                    if (lastDate.equals(today.minusDays(1))) {
                        return lastAttendance.getConsecutiveDays() + 1;
                    }

                    return 1;
                })
                .orElse(1);
    }

    private int calculateAndSaveBonus(
            Long userId,
            LocalDate today,
            int consecutiveDays
    ) {
        LocalDate rewardMonth = today.withDayOfMonth(1);

        if (consecutiveDays == 30 &&
                !hasMonthlyReward(
                        userId,
                        PointReason.ATTENDANCE_30_DAYS,
                        rewardMonth
                )) {
            saveBonusPoint(
                    userId,
                    100,
                    PointReason.ATTENDANCE_30_DAYS,
                    rewardMonth,
                    "30일 연속 출석 보너스"
            );

            return 100;
        }

        if (consecutiveDays == 15 &&
                !hasMonthlyReward(
                        userId,
                        PointReason.ATTENDANCE_15_DAYS,
                        rewardMonth
                )) {
            saveBonusPoint(
                    userId,
                    30,
                    PointReason.ATTENDANCE_15_DAYS,
                    rewardMonth,
                    "15일 연속 출석 보너스"
            );

            return 30;
        }

        if (consecutiveDays == 7 &&
                !hasMonthlyReward(
                        userId,
                        PointReason.ATTENDANCE_7_DAYS,
                        rewardMonth
                )) {
            saveBonusPoint(
                    userId,
                    30,
                    PointReason.ATTENDANCE_7_DAYS,
                    rewardMonth,
                    "7일 연속 출석 보너스"
            );

            return 30;
        }

        return 0;
    }

    private void saveDailyPoint(
            Long userId,
            LocalDate today
    ) {
        PointHistory pointHistory = PointHistory.builder()
                .userId(userId)
                .point(10)
                .reason(PointReason.DAILY_ATTENDANCE)
                .rewardMonth(today.withDayOfMonth(1))
                .description("일일 출석 보상")
                .build();

        pointHistoryRepository.save(pointHistory);
    }

    private void saveBonusPoint(
            Long userId,
            int point,
            PointReason reason,
            LocalDate rewardMonth,
            String description
    ) {
        PointHistory pointHistory = PointHistory.builder()
                .userId(userId)
                .point(point)
                .reason(reason)
                .rewardMonth(rewardMonth)
                .description(description)
                .build();

        pointHistoryRepository.save(pointHistory);
    }

    private boolean hasMonthlyReward(
            Long userId,
            PointReason reason,
            LocalDate rewardMonth
    ) {
        return pointHistoryRepository
                .existsByUserIdAndReasonAndRewardMonth(
                        userId,
                        reason,
                        rewardMonth
                );
    }
}