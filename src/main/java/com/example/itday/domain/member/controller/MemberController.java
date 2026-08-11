package com.example.itday.domain.member.controller;

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
import com.example.itday.global.response.ApiResponse;
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

    @DeleteMapping("/me")
    public ApiResponse<Void> withdraw(@AuthenticationPrincipal Long memberId){
        memberService.withdraw(memberId);
        return ApiResponse.success("회원 탈퇴 되었습니다");
    }

    @PostMapping("/onboarding")
    public ApiResponse<Void> submitOnboarding(
            @AuthenticationPrincipal Long memberId,
            @RequestBody OnboardingReqDTO request
            ){
        memberService.submitOnboarding(memberId,request);
        return ApiResponse.success("온보딩이 완료되었습니다");
    }

    @PostMapping("/me/barcode")
    public ApiResponse<Void> registerBarcode(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody BarcodeReqDTO request
    ){
        barcodeService.registerBarcode(memberId,request);
        return ApiResponse.success("바코드가 등록되었습니다");
    }

    @PutMapping("/me/barcode")
    public ApiResponse<Void> updateBarcode(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody BarcodeReqDTO request
    ){
        barcodeService.updateBarcode(memberId, request);
        return ApiResponse.success("바코드가 수정되었습니다.");
    }

    @GetMapping("/me/barcode")
    public ApiResponse<BarcodeResDTO> getBarcode(@AuthenticationPrincipal Long memberId) {
        BarcodeResDTO result = barcodeService.getBarcode(memberId);
        return ApiResponse.success(result); // 없다면 null 반환
    }

    @PostMapping("/me/barcode/usage")
    public ApiResponse<Void> recordUsage(
            @AuthenticationPrincipal Long memberId,
            @RequestBody UsageReqDTO request
    ){
        usageService.recordUsage(memberId,request);
        return ApiResponse.success("사용 기록이 저장되었습니다.");
    }

    @GetMapping("/me/membership")
    public ApiResponse<MembershipSummaryResDTO> getMembershipSummary(
            @AuthenticationPrincipal Long memberId
    ){
        MembershipSummaryResDTO result = memberService.getMembershipSummary(memberId);
        return ApiResponse.success(result);
    }

    @GetMapping("/me/profile")
    public ApiResponse<MemberInfoResDTO> getMemberInfo(
            @AuthenticationPrincipal Long memberId
    ) {
        MemberInfoResDTO result = memberService.getMemberInfo(memberId);
        return ApiResponse.success(result);
    }

    @PatchMapping("/me/membership")
    public ApiResponse<Void> updateMembership(
            @AuthenticationPrincipal Long memberId,
            @RequestBody MembershipUpdateReqDTO request
    ) {
        memberService.updateMembership(memberId, request);
        return ApiResponse.success("멤버십 정보가 변경되었습니다.");
    }

    @PatchMapping("/me/name")
    public ApiResponse<Void> updateMemberName(
            @AuthenticationPrincipal Long memberId,
            @Valid @RequestBody MemberNameUpdateReqDTO request
    ) {
        memberService.updateMemberName(memberId, request);
        return ApiResponse.success("이름이 변경되었습니다.");
    }
}
