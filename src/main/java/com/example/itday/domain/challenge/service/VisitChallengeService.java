package com.example.itday.domain.challenge.service;

import com.example.itday.domain.challenge.dto.response.VisitChallengeResponse;
import com.example.itday.domain.challenge.entity.PointHistory;
import com.example.itday.domain.challenge.entity.VisitChallengeProgress;
import com.example.itday.domain.challenge.entity.VisitMilestone;
import com.example.itday.domain.challenge.repository.PointHistoryRepository;
import com.example.itday.domain.challenge.repository.VisitChallengeProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VisitChallengeService {

    private final VisitChallengeProgressRepository progressRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public VisitChallengeResponse recordVisit(Long userId) {
        VisitChallengeProgress progress =
                progressRepository.findByUserId(userId)
                        .orElseGet(() ->
                                VisitChallengeProgress.builder()
                                        .userId(userId)
                                        .build()
                        );

        if (progress.getCompleted()) {
            return createResponse(progress, 0);
        }

        progress.increaseVisitCount();

        progressRepository.save(progress);

        int newlyEarnedPoint =
                rewardMilestoneIfReached(
                        userId,
                        progress.getVisitCount()
                );

        return createResponse(
                progress,
                newlyEarnedPoint
        );
    }

    public VisitChallengeResponse getProgress(Long userId) {
        VisitChallengeProgress progress =
                progressRepository.findByUserId(userId)
                        .orElseGet(() ->
                                VisitChallengeProgress.builder()
                                        .userId(userId)
                                        .build()
                        );

        return createResponse(progress, 0);
    }

    private int rewardMilestoneIfReached(
            Long userId,
            int visitCount
    ) {
        return VisitMilestone.findByVisitCount(visitCount)
                .map(milestone -> {
                    boolean alreadyRewarded =
                            pointHistoryRepository
                                    .existsByUserIdAndReason(
                                            userId,
                                            milestone.getPointReason()
                                    );

                    if (alreadyRewarded) {
                        return 0;
                    }

                    PointHistory pointHistory =
                            PointHistory.builder()
                                    .userId(userId)
                                    .point(milestone.getRewardPoint())
                                    .reason(milestone.getPointReason())
                                    .rewardMonth(null)
                                    .description(
                                            "누적 방문 "
                                                    + milestone.getRequiredVisitCount()
                                                    + "회 달성 보상"
                                    )
                                    .build();

                    pointHistoryRepository.save(pointHistory);

                    return milestone.getRewardPoint();
                })
                .orElse(0);
    }

    private VisitChallengeResponse createResponse(
            VisitChallengeProgress progress,
            int newlyEarnedPoint
    ) {
        int nextTarget =
                calculateNextTarget(progress.getVisitCount());

        int currentStageReward =
                calculateCurrentStageReward(
                        progress.getCurrentStage()
                );

        double progressRate =
                Math.min(
                        100.0,
                        progress.getVisitCount() / 20.0 * 100
                );

        return new VisitChallengeResponse(
                progress.getVisitCount(),
                progress.getCurrentStage(),
                nextTarget,
                currentStageReward,
                progress.getTotalRewardPoint(),
                newlyEarnedPoint,
                progressRate,
                progress.getCompleted(),
                getCharacterVisual(progress.getCurrentStage())
        );
    }

    private int calculateNextTarget(int visitCount) {
        if (visitCount < 5) {
            return 5;
        }

        if (visitCount < 10) {
            return 10;
        }

        if (visitCount < 15) {
            return 15;
        }

        if (visitCount < 20) {
            return 20;
        }

        return 20;
    }

    private int calculateCurrentStageReward(int stage) {
        if (stage == 1) {
            return 100;
        }

        if (stage == 2) {
            return 200;
        }

        if (stage == 3) {
            return 400;
        }

        if (stage == 4) {
            return 1000;
        }

        return 0;
    }

    private String getCharacterVisual(int stage) {
        return switch (stage) {
            case 1 -> "1단계 카피바라";
            case 2 -> "2단계 카피바라";
            case 3 -> "3단계 카피바라";
            case 4 -> "올클리어 카피바라";
            default -> "기본 카피바라";
        };
    }
}
