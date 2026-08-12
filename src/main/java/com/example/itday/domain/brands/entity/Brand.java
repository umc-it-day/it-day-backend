package com.example.itday.domain.brands.entity;

import com.example.itday.domain.brands.enums.BrandCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "brand")
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brandId")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "category",nullable = false)
    private BrandCategory category;

    @Column(name = "brandName",nullable = false)
    private String brandName;

    @Column(name = "brandImg")
    private String brandImg;
}
