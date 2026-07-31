package com.example.itday.domain.report.repository;

import com.example.itday.domain.report.entity.Report;
import com.example.itday.domain.report.entity.ReportPeriodType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReportRepository
        extends JpaRepository<Report, Long> {

    boolean existsByUserIdAndPeriodTypeAndStartDateAndEndDate(
            Long userId,
            ReportPeriodType periodType,
            LocalDate startDate,
            LocalDate endDate
    );

    List<Report> findAllByUserIdOrderByStartDateDesc(
            Long userId
    );

    List<Report>
    findAllByUserIdAndPeriodTypeOrderByStartDateDesc(
            Long userId,
            ReportPeriodType periodType
    );

    Optional<Report> findByIdAndUserId(
            Long reportId,
            Long userId
    );
}
