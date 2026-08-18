package com.example.itday.domain.map.service;

import com.example.itday.domain.benefit.repository.BenefitRepository;
import com.example.itday.domain.brands.entity.Brand;
import com.example.itday.domain.brands.enums.BrandCategory;
import com.example.itday.domain.brands.repository.BrandRepository;
import com.example.itday.domain.map.dto.KakaoLocalResponse;
import com.example.itday.domain.map.dto.MapSearchResponse;
import com.example.itday.domain.map.dto.PlaceResponse;
import com.example.itday.domain.store.entity.Store;
import com.example.itday.domain.store.repository.StoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MapServiceTest {

    private StoreRepository storeRepository;
    private BenefitRepository benefitRepository;
    private BrandRepository brandRepository;
    private WebClient.ResponseSpec responseSpec;
    private MapService mapService;

    @BeforeEach
    void setUp() {
        storeRepository = mock(StoreRepository.class);
        benefitRepository = mock(BenefitRepository.class);
        brandRepository = mock(BrandRepository.class);

        WebClient.Builder webClientBuilder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec =
                mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec =
                mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), any(String[].class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        mapService = new MapService(
                webClientBuilder,
                storeRepository,
                benefitRepository,
                brandRepository
        );
        ReflectionTestUtils.setField(mapService, "kakaoRestApiKey", "test-key");
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

    @Test
    void getNearbyPlacesUsesKakaoWhenDatabaseResultIsEmpty() {
        Brand brand = Brand.builder()
                .id(1L)
                .category(BrandCategory.CAFE)
                .brandName("스타벅스")
                .brandImg("brand-image")
                .build();

        KakaoLocalResponse.Document document = new KakaoLocalResponse.Document(
                "kakao-place-id",
                "스타벅스 테스트점",
                "음식점 > 카페",
                "서울 테스트 주소",
                "서울 테스트 도로명 주소",
                "127.0",
                "37.0005",
                "02-0000-0000",
                "https://place.map.kakao.com/kakao-place-id"
        );
        KakaoLocalResponse kakaoResponse = new KakaoLocalResponse(
                List.of(document),
                new KakaoLocalResponse.Meta(1, 1, true)
        );
        Store savedStore = createStore(
                10L,
                "kakao-place-id",
                127.0,
                37.0005,
                brand
        );

        when(storeRepository.findAll()).thenReturn(Collections.emptyList());
        when(storeRepository.findByKakaoPlaceId("kakao-place-id"))
                .thenReturn(java.util.Optional.empty());
        when(storeRepository.save(any(Store.class))).thenReturn(savedStore);
        when(brandRepository.findAll()).thenReturn(List.of(brand));
        when(benefitRepository.findAllByBrandId(1L))
                .thenReturn(Collections.emptyList());
        when(responseSpec.bodyToMono(KakaoLocalResponse.class))
                .thenReturn(Mono.just(kakaoResponse));

        MapSearchResponse response = mapService.getNearbyPlaces(
                127.0,
                37.0,
                1000,
                1,
                15
        );

        assertThat(response.totalCount()).isEqualTo(1);
        assertThat(response.places()).hasSize(1);
        assertThat(response.places().get(0).placeName())
                .isEqualTo("스타벅스 테스트점");
        assertThat(response.places().get(0).storeId()).isEqualTo(10L);
        assertThat(response.places().get(0).partnerStore()).isTrue();
        verify(storeRepository, times(1)).save(any(Store.class));
        verify(responseSpec, times(9)).bodyToMono(KakaoLocalResponse.class);
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
