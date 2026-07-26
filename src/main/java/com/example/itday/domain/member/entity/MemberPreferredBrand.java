package com.example.itday.domain.member.entity;

import com.example.itday.domain.brands.entity.Brand;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "memberpreferredbrand")
public class MemberPreferredBrand {

    @EmbeddedId
    @Builder.Default
    private MemberPreferredBrandId id = new MemberPreferredBrandId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("brandId")
    @JoinColumn(name = "brandId")
    private Brand brand;
}