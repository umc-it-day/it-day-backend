package com.example.itday.domain.benefit.repository;

import com.example.itday.domain.benefit.entity.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {

    List<Benefit> findAllByMembershipId(Long membershipId);

    List<Benefit> findAllByBrandId(Long brandId);
}
