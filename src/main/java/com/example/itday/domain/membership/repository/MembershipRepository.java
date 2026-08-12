package com.example.itday.domain.membership.repository;

import com.example.itday.domain.membership.entity.Membership;
import com.example.itday.domain.membership.enums.Telecom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipRepository extends JpaRepository<Membership,Long> {

    List<Membership> findAllByTelecom(Telecom telecom);
}
