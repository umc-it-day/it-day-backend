package com.example.itday.domain.member.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MemberPreferredBrandId implements Serializable {

    private Long brandId;
    private Long memberId;
}
