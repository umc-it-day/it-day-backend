package com.example.itday.domain.terms.repository;

import com.example.itday.domain.terms.entity.Terms;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermsRepository extends JpaRepository<Terms,Long> {

}
