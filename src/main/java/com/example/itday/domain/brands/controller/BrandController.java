package com.example.itday.domain.brands.controller;

import com.example.itday.domain.brands.dto.BrandResDTO;
import com.example.itday.domain.brands.service.BrandService;
import com.example.itday.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping()
    public ApiResponse<List<BrandResDTO>> getBrands(){
        List<BrandResDTO> brandList = brandService.getBrandList();
        return ApiResponse.success("브랜드를 조회 완료", brandList);
    }

}
