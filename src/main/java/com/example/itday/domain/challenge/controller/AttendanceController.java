package com.example.itday.domain.challenge.controller;

import com.example.itday.domain.challenge.dto.response.AttendanceResponse;
import com.example.itday.domain.challenge.service.AttendanceService;
import com.example.itday.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ApiResponse<AttendanceResponse> checkIn(
            @RequestHeader("X-USER-ID") Long userId
    ) {
        AttendanceResponse response =
                attendanceService.checkIn(
                        userId,
                        LocalDate.now()
                );

        return ApiResponse.success(
                "출석이 완료되었습니다.",
                response
        );
    }
}
