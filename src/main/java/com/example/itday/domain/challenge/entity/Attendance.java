package com.example.itday.domain.challenge.entity;


import com.example.itday.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(
        name = "attendances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_attendance_user_date",
                        columnNames = {"user_id", "attendance_date"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_attendance_user_date",
                        columnList = "user_id, attendance_date"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attendance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    /*
     * 로그인 담당 도메인과 충돌을 줄이기 위해
     * User 연관관계 대신 userId만 저장한다.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "consecutive_days", nullable = false)
    private Integer consecutiveDays;

    @Column(name = "daily_point", nullable = false)
    private Integer dailyPoint;

    @Column(name = "bonus_point", nullable = false)
    private Integer bonusPoint;

    @Column(name = "total_point", nullable = false)
    private Integer totalPoint;

    @Builder
    private Attendance(
            Long userId,
            LocalDate attendanceDate,
            Integer consecutiveDays,
            Integer dailyPoint,
            Integer bonusPoint
    ) {
        this.userId = userId;
        this.attendanceDate = attendanceDate;
        this.consecutiveDays = consecutiveDays;
        this.dailyPoint = dailyPoint;
        this.bonusPoint = bonusPoint;
        this.totalPoint = dailyPoint + bonusPoint;
    }
}
