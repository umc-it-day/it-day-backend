package com.example.itday.domain.brands.dto;

import com.example.itday.domain.brands.enums.BrandCategory;

public record BrandResDTO(
        Long brandId,
        String brandName,
        String brandImg,
        BrandCategory category
) {
}
