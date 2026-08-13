package com.example.itday.domain.map.dto;

import com.example.itday.domain.benefit.entity.Benefit;
import com.example.itday.domain.store.entity.Store;

import java.util.ArrayList;
import java.util.List;

public record PlaceResponse(
        String kakaoPlaceId,
        Long storeId,
        Boolean partnerStore,
        String placeName,
        String brandName,
        String categoryName,
        String addressName,
        String roadAddressName,
        Double longitude,
        Double latitude,
        Integer distanceMeters,
        String phone,
        String placeUrl,
        String brandImg,
        String storeImg,
        String businessHour,
        List<BenefitResponse> benefits
) {

    public static PlaceResponse from(
            KakaoLocalResponse.Document document,
            Integer distanceMeters,
            Store store,
            List<Benefit> benefits
    ) {
        List<BenefitResponse> benefitResponses = new ArrayList<>();

        for (Benefit benefit : benefits) {
            benefitResponses.add(BenefitResponse.from(benefit));
        }

        return new PlaceResponse(
                document.id(),
                store.getId(),
                true,
                document.placeName(),
                store.getBrand().getBrandName(),
                document.categoryName(),
                document.addressName(),
                document.roadAddressName(),
                Double.valueOf(document.x()),
                Double.valueOf(document.y()),
                distanceMeters,
                document.phone(),
                document.placeUrl(),
                store.getBrand().getBrandImg(),
                store.getStoreImg(),
                store.getBusinessHour(),
                benefitResponses
        );
    }

    public static PlaceResponse from(
            Store store,
            Integer distanceMeters,
            List<Benefit> benefits
    ) {
        List<BenefitResponse> benefitResponses = new ArrayList<>();

        for (Benefit benefit : benefits) {
            benefitResponses.add(BenefitResponse.from(benefit));
        }

        String placeUrl = "https://place.map.kakao.com/" + store.getKakaoPlaceId();

        return new PlaceResponse(
                store.getKakaoPlaceId(),
                store.getId(),
                true,
                store.getStoreName(),
                store.getBrand().getBrandName(),
                store.getBrand().getCategory().name(),
                store.getAddress(),
                store.getAddress(),
                store.getLongitude().doubleValue(),
                store.getLatitude().doubleValue(),
                distanceMeters,
                store.getTelNum(),
                placeUrl,
                store.getBrand().getBrandImg(),
                store.getStoreImg(),
                store.getBusinessHour(),
                benefitResponses
        );
    }
}
