package com.example.itday.domain.challenge.controller;

import com.example.itday.domain.challenge.dto.response.VisitChallengeResponse;
import com.example.itday.domain.challenge.service.VisitChallengeService;
import com.example.itday.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenges/visits")
public class VisitChallengeController {

    private final VisitChallengeService visitChallengeService;

    /*
     * 테스트용 API다.
     * 실제 운영에서는 혜택 사용 완료 서비스에서 호출하는 것이 안전하다.
     */
    @PostMapping
    public ApiResponse<VisitChallengeResponse> recordVisit(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        VisitChallengeResponse response =
                visitChallengeService.recordVisit(userId);

        return ApiResponse.success(
                "방문 기록이 반영되었습니다.",
                response
        );
    }

    @GetMapping
    public ApiResponse<VisitChallengeResponse> getProgress(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        VisitChallengeResponse response =
                visitChallengeService.getProgress(userId);

        return ApiResponse.success(
                "방문 도전과제 진행도를 조회했습니다.",
                response
        );
    }
}
