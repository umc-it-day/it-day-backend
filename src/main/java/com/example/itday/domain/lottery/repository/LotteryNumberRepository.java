package com.example.itday.domain.lottery.repository;

import com.example.itday.domain.lottery.entity.LotteryNumber;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LotteryNumberRepository extends JpaRepository<LotteryNumber,Long> {

    @Query(
            value = """
            SELECT *
            FROM lotteryNumber
            WHERE memberId IS NULL
            ORDER BY RAND()
            LIMIT 1
            FOR UPDATE
            """,
            nativeQuery = true
    )
    Optional<LotteryNumber> findRandomUnassigned();

    Optional<LotteryNumber> findByNumber(String number);

    Optional<LotteryNumber> findByMemberId(Long memberId);
}
