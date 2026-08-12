package com.example.itday.domain.map.dto;

import java.util.List;

public record MapSearchResponse(
        List<PlaceResponse> places,
        Integer page,
        Integer size,
        Boolean isEnd,
        Integer totalCount
) {

    public static MapSearchResponse of(
            List<PlaceResponse> places,
            Integer page,
            Integer size,
            Boolean isEnd,
            Integer totalCount
    ) {
        return new MapSearchResponse(places, page, size, isEnd, totalCount);
    }
}
