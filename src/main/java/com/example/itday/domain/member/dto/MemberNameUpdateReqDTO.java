package com.example.itday.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record MemberNameUpdateReqDTO(
        @NotBlank(message = "이름은 비어있을 수 없습니다.")
        String name
) {

}
