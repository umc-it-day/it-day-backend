package com.example.itday.domain.store.entity;

import com.example.itday.domain.brands.entity.Brand;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "store",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_store_kakao_place_id",
                columnNames = "kakaoPlaceId"
        )
)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storeId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brandId", nullable = false)
    private Brand brand;

    @Column(name = "kakaoPlaceId", nullable = false, length = 50)
    private String kakaoPlaceId;

    @Column(name = "storeName", nullable = false)
    private String storeName;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "storeImg")
    private String storeImg;

    @Column(name = "businessHour")
    private String businessHour;

    @Column(name = "telNum")
    private String telNum;

    @Column(name = "longitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "latitude", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitude;
}
