package com.example.itday.domain.membership.service;

import com.example.itday.domain.benefit.dto.BenefitResDTO;
import com.example.itday.domain.benefit.dto.GradeWithBenefitsResDTO;
import com.example.itday.domain.benefit.repository.BenefitRepository;
import com.example.itday.domain.membership.dto.MembershipGradeResDTO;
import com.example.itday.domain.membership.dto.TelecomResDTO;
import com.example.itday.domain.membership.entity.Membership;
import com.example.itday.domain.membership.enums.Telecom;
import com.example.itday.domain.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final BenefitRepository benefitRepository;

    public List<TelecomResDTO> getTelecoms(){
        return Arrays.stream(Telecom.values())
                .map(t -> new TelecomResDTO(t.name(), t.getLabel()))
                .toList();
    }

    public List<MembershipGradeResDTO> getGrades(Telecom telecom) {
        List<Membership> memberships = membershipRepository.findAllByTelecom(telecom);

        return memberships.stream()
                .map(m -> new MembershipGradeResDTO(
                        m.getId(),
                        m.getTelecomGrade().name(),
                        m.getGradeContent()
                ))
                .toList();
    }

    public List<GradeWithBenefitsResDTO> getAllBenefits() {

        List<Membership> memberships = membershipRepository.findAll();

        return memberships.stream()
                .map(membership -> {
                    List<BenefitResDTO> benefits = benefitRepository.findAllByMembershipId(membership.getId())
                            .stream()
                            .map(benefit -> new BenefitResDTO(
                                    benefit.getBrand().getBrandName(),
                                    benefit.getTitle()
                            ))
                            .toList();

                    return new GradeWithBenefitsResDTO(
                            membership.getTelecom().name(),
                            membership.getTelecomGrade().name(),
                            benefits
                    );
                })
                .toList();
    }
}
