package com.example.itday.domain.point.repository;

import com.example.itday.domain.point.entity.PointHistory;
import com.example.itday.domain.point.enums.PointReason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PointHistoryRepository extends JpaRepository<PointHistory,Long> {

    boolean existsByMemberIdAndReasonAndCreatedAtBetween(
            Long memberId, PointReason reason, LocalDate start, LocalDate end
    );

    List<PointHistory> findAllByMemberIdAndCreatedAtBetween(Long memberId, LocalDate start, LocalDate end);

    Optional<PointHistory> findByMemberIdAndReasonAndCreatedAtBetween(
            Long memberId, PointReason reason, LocalDate start, LocalDate end
    );
}
