package com.example.itday.domain.map.dto;

import java.util.List;

public record NearbyPlaceResDTO(
        String placeName,
        String brandImg,
        Integer distanceMeters,
        List<String> benefitTitles
) {
}
