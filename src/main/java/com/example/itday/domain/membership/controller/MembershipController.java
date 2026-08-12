package com.example.itday.domain.membership.controller;

import com.example.itday.domain.benefit.dto.GradeWithBenefitsResDTO;
import com.example.itday.domain.membership.dto.MembershipGradeResDTO;
import com.example.itday.domain.membership.dto.TelecomResDTO;
import com.example.itday.domain.membership.enums.Telecom;
import com.example.itday.domain.membership.service.MembershipService;
import com.example.itday.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/telecoms")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping()
    public ApiResponse<List<TelecomResDTO>> getTelecoms() {
        return ApiResponse.success(membershipService.getTelecoms());
    }

    @GetMapping("/{telecom}/grades")
    public ApiResponse<List<MembershipGradeResDTO>> getGrades(@PathVariable Telecom telecom) {
        return ApiResponse.success(membershipService.getGrades(telecom));
    }

    @GetMapping("/grades-with-benefits")
    public ApiResponse<List<GradeWithBenefitsResDTO>> getAllGradesWithBenefits() {
        List<GradeWithBenefitsResDTO> result = membershipService.getAllBenefits();
        return ApiResponse.success(result);
    }
}
