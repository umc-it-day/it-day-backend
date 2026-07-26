package com.example.itday.domain.attendance.service;

import com.example.itday.domain.attendance.dto.AttendanceResDTO;
import com.example.itday.domain.attendance.entity.Attendance;
import com.example.itday.domain.attendance.repository.AttendanceRepository;
import com.example.itday.domain.member.entity.Member;
import com.example.itday.domain.member.exception.MemberNotFoundException;
import com.example.itday.domain.member.repository.MemberRepository;
import com.example.itday.domain.point.entity.PointHistory;
import com.example.itday.domain.point.enums.PointReason;
import com.example.itday.domain.point.repository.PointHistoryRepository;
import com.example.itday.global.apiPayload.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void checkAttendance(Long memberId){

        LocalDate today = LocalDate.now();

        if(attendanceRepository.existsByMemberIdAndAttendedAt(memberId, today)){
            return; // 이미 출석을 했다면 로직 종ㄹ
        }

        Member member = memberRepository.findById(memberId)
                        .orElseThrow(()->new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        // 출석 저장
        Attendance attendance = Attendance.builder()
                .member(member)
                .attendedAt(today)
                .build();
        attendanceRepository.save(attendance);

        // 연속 출석일수
        int streak = calculateStreak(memberId, today);

        // 이번 달 시작
        LocalDate monthStart = today.withDayOfMonth(1);

        // 지급 포인트, 이유
        int rewardPoint = 10;
        PointReason reason = PointReason.ATTENDANCE;

        if (streak == 7 && !hasReceivedThisMonth(memberId, PointReason.STREAK_7_DAYS, monthStart, today)) {
            rewardPoint = 30;
            reason = PointReason.STREAK_7_DAYS;
        } else if (streak == 15 && !hasReceivedThisMonth(memberId, PointReason.STREAK_15_DAYS, monthStart, today)) {
            rewardPoint = 30;
            reason = PointReason.STREAK_15_DAYS;
        } else if (isLastDayOfMonth(today) && isMonthlyPerfect(memberId, today)) {
            rewardPoint = 100;
            reason = PointReason.MONTHLY_PERFECT;
        }

        PointHistory pointHistory = PointHistory.builder()
                .member(member)
                .rewardPoint(rewardPoint)
                .reason(reason)
                .createdAt(today)
                .build();
        pointHistoryRepository.save(pointHistory);

        member.addPoint(rewardPoint);
    }

    public AttendanceResDTO getMonthlyAttendance(Long memberId) {

        LocalDate today = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);

        List<LocalDate> attendedDates = attendanceRepository.findAllByMemberIdAndAttendedAtBetween(memberId,monthStart,today).stream()
                .map(Attendance::getAttendedAt)
                .toList();

        int streak = calculateStreak(memberId,today);

        int monthPoint = pointHistoryRepository
                .findAllByMemberIdAndCreatedAtBetween(memberId, monthStart, today)
                .stream()
                .mapToInt(PointHistory::getRewardPoint)
                .sum();

        AttendanceResDTO.BonusInfo streak7Bonus = buildBonusInfo(memberId, PointReason.STREAK_7_DAYS, monthStart, today);
        AttendanceResDTO.BonusInfo streak15Bonus = buildBonusInfo(memberId, PointReason.STREAK_15_DAYS, monthStart, today);
        AttendanceResDTO.BonusInfo monthlyBonus = buildBonusInfo(memberId, PointReason.MONTHLY_PERFECT, monthStart, today);

        return new AttendanceResDTO(
                monthPoint,
                streak,
                streak7Bonus,
                streak15Bonus,
                monthlyBonus,
                attendedDates
        );
    }

    private int calculateStreak(Long memberId, LocalDate today) {
        int streak = 0;
        LocalDate date = today;
        LocalDate monthStart = today.withDayOfMonth(1);

        while (!date.isBefore(monthStart) && attendanceRepository.existsByMemberIdAndAttendedAt(memberId, date)) {
            streak++;
            date = date.minusDays(1);
        }
        return streak;
    }

    private boolean hasReceivedThisMonth(Long memberId, PointReason reason, LocalDate start, LocalDate end) {
        return pointHistoryRepository.existsByMemberIdAndReasonAndCreatedAtBetween(memberId, reason, start, end);
    }

    private boolean isLastDayOfMonth(LocalDate date) {
        return date.equals(date.withDayOfMonth(date.lengthOfMonth()));
    }

    private boolean isMonthlyPerfect(Long memberId, LocalDate today) {
        LocalDate firstDay = today.withDayOfMonth(1);
        long expectedDays = ChronoUnit.DAYS.between(firstDay, today) + 1;
        long actualDays = attendanceRepository
                .findAllByMemberIdAndAttendedAtBetween(memberId, firstDay, today)
                .size();
        return expectedDays == actualDays;
    }

    private AttendanceResDTO.BonusInfo buildBonusInfo(Long memberId, PointReason reason, LocalDate start, LocalDate end) {
        return pointHistoryRepository.findByMemberIdAndReasonAndCreatedAtBetween(memberId, reason, start, end)
                .map(history -> new AttendanceResDTO.BonusInfo(true, history.getCreatedAt()))
                .orElse(new AttendanceResDTO.BonusInfo(false, null));
    }
}
