package com.example.itday.domain.map.controller;

import com.example.itday.domain.map.dto.MapSearchResponse;
import com.example.itday.domain.map.dto.NearbyPlaceResDTO;
import com.example.itday.domain.map.dto.StoreDetailResponse;
import com.example.itday.domain.map.service.MapService;
import com.example.itday.global.response.ApiResponse;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/maps")
@Validated
public class MapController {

    private final MapService mapService;

    @GetMapping("/search")
    public ApiResponse<MapSearchResponse> searchPlaces(
            @RequestParam @NotBlank String query,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) @Min(0) @Max(20_000) Integer radius,
            @RequestParam(defaultValue = "1") @Min(1) Integer page,
            @RequestParam(defaultValue = "15") @Min(1) @Max(15) Integer size
    ) {
        MapSearchResponse result = mapService.searchPlaces(
                query,
                longitude,
                latitude,
                radius,
                page,
                size
        );
        return ApiResponse.success(result);
    }

    @GetMapping("/stores/{storeId}")
    public ApiResponse<StoreDetailResponse> getStoreDetail(
            @PathVariable Long storeId,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double latitude
    ) {
        StoreDetailResponse result =
                mapService.getStoreDetail(storeId, longitude, latitude);
        return ApiResponse.success(result);
    }

    @GetMapping("/nearby")
    public ApiResponse<MapSearchResponse> getNearbyPlaces(
            @RequestParam
            @DecimalMin("-180.0")
            @DecimalMax("180.0")
            Double longitude,

            @RequestParam
            @DecimalMin("-90.0")
            @DecimalMax("90.0")
            Double latitude,

            @RequestParam(defaultValue = "1000")
            @Min(1)
            @Max(20_000)
            Integer radius,

            @RequestParam(defaultValue = "1")
            @Min(1)
            Integer page,

            @RequestParam(defaultValue = "15")
            @Min(1)
            @Max(15)
            Integer size
    ) {
        MapSearchResponse result = mapService.getNearbyPlaces(
                longitude,
                latitude,
                radius,
                page,
                size
        );
        return ApiResponse.success(result);
    }

    @GetMapping("/stores/search")
    public ApiResponse<List<NearbyPlaceResDTO>> searchByCategory(
            @RequestParam String category,
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam(required = false) Integer radius
    ) {
        List<NearbyPlaceResDTO> result = mapService.searchByCategory(category, longitude, latitude, radius);
        return ApiResponse.success(result);
    }


}
