package com.example.itday.domain.map.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record KakaoLocalResponse(
        List<Document> documents,
        Meta meta
) {

    public record Document(
            String id,
            @JsonProperty("place_name")
            String placeName,
            @JsonProperty("category_name")
            String categoryName,
            @JsonProperty("address_name")
            String addressName,
            @JsonProperty("road_address_name")
            String roadAddressName,
            String x,
            String y,
            String phone,
            @JsonProperty("place_url")
            String placeUrl
    ) {
    }

    public record Meta(
            @JsonProperty("total_count")
            Integer totalCount,
            @JsonProperty("pageable_count")
            Integer pageableCount,
            @JsonProperty("is_end")
            Boolean isEnd
    ) {
    }
}
