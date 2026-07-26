package com.example.itday.domain.barcode.repository;

import com.example.itday.domain.barcode.entity.Barcode;
import com.example.itday.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BarcodeRepository extends JpaRepository<Barcode, Long> {

    boolean existsByBarcodeNum(String barcodeNum);

    Optional<Barcode> findByMemberId(Long memberId);
}
