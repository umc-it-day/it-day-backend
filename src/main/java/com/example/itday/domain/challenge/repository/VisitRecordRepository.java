package com.example.itday.domain.challenge.repository;


import com.example.itday.domain.challenge.entity.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VisitRecordRepository
        extends JpaRepository<VisitRecord, Long> {

    long countByUserIdAndVisitedAtGreaterThanEqualAndVisitedAtLessThan(
            Long userId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
}
