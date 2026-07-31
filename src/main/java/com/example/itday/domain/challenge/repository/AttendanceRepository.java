package com.example.itday.domain.challenge.repository;


import com.example.itday.domain.challenge.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Long> {

    boolean existsByUserIdAndAttendanceDate(
            Long userId,
            LocalDate attendanceDate
    );

    Optional<Attendance>
    findTopByUserIdOrderByAttendanceDateDesc(Long userId);

    List<Attendance>
    findAllByUserIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    long countByUserIdAndAttendanceDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );

    @Query("""
        select coalesce(sum(p.point), 0)
        from PointHistory p
        where p.userId = :userId
          and p.createdAt >= :startDateTime
          and p.createdAt < :endDateTime
        """)
    Integer sumPointByUserIdAndPeriod(
            @Param("userId") Long userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    int findMaxConsecutiveDays(Long userId, LocalDate startDate, LocalDate endDate);
}
