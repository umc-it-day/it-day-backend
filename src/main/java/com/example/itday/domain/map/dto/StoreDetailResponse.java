package com.example.itday.domain.map.dto;

import com.example.itday.domain.benefit.entity.Benefit;
import com.example.itday.domain.store.entity.Store;

import java.util.ArrayList;
import java.util.List;

public record StoreDetailResponse(
        Long storeId,
        String kakaoPlaceId,
        String storeName,
        String brandName,
        String category,
        String brandImg,
        String storeImg,
        String address,
        String businessHour,
        String telNum,
        Double longitude,
        Double latitude,
        Integer distanceMeters,
        String placeUrl,
        List<BenefitResponse> benefits
) {

    public static StoreDetailResponse from(
            Store store,
            Integer distanceMeters,
            List<Benefit> benefits
    ) {
        List<BenefitResponse> benefitResponses = new ArrayList<>();

        for (Benefit benefit : benefits) {
            benefitResponses.add(BenefitResponse.from(benefit));
        }

        String placeUrl = "https://place.map.kakao.com/" + store.getKakaoPlaceId();

        return new StoreDetailResponse(
                store.getId(),
                store.getKakaoPlaceId(),
                store.getStoreName(),
                store.getBrand().getBrandName(),
                store.getBrand().getCategory().name(),
                store.getBrand().getBrandImg(),
                store.getStoreImg(),
                store.getAddress(),
                store.getBusinessHour(),
                store.getTelNum(),
                store.getLongitude().doubleValue(),
                store.getLatitude().doubleValue(),
                distanceMeters,
                placeUrl,
                benefitResponses
        );
    }
}
