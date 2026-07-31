package com.example.itday.domain.challenge.entity;

import com.example.itday.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "visit_challenge_progresses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_visit_progress_user",
                        columnNames = "user_id"
                )
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitChallengeProgress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_progress_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "visit_count", nullable = false)
    private Integer visitCount;

    @Column(name = "current_stage", nullable = false)
    private Integer currentStage;

    @Column(name = "total_reward_point", nullable = false)
    private Integer totalRewardPoint;

    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @Builder
    private VisitChallengeProgress(Long userId) {
        this.userId = userId;
        this.visitCount = 0;
        this.currentStage = 0;
        this.totalRewardPoint = 0;
        this.completed = false;
    }

    public void increaseVisitCount() {
        if (completed) {
            return;
        }

        this.visitCount++;

        this.currentStage =
                VisitMilestone.calculateStage(this.visitCount);

        this.totalRewardPoint =
                VisitMilestone.calculateTotalReward(this.currentStage);

        if (this.visitCount >= 20) {
            this.completed = true;
        }
    }
}
