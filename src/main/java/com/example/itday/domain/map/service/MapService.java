package com.example.itday.domain.map.service;

import com.example.itday.domain.benefit.entity.Benefit;
import com.example.itday.domain.benefit.repository.BenefitRepository;
import com.example.itday.domain.brands.entity.Brand;
import com.example.itday.domain.brands.repository.BrandRepository;
import com.example.itday.domain.map.dto.*;
import com.example.itday.domain.store.entity.Store;
import com.example.itday.domain.store.repository.StoreRepository;
import com.example.itday.global.exception.ErrorCode;
import com.example.itday.global.exception.ItDayException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MapService {

    private static final double EARTH_RADIUS_METERS = 6_371_000;

    private final WebClient.Builder webClientBuilder;
    private final StoreRepository storeRepository;
    private final BenefitRepository benefitRepository;
    private final BrandRepository brandRepository;

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    public MapSearchResponse searchPlaces(
            String query,
            Double longitude,
            Double latitude,
            Integer radius,
            Integer page,
            Integer size
    ) {
        URI uri = createUri(query, longitude, latitude, radius, page, size);

        try {
            KakaoLocalResponse kakaoResponse = webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .header("Authorization", "KakaoAK " + kakaoRestApiKey)
                    .retrieve()
                    .bodyToMono(KakaoLocalResponse.class)
                    .block();

            validateKakaoResponse(kakaoResponse);

            List<PlaceResponse> places = new ArrayList<>();

            for (KakaoLocalResponse.Document document : kakaoResponse.documents()) {
                Optional<Store> optionalStore =
                        storeRepository.findByKakaoPlaceId(document.id());

                if (optionalStore.isEmpty()) {
                    continue;
                }

                Store store = optionalStore.get();
                Integer distanceMeters = null;

                if (longitude != null && latitude != null) {
                    distanceMeters = calculateDistanceMeters(
                            longitude,
                            latitude,
                            Double.valueOf(document.x()),
                            Double.valueOf(document.y())
                    );
                }

                List<Benefit> benefits =
                        benefitRepository.findAllByBrandId(store.getBrand().getId());
                List<Benefit> activeBenefits = findActiveBenefits(benefits);

                PlaceResponse placeResponse = PlaceResponse.from(
                        document,
                        distanceMeters,
                        store,
                        activeBenefits
                );
                places.add(placeResponse);
            }

            return MapSearchResponse.of(
                    places,
                    page,
                    size,
                    kakaoResponse.meta().isEnd(),
                    kakaoResponse.meta().totalCount()
            );
        } catch (WebClientResponseException exception) {
            throw new ItDayException(ErrorCode.KAKAO_MAP_API_ERROR,
                    exception
            );
        }
    }

    public StoreDetailResponse getStoreDetail(
            Long storeId,
            Double longitude,
            Double latitude
    ) {
        Optional<Store> optionalStore = storeRepository.findById(storeId);

        if (optionalStore.isEmpty()) {
            throw new ItDayException(ErrorCode.STORE_NOT_FOUND, "storeId=" + storeId);
        }

        Store store = optionalStore.get();

        List<Benefit> benefits =
                benefitRepository.findAllByBrandId(store.getBrand().getId());
        List<Benefit> activeBenefits = findActiveBenefits(benefits);
        Integer distanceMeters = null;

        if (longitude != null && latitude != null) {
            distanceMeters = calculateDistanceMeters(
                    longitude,
                    latitude,
                    store.getLongitude().doubleValue(),
                    store.getLatitude().doubleValue()
            );
        }

        return StoreDetailResponse.from(store, distanceMeters, activeBenefits);
    }

    private List<Benefit> findActiveBenefits(List<Benefit> benefits) {
        List<Benefit> activeBenefits = new ArrayList<>();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

        for (Benefit benefit : benefits) {
            boolean hasStarted = !benefit.getValidFrom().isAfter(today);
            boolean notExpired = !benefit.getValidTo().isBefore(today);

            if (hasStarted && notExpired) {
                activeBenefits.add(benefit);
            }
        }

        return activeBenefits;
    }

    private void validateKakaoResponse(KakaoLocalResponse kakaoResponse) {
        if (kakaoResponse == null
                || kakaoResponse.documents() == null
                || kakaoResponse.meta() == null) {
            throw new ItDayException(
                    ErrorCode.KAKAO_MAP_API_ERROR,
                    "The Kakao local search API returned an invalid response."
            );
        }
    }

    private URI createUri(
            String query,
            Double longitude,
            Double latitude,
            Integer radius,
            Integer page,
            Integer size
    ) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/keyword.json")
                .queryParam("query", query)
                .queryParam("page", page)
                .queryParam("size", size);

        if (longitude != null && latitude != null) {
            builder.queryParam("x", longitude);
            builder.queryParam("y", latitude);

            if (radius != null) {
                builder.queryParam("radius", radius);
            }
        }

        return builder.build().encode().toUri();
    }

    private Integer calculateDistanceMeters(
            Double currentLongitude,
            Double currentLatitude,
            Double placeLongitude,
            Double placeLatitude
    ) {
        double currentLatitudeRadian = Math.toRadians(currentLatitude);
        double placeLatitudeRadian = Math.toRadians(placeLatitude);
        double latitudeDifference =
                Math.toRadians(placeLatitude - currentLatitude);
        double longitudeDifference =
                Math.toRadians(placeLongitude - currentLongitude);

        double haversineValue =
                Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
                        + Math.cos(currentLatitudeRadian)
                        * Math.cos(placeLatitudeRadian)
                        * Math.sin(longitudeDifference / 2)
                        * Math.sin(longitudeDifference / 2);

        double centralAngle = 2 * Math.atan2(
                Math.sqrt(haversineValue),
                Math.sqrt(1 - haversineValue)
        );

        return (int) Math.round(EARTH_RADIUS_METERS * centralAngle);
    }

    public List<NearbyPlaceResDTO> searchByCategory(
            String category,
            Double longitude,
            Double latitude,
            Integer radius
    ) {
        List<Brand> brands = brandRepository.findAll();

        URI uri = createUri(category, longitude, latitude, radius, 1, 15);

        try {
            KakaoLocalResponse kakaoResponse = webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .header("Authorization", "KakaoAK " + kakaoRestApiKey)
                    .retrieve()
                    .bodyToMono(KakaoLocalResponse.class)
                    .block();

            validateKakaoResponse(kakaoResponse);

            List<NearbyPlaceResDTO> results = new ArrayList<>();

            for (KakaoLocalResponse.Document document : kakaoResponse.documents()) {

                Brand matchedBrand = brands.stream()
                        .filter(brand -> document.placeName().contains(brand.getBrandName()))
                        .findFirst()
                        .orElse(null);

                if (matchedBrand == null) {
                    continue;
                }

                Integer distanceMeters = (longitude != null && latitude != null)
                        ? calculateDistanceMeters(longitude, latitude, Double.valueOf(document.x()), Double.valueOf(document.y()))
                        : null;

                List<String> benefitTitles = findActiveBenefits(benefitRepository.findAllByBrandId(matchedBrand.getId()))
                        .stream()
                        .map(Benefit::getTitle)
                        .toList();

                results.add(new NearbyPlaceResDTO(
                        document.placeName(),
                        matchedBrand.getBrandImg(),
                        distanceMeters,
                        benefitTitles
                ));
            }

            return results;

        } catch (WebClientResponseException exception) {
            throw new ItDayException(
                    ErrorCode.KAKAO_MAP_API_ERROR,
                    exception);
        }
    }

}
