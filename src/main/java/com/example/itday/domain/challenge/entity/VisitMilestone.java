package com.example.itday.domain.challenge.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum VisitMilestone {

    STAGE_1(1, 5, 100, PointReason.VISIT_5_TIMES),
    STAGE_2(2, 10, 200, PointReason.VISIT_10_TIMES),
    STAGE_3(3, 15, 400, PointReason.VISIT_15_TIMES),
    STAGE_4(4, 20, 1000, PointReason.VISIT_20_TIMES);

    private final int stage;
    private final int requiredVisitCount;
    private final int rewardPoint;
    private final PointReason pointReason;

    public static Optional<VisitMilestone> findByVisitCount(
            int visitCount
    ) {
        return Arrays.stream(values())
                .filter(milestone ->
                        milestone.requiredVisitCount == visitCount
                )
                .findFirst();
    }

    public static int calculateStage(int visitCount) {
        if (visitCount >= 20) {
            return 4;
        }

        if (visitCount >= 15) {
            return 3;
        }

        if (visitCount >= 10) {
            return 2;
        }

        if (visitCount >= 5) {
            return 1;
        }

        return 0;
    }

    public static int calculateTotalReward(int stage) {
        return Arrays.stream(values())
                .filter(milestone -> milestone.stage <= stage)
                .mapToInt(VisitMilestone::getRewardPoint)
                .sum();
    }
}
