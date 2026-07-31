package com.example.itday.domain.challenge.dto.response;

public record VisitChallengeResponse(

        int visitCount,
        int currentStage,
        int nextTargetVisitCount,
        int currentStageRewardPoint,
        int accumulatedRewardPoint,
        int newlyEarnedPoint,
        double progressRate,
        boolean completed,
        String characterVisual
) {
}
