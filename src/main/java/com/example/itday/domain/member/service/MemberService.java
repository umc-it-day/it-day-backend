package com.example.itday.domain.member.service;

import com.example.itday.domain.auth.repository.RefreshTokenRepository;
import com.example.itday.domain.auth.service.KakaoAuthClient;
import com.example.itday.domain.member.dto.MemberInfoResDTO;
import com.example.itday.domain.brands.entity.Brand;
import com.example.itday.domain.brands.repository.BrandRepository;
import com.example.itday.domain.member.dto.MemberNameUpdateReqDTO;
import com.example.itday.domain.member.dto.OnboardingReqDTO;
import com.example.itday.domain.member.entity.Member;
import com.example.itday.domain.member.entity.MemberPreferredBrand;
import com.example.itday.domain.member.entity.MemberTerms;
import com.example.itday.domain.member.repository.MemberPreferredBrandRepository;
import com.example.itday.domain.member.repository.MemberRepository;
import com.example.itday.domain.membership.dto.MembershipSummaryResDTO;
import com.example.itday.domain.membership.dto.MembershipUpdateReqDTO;
import com.example.itday.domain.membership.entity.Membership;
import com.example.itday.domain.member.repository.MemberTermsRepository;
import com.example.itday.domain.membership.repository.MembershipRepository;
import com.example.itday.domain.terms.entity.Terms;
import com.example.itday.domain.terms.repository.TermsRepository;
import com.example.itday.global.exception.ErrorCode;
import com.example.itday.global.exception.ItDayException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KakaoAuthClient kakaoAuthClient;
    private final TermsRepository termsRepository;
    private final MemberTermsRepository memberTermsRepository;
    private final MembershipRepository membershipRepository;
    private final BrandRepository brandRepository;
    private final MemberPreferredBrandRepository memberPreferredBrandRepository;

    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ItDayException(ErrorCode.MEMBER_NOT_FOUND));

        kakaoAuthClient.unlink(member.getSocialId());
        refreshTokenRepository.deleteByMemberId(memberId);
        memberRepository.delete(member);
    }

    public MembershipSummaryResDTO getMembershipSummary(Long memberId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ItDayException(ErrorCode.MEMBER_NOT_FOUND));

        Membership membership = member.getMembership();

        return new MembershipSummaryResDTO(membership.getTelecom().getLabel(),membership.getTelecomGrade().name());
    }

    public MemberInfoResDTO getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new ItDayException(ErrorCode.MEMBER_NOT_FOUND));
        return new MemberInfoResDTO(member.getName(),member.getEmail());
    }

    @Transactional
    public void submitOnboarding(Long memberId, OnboardingReqDTO request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ItDayException(ErrorCode.MEMBER_NOT_FOUND));

        List<MemberTerms> memberTermsList = request.termAgreements().stream()
                .map(agreement -> {
                    Terms terms = termsRepository.findById(agreement.termId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약관입니다."));

                    return MemberTerms.builder()
                            .member(member)
                            .terms(terms)
                            .isAgree(agreement.isAgree())
                            .agreeAt(LocalDateTime.now())
                            .build();
                })
                .toList();

        memberTermsRepository.saveAll(memberTermsList);

        Membership membership = membershipRepository.findById(request.membershipId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버십입니다."));
        member.setMembership(membership);

        List<MemberPreferredBrand> preferredBrands = request.preferredBrandIds().stream()
                .map(brandId -> {
                    Brand brand = brandRepository.findById(brandId)
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));
                    return MemberPreferredBrand.builder()
                            .member(member)
                            .brand(brand)
                            .build();
                })
                .toList();
        memberPreferredBrandRepository.saveAll(preferredBrands);
    }

    @Transactional
    public void updateMembership(Long memberId, MembershipUpdateReqDTO request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new ItDayException(ErrorCode.MEMBER_NOT_FOUND));

        Membership membership = membershipRepository.findById(request.membershipId())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 멤버십입니다."));
        member.setMembership(membership);
    }

    @Transactional
    public void updateMemberName(Long memberId, MemberNameUpdateReqDTO request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new ItDayException(ErrorCode.MEMBER_NOT_FOUND));
        member.setName(request.name());
    }
}
