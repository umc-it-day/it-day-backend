package com.example.itday.domain.map.service;

import com.example.itday.domain.benefit.entity.Benefit;
import com.example.itday.domain.benefit.repository.BenefitRepository;
import com.example.itday.domain.brands.entity.Brand;
import com.example.itday.domain.brands.repository.BrandRepository;
import com.example.itday.domain.map.dto.*;
import com.example.itday.domain.member.entity.Member;
import com.example.itday.domain.member.repository.MemberRepository;
import com.example.itday.domain.store.entity.Store;
import com.example.itday.domain.store.repository.StoreRepository;
import com.example.itday.global.exception.ErrorCode;
import com.example.itday.global.exception.ItDayException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MapService {

    private static final double EARTH_RADIUS_METERS = 6_371_000;
    private static final List<String> KAKAO_NEARBY_CATEGORY_CODES = List.of(
            "MT1",
            "CS2",
            "AC5",
            "OL7",
            "CT1",
            "FD6",
            "CE7",
            "HP8",
            "PM9"
    );

    private final WebClient.Builder webClientBuilder;
    private final StoreRepository storeRepository;
    private final BenefitRepository benefitRepository;
    private final BrandRepository brandRepository;
    private final MemberRepository memberRepository;

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
            log.info(
                    "Kakao local search request started: query={}, hasCoordinates={}, radius={}, page={}, size={}",
                    query,
                    longitude != null && latitude != null,
                    radius,
                    page,
                    size
            );

            KakaoLocalResponse kakaoResponse = webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .header("Authorization", "KakaoAK " + kakaoRestApiKey)
                    .retrieve()
                    .bodyToMono(KakaoLocalResponse.class)
                    .block();

            validateKakaoResponse(kakaoResponse);

            log.info(
                    "Kakao local search request succeeded: documentCount={}, totalCount={}",
                    kakaoResponse.documents().size(),
                    kakaoResponse.meta().totalCount()
            );

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
            log.error(
                    "Kakao local search request failed: status={}, responseBody={}",
                    exception.getStatusCode().value(),
                    exception.getResponseBodyAsString(),
                    exception
            );

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

    @Transactional
    public MapSearchResponse getNearbyPlaces(
            Double longitude,
            Double latitude,
            Integer radius,
            Integer page,
            Integer size
    ) {
        List<Store> stores = storeRepository.findAll();
        List<PlaceResponse> nearbyPlaces = new ArrayList<>();

        for (Store store : stores) {
            Integer distanceMeters = calculateDistanceMeters(
                    longitude,
                    latitude,
                    store.getLongitude().doubleValue(),
                    store.getLatitude().doubleValue()
            );

            if (distanceMeters > radius) {
                continue;
            }

            List<Benefit> benefits =
                    benefitRepository.findAllByBrandId(store.getBrand().getId());
            List<Benefit> activeBenefits = findActiveBenefits(benefits);

            PlaceResponse placeResponse = PlaceResponse.from(
                    store,
                    distanceMeters,
                    activeBenefits
            );
            nearbyPlaces.add(placeResponse);
        }

        if (nearbyPlaces.isEmpty()) {
            nearbyPlaces = searchKakaoNearby(longitude, latitude, radius);
        }

        Collections.sort(nearbyPlaces, new Comparator<PlaceResponse>() {
            @Override
            public int compare(PlaceResponse firstPlace, PlaceResponse secondPlace) {
                return firstPlace.distanceMeters()
                        .compareTo(secondPlace.distanceMeters());
            }
        });

        int totalCount = nearbyPlaces.size();
        int fromIndex = (page - 1) * size;

        if (fromIndex >= totalCount) {
            return MapSearchResponse.of(
                    new ArrayList<>(),
                    page,
                    size,
                    true,
                    totalCount
            );
        }

        int toIndex = Math.min(fromIndex + size, totalCount);
        List<PlaceResponse> pagePlaces =
                new ArrayList<>(nearbyPlaces.subList(fromIndex, toIndex));
        boolean isEnd = toIndex >= totalCount;

        return MapSearchResponse.of(
                pagePlaces,
                page,
                size,
                isEnd,
                totalCount
        );
    }

    private List<PlaceResponse> searchKakaoNearby(
            Double longitude,
            Double latitude,
            Integer radius
    ) {
        List<Brand> brands = new ArrayList<>(brandRepository.findAll());
        sortBrandsByNameLength(brands);

        List<PlaceResponse> places = new ArrayList<>();
        Set<String> placeIds = new HashSet<>();
        Map<Long, List<Benefit>> benefitsByBrandId = new HashMap<>();

        try {
            for (String categoryCode : KAKAO_NEARBY_CATEGORY_CODES) {
                KakaoLocalResponse kakaoResponse = requestKakaoCategory(
                        categoryCode,
                        longitude,
                        latitude,
                        radius
                );

                for (KakaoLocalResponse.Document document : kakaoResponse.documents()) {
                    if (placeIds.contains(document.id())) {
                        continue;
                    }

                    Brand brand = findMatchingBrand(document.placeName(), brands);

                    if (brand == null) {
                        continue;
                    }

                    Store store = findOrCreateStore(document, brand);
                    placeIds.add(document.id());

                    List<Benefit> activeBenefits =
                            benefitsByBrandId.get(store.getBrand().getId());

                    if (activeBenefits == null) {
                        List<Benefit> benefits =
                                benefitRepository.findAllByBrandId(store.getBrand().getId());
                        activeBenefits = findActiveBenefits(benefits);
                        benefitsByBrandId.put(store.getBrand().getId(), activeBenefits);
                    }

                    Integer distanceMeters = calculateDistanceMeters(
                            longitude,
                            latitude,
                            Double.valueOf(document.x()),
                            Double.valueOf(document.y())
                    );

                    PlaceResponse placeResponse = PlaceResponse.from(
                            document,
                            distanceMeters,
                            store,
                            activeBenefits
                    );
                    places.add(placeResponse);
                }
            }

            log.info(
                    "Kakao nearby fallback succeeded: matchedPartnerCount={}",
                    places.size()
            );
            return places;
        } catch (WebClientResponseException exception) {
            log.error(
                    "Kakao nearby fallback failed: status={}, responseBody={}",
                    exception.getStatusCode().value(),
                    exception.getResponseBodyAsString(),
                    exception
            );
            throw new ItDayException(ErrorCode.KAKAO_MAP_API_ERROR, exception);
        }
    }

    private Store findOrCreateStore(
            KakaoLocalResponse.Document document,
            Brand brand
    ) {
        Optional<Store> optionalStore =
                storeRepository.findByKakaoPlaceId(document.id());

        if (optionalStore.isPresent()) {
            return optionalStore.get();
        }

        String address = document.roadAddressName();

        if (address == null || address.isBlank()) {
            address = document.addressName();
        }

        if (address == null) {
            address = "";
        }

        Store store = Store.builder()
                .brand(brand)
                .kakaoPlaceId(document.id())
                .storeName(document.placeName())
                .address(address)
                .storeImg(null)
                .businessHour(null)
                .telNum(document.phone())
                .longitude(new BigDecimal(document.x()))
                .latitude(new BigDecimal(document.y()))
                .build();

        Store savedStore = storeRepository.save(store);

        log.info(
                "Kakao partner store cached: kakaoPlaceId={}, storeName={}",
                savedStore.getKakaoPlaceId(),
                savedStore.getStoreName()
        );
        return savedStore;
    }

    private KakaoLocalResponse requestKakaoCategory(
            String categoryCode,
            Double longitude,
            Double latitude,
            Integer radius
    ) {
        URI uri = createCategoryUri(
                categoryCode,
                longitude,
                latitude,
                radius
        );

        log.info(
                "Kakao nearby category request started: categoryCode={}, radius={}",
                categoryCode,
                radius
        );

        KakaoLocalResponse kakaoResponse = webClientBuilder.build()
                .get()
                .uri(uri)
                .header("Authorization", "KakaoAK " + kakaoRestApiKey)
                .retrieve()
                .bodyToMono(KakaoLocalResponse.class)
                .block();

        validateKakaoResponse(kakaoResponse);
        return kakaoResponse;
    }

    private URI createCategoryUri(
            String categoryCode,
            Double longitude,
            Double latitude,
            Integer radius
    ) {
        return UriComponentsBuilder
                .fromUriString("https://dapi.kakao.com/v2/local/search/category.json")
                .queryParam("category_group_code", categoryCode)
                .queryParam("x", longitude)
                .queryParam("y", latitude)
                .queryParam("radius", radius)
                .queryParam("size", 15)
                .queryParam("sort", "distance")
                .build()
                .encode()
                .toUri();
    }

    private Brand findMatchingBrand(String placeName, List<Brand> brands) {
        String normalizedPlaceName = normalizeName(placeName);

        for (Brand brand : brands) {
            String normalizedBrandName = normalizeName(brand.getBrandName());

            if (normalizedPlaceName.contains(normalizedBrandName)) {
                return brand;
            }
        }

        return null;
    }

    private String normalizeName(String name) {
        return name
                .replace(" ", "")
                .toLowerCase(Locale.ROOT);
    }

    private void sortBrandsByNameLength(List<Brand> brands) {
        Collections.sort(brands, new Comparator<Brand>() {
            @Override
            public int compare(Brand firstBrand, Brand secondBrand) {
                return Integer.compare(
                        secondBrand.getBrandName().length(),
                        firstBrand.getBrandName().length()
                );
            }
        });
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
            Long memberId,
            String category,
            Double longitude,
            Double latitude,
            Integer radius
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ItDayException(ErrorCode.MEMBER_NOT_FOUND));

        Long membershipId = member.getMembership().getId();

        // 내 등급으로 혜택받을 수 있는 브랜드만
        List<Brand> brands = benefitRepository.findAllByMembershipId(membershipId)
                .stream()
                .map(Benefit::getBrand)
                .distinct()
                .toList();

        URI uri = createUri(category, longitude, latitude, radius, 1, 15);

        try {
            log.info(
                    "Kakao category search request started: category={}, hasCoordinates={}, radius={}",
                    category,
                    longitude != null && latitude != null,
                    radius
            );

            KakaoLocalResponse kakaoResponse = webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .header("Authorization", "KakaoAK " + kakaoRestApiKey)
                    .retrieve()
                    .bodyToMono(KakaoLocalResponse.class)
                    .block();

            validateKakaoResponse(kakaoResponse);

            log.info(
                    "Kakao category search request succeeded: documentCount={}, totalCount={}",
                    kakaoResponse.documents().size(),
                    kakaoResponse.meta().totalCount()
            );

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
            log.error(
                    "Kakao category search request failed: status={}, responseBody={}",
                    exception.getStatusCode().value(),
                    exception.getResponseBodyAsString(),
                    exception
            );

            throw new ItDayException(
                    ErrorCode.KAKAO_MAP_API_ERROR,
                    exception);
        }
    }

}
