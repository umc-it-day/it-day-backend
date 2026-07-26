package com.example.itday.domain.barcode.service;

import com.example.itday.domain.barcode.dto.BarcodeReqDTO;
import com.example.itday.domain.barcode.dto.BarcodeResDTO;
import com.example.itday.domain.barcode.entity.Barcode;
import com.example.itday.domain.barcode.exception.DuplicateBarcodeException;
import com.example.itday.domain.barcode.repository.BarcodeRepository;
import com.example.itday.domain.member.entity.Member;
import com.example.itday.domain.member.repository.MemberRepository;
import com.example.itday.global.apiPayload.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BarcodeService {

    private final BarcodeRepository barcodeRepository;
    private final MemberRepository memberRepository;

    public void registerBarcode(Long memberId, BarcodeReqDTO request) {
        if (barcodeRepository.existsByBarcodeNum(request.barcodeNum())) {
            throw new DuplicateBarcodeException(ErrorCode.DUPLICATE_BARCODE);
        }
        Member member = memberRepository.findById(memberId).orElseThrow();

        Barcode barcode = Barcode
                .builder()
                .member(member)
                .barcodeNum(request.barcodeNum())
                .build();

        barcodeRepository.save(barcode);
    }

    @Transactional
    public void updateBarcode(Long memberId, BarcodeReqDTO request){
        Barcode barcode = barcodeRepository.findByMemberId(memberId)
                .orElseThrow(()->new IllegalArgumentException("등록된 바코드가 없습니다."));
        if (!barcode.getBarcodeNum().equals(request.barcodeNum())
                && barcodeRepository.existsByBarcodeNum(request.barcodeNum())) {
            throw new DuplicateBarcodeException(ErrorCode.DUPLICATE_BARCODE);
        }
        barcode.setBarcodeNum(request.barcodeNum());
    }

    public BarcodeResDTO getBarcode(Long memberId){
        return barcodeRepository.findByMemberId(memberId)
                .map(barcode -> new BarcodeResDTO(barcode.getBarcodeNum()))
                .orElse(null);
    }
}
