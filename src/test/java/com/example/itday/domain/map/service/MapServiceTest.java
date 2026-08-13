package com.example.itday.domain.map.service;

import com.example.itday.domain.benefit.repository.BenefitRepository;
import com.example.itday.domain.brands.entity.Brand;
import com.example.itday.domain.brands.enums.BrandCategory;
import com.example.itday.domain.brands.repository.BrandRepository;
import com.example.itday.domain.map.dto.MapSearchResponse;
import com.example.itday.domain.map.dto.PlaceResponse;
import com.example.itday.domain.store.entity.Store;
import com.example.itday.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MapServiceTest {

    private StoreRepository storeRepository;
    private BenefitRepository benefitRepository;
    private MapService mapService;

    @BeforeEach
    void setUp() {
        storeRepository = mock(StoreRepository.class);
        benefitRepository = mock(BenefitRepository.class);
        BrandRepository brandRepository = mock(BrandRepository.class);

        mapService = new MapService(
                WebClient.builder(),
                storeRepository,
                benefitRepository,
                brandRepository
        );
    }

    @Test
    void getNearbyPlacesFiltersByRadiusAndSortsByDistance() {
        Brand brand = Brand.builder()
                .id(1L)
                .category(BrandCategory.CAFE)
                .brandName("테스트 브랜드")
                .build();

        Store nearStore = createStore(1L, "near", 127.0, 37.0005, brand);
        Store farStore = createStore(2L, "far", 127.0, 37.0050, brand);
        Store outsideStore = createStore(3L, "outside", 127.0, 37.0200, brand);

        when(storeRepository.findAll())
                .thenReturn(List.of(farStore, outsideStore, nearStore));
        when(benefitRepository.findAllByBrandId(1L))
                .thenReturn(Collections.emptyList());

        MapSearchResponse response = mapService.getNearbyPlaces(
                127.0,
                37.0,
                1000,
                1,
                15
        );

        assertThat(response.totalCount()).isEqualTo(2);
        assertThat(response.places())
                .extracting(PlaceResponse::kakaoPlaceId)
                .containsExactly("near", "far");
        assertThat(response.places().get(0).distanceMeters())
                .isLessThan(response.places().get(1).distanceMeters());
    }

    private Store createStore(
            Long storeId,
            String kakaoPlaceId,
            Double longitude,
            Double latitude,
            Brand brand
    ) {
        return Store.builder()
                .id(storeId)
                .brand(brand)
                .kakaoPlaceId(kakaoPlaceId)
                .storeName("테스트 매장")
                .address("테스트 주소")
                .longitude(BigDecimal.valueOf(longitude))
                .latitude(BigDecimal.valueOf(latitude))
                .build();
    }
}
