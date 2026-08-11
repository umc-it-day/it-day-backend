package com.example.itday.domain.terms.controller;

import com.example.itday.domain.terms.dto.TermsResDTO;
import com.example.itday.domain.terms.service.TermsService;
import com.example.itday.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/terms")
public class TermsController {

    private final TermsService termsService;

    @GetMapping
    public ApiResponse<List<TermsResDTO>> getTerms(){
        List<TermsResDTO> result = termsService.getTerms();
        return ApiResponse.success("약관 조회 완료",result);
    }
}
