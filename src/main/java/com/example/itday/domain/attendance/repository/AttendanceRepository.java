package com.example.itday.domain.attendance.repository;

import com.example.itday.domain.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance,Long> {

    boolean existsByMemberIdAndAttendedAt(Long memberId, LocalDate attendedAt);

    List<Attendance> findAllByMemberIdAndAttendedAtBetween(Long memberId, LocalDate start, LocalDate end);
}
