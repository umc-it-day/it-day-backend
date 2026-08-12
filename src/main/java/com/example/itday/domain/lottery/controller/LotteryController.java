package com.example.itday.domain.lottery.controller;

import com.example.itday.domain.lottery.dto.LotteryResDTO;
import com.example.itday.domain.lottery.service.LotteryService;
import com.example.itday.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LotteryController {

    private final LotteryService lotteryService;

    @GetMapping("/api/lottery/check")
    public ApiResponse<LotteryResDTO> checkRanking(@RequestParam String number) {
        LotteryResDTO result = lotteryService.checkRanking(number);
        return ApiResponse.success(result);
    }
}
