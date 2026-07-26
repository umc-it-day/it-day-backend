package com.example.itday.domain.brands.service;

import com.example.itday.domain.brands.dto.BrandResDTO;
import com.example.itday.domain.brands.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    public List<BrandResDTO> getBrandList() {

        List<BrandResDTO> brandList = brandRepository.findAll().stream().map(
                brand -> new BrandResDTO(
                        brand.getId(),
                        brand.getBrandName(),
                        brand.getBrandImg(),
                        brand.getCategory()
                )
        ).toList();

        return brandList;
    }
}
