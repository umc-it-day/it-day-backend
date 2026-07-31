package com.example.itday.domain.challenge.repository;

import com.example.itday.domain.challenge.entity.VisitChallengeProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitChallengeProgressRepository
        extends JpaRepository<VisitChallengeProgress, Long> {

    Optional<VisitChallengeProgress> findByUserId(
            Long userId
    );
}
