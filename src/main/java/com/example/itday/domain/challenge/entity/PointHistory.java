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
        name = "point_histories",
        indexes = {
                @Index(
                        name = "idx_point_history_user",
                        columnList = "user_id"
                ),
                @Index(
                        name = "idx_point_history_user_reason",
                        columnList = "user_id, reason"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_history_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "point", nullable = false)
    private Integer point;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false, length = 50)
    private PointReason reason;

    /*
     * 출석 보너스의 월 1회 지급 여부를 확인하기 위한 기준 월.
     * 방문 보상은 null이 될 수 있다.
     */
    @Column(name = "reward_month")
    private LocalDate rewardMonth;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Builder
    private PointHistory(
            Long userId,
            Integer point,
            PointReason reason,
            LocalDate rewardMonth,
            String description
    ) {
        this.userId = userId;
        this.point = point;
        this.reason = reason;
        this.rewardMonth = rewardMonth;
        this.description = description;
    }
}
