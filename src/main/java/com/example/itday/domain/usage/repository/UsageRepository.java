package com.example.itday.domain.usage.repository;

import com.example.itday.domain.usage.entity.Usage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageRepository extends JpaRepository<Usage,Long> {
}
