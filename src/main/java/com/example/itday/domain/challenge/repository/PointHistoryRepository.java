package com.example.itday.domain.challenge.repository;


import com.example.itday.domain.challenge.entity.PointHistory;
import com.example.itday.domain.challenge.entity.PointReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface PointHistoryRepository
        extends JpaRepository<PointHistory, Long> {

    boolean existsByUserIdAndReasonAndRewardMonth(
            Long userId,
            PointReason reason,
            LocalDate rewardMonth
    );

    boolean existsByUserIdAndReason(
            Long userId,
            PointReason reason
    );

    int countByUserId(Long userId);

    @Query("""
        select coalesce(sum(p.point), 0)
        from PointHistory p
        where p.userId = :userId
        """)
    Integer sumPointByUserId(@Param("userId") Long userId);

    int sumPointByUserIdAndMonth(Long userId, LocalDate monthStart, LocalDate monthEnd);

    int sumByUserIdAndReasonsAndPeriod(Long userId, List<PointReason> visitReasons, LocalDateTime startDateTime, LocalDateTime endDateTimeExclusive);

    int sumByUserIdAndPeriod(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTimeExclusive);
}
