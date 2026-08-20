package com.example.itday.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoUserInfoResDTO(
        Long id,
        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            String email,
            Profile profile   // profile이라는 "객체"를 그대로 받음
    ) {
        public record Profile(
                String nickname   // 그 안에 nickname이 있음
        ) {}
    }
}