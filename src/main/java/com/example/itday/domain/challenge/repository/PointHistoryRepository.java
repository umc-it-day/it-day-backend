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

    @Query("""
        select coalesce(sum(p.point), 0)
        from PointHistory p
        where p.userId = :userId
          and function('date', p.createdAt) between :monthStart and :monthEnd
        """)
    Integer sumPointByUserIdAndMonth(
            @Param("userId") Long userId,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd
    );

    @Query("""
        select coalesce(sum(p.point), 0)
        from PointHistory p
        where p.userId = :userId
          and p.reason in :visitReasons
          and p.createdAt >= :startDateTime
          and p.createdAt < :endDateTimeExclusive
        """)
    Integer sumByUserIdAndReasonsAndPeriod(
            @Param("userId") Long userId,
            @Param("visitReasons") List<PointReason> visitReasons,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTimeExclusive") LocalDateTime endDateTimeExclusive
    );

    @Query("""
        select coalesce(sum(p.point), 0)
        from PointHistory p
        where p.userId = :userId
          and p.createdAt >= :startDateTime
          and p.createdAt < :endDateTimeExclusive
        """)
    Integer sumByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTimeExclusive") LocalDateTime endDateTimeExclusive
    );
}
