package com.example.itday.domain.member.controller;

import com.example.itday.domain.attendance.dto.AttendanceResDTO;
import com.example.itday.domain.attendance.service.AttendanceService;
import com.example.itday.domain.barcode.dto.BarcodeReqDTO;
import com.example.itday.domain.barcode.dto.BarcodeResDTO;
import com.example.itday.domain.barcode.service.BarcodeService;
import com.example.itday.domain.member.dto.MemberInfoResDTO;
import com.example.itday.domain.member.dto.MemberNameUpdateReqDTO;
import com.example.itday.domain.member.dto.OnboardingReqDTO;
import com.example.itday.domain.member.service.MemberService;
import com.example.itday.domain.membership.dto.MembershipSummaryResDTO;
import com.example.itday.domain.membership.dto.MembershipUpdateReqDTO;
import com.example.itday.domain.usage.dto.UsageReqDTO;
import com.example.itday.domain.usage.service.UsageService;
import com.example.itday.global.apiPayload.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final BarcodeService barcodeService;
    private final UsageService usageService;
    private final AttendanceService attendanceService;

    @DeleteMapping("/me")
    public ApiResponse<Void> withdraw(@AuthenticationPrincipal Long memberId){
        memberService.withdraw(memberId);
        return ApiResponse.onSuccess("회원 탈퇴 되었습니다", null);
    }

    @PostMapping("/onboarding")
    public ApiResponse<Void> submitOnboarding(
            @AuthenticationPrincipal Long memberId,
            @RequestBody OnboardingReqDTO request
            ){
        memberService.submitOnboarding(memberId,request);
        return ApiResponse.onSuccess("온보딩이 완료되었습니다",null);
    }

    @PostMapping("/me/barcode")
    public ApiResponse<Void> registerBarcode(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody BarcodeReqDTO request
    ){
        barcodeService.registerBarcode(memberId,request);
        return ApiResponse.onSuccess("바코드가 등록되었습니다",null);
    }

    @PutMapping("/me/barcode")
    public ApiResponse<Void> updateBarcode(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody BarcodeReqDTO request
    ){
        barcodeService.updateBarcode(memberId, request);
        return ApiResponse.onSuccess("바코드가 수정되었습니다.", null);
    }

    @GetMapping("/me/barcode")
    public ApiResponse<BarcodeResDTO> getBarcode(@AuthenticationPrincipal Long memberId) {
        BarcodeResDTO result = barcodeService.getBarcode(memberId);
        return ApiResponse.onSuccess(result); // 없다면 null 반환
    }

    @PostMapping("/me/barcode/usage")
    public ApiResponse<Void> recordUsage(
            @AuthenticationPrincipal Long memberId,
            @RequestBody UsageReqDTO request
    ){
        usageService.recordUsage(memberId,request);
        return ApiResponse.onSuccess("사용 기록이 저장되었습니다.",null);
    }

    @GetMapping("/me/membership")
    public ApiResponse<MembershipSummaryResDTO> getMembershipSummary(
            @AuthenticationPrincipal Long memberId
    ){
        MembershipSummaryResDTO result = memberService.getMembershipSummary(memberId);
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/me/profile")
    public ApiResponse<MemberInfoResDTO> getMemberInfo(
            @AuthenticationPrincipal Long memberId
    ) {
        MemberInfoResDTO result = memberService.getMemberInfo(memberId);
        return ApiResponse.onSuccess(result);
    }

    @PatchMapping("/me/membership")
    public ApiResponse<Void> updateMembership(
            @AuthenticationPrincipal Long memberId,
            @RequestBody MembershipUpdateReqDTO request
    ) {
        memberService.updateMembership(memberId, request);
        return ApiResponse.onSuccess("멤버십 정보가 변경되었습니다.", null);
    }

    @PatchMapping("/me/name")
    public ApiResponse<Void> updateMemberName(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody MemberNameUpdateReqDTO request
    ) {
        memberService.updateMemberName(memberId, request);
        return ApiResponse.onSuccess("이름이 변경되었습니다.",null);
    }

    @PostMapping("/me/attendance")
    public ApiResponse<Void> checkAttendance(@AuthenticationPrincipal Long memberId) {
        attendanceService.checkAttendance(memberId);
        return ApiResponse.onSuccess(null);
    }

    @GetMapping("/me/attendance")
    public ApiResponse<AttendanceResDTO> getMonthlyAttendance(@AuthenticationPrincipal Long memberId) {
        AttendanceResDTO result = attendanceService.getMonthlyAttendance(memberId);
        return ApiResponse.onSuccess(result);
    }
}
