package com.example.itday.domain.challenge.repository;

import com.example.itday.domain.challenge.entity.TowerProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TowerProgressRepository
        extends JpaRepository<TowerProgress, Long> {

    Optional<TowerProgress> findByUserId(
            Long userId
    );
}
