package com.example.itday.domain.member.repository;

import com.example.itday.domain.member.entity.MemberTerms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberTermsRepository extends JpaRepository<MemberTerms, Long> {
}
