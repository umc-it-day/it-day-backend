package com.example.itday.domain.member.repository;

import com.example.itday.domain.member.entity.MemberPreferredBrand;
import com.example.itday.domain.member.entity.MemberPreferredBrandId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberPreferredBrandRepository extends JpaRepository<MemberPreferredBrand, MemberPreferredBrandId> {

}
