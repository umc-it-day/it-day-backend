package com.example.itday.domain.report.entity;

import com.example.itday.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "reports",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_report_user_period",
                        columnNames = {
                                "user_id",
                                "period_type",
                                "start_date",
                                "end_date"
                        }
                )
        }
)
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false)
    private ReportPeriodType periodType;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "attendance_count", nullable = false)
    private int attendanceCount;

    @Column(name = "max_consecutive_days", nullable = false)
    private int maxConsecutiveDays;

    @Column(name = "attendance_point", nullable = false)
    private int attendancePoint;

    @Column(name = "visit_count", nullable = false)
    private int visitCount;

    @Column(name = "visit_stage", nullable = false)
    private int visitStage;

    @Column(name = "visit_reward_point", nullable = false)
    private int visitRewardPoint;

    @Column(name = "total_earned_point", nullable = false)
    private int totalEarnedPoint;

    @Column(name = "unlocked_floor", nullable = false)
    private int unlockedFloor;

    @Column(name = "summary", nullable = false, length = 500)
    private String summary;
}