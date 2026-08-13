package com.example.itday.domain.lottery.service;

import com.example.itday.domain.lottery.dto.LotteryNumResDTO;
import com.example.itday.domain.lottery.dto.LotteryResDTO;
import com.example.itday.domain.lottery.entity.LotteryNumber;
import com.example.itday.domain.lottery.repository.LotteryNumberRepository;
import com.example.itday.global.exception.ErrorCode;
import com.example.itday.global.exception.ItDayException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LotteryService {

    private final LotteryNumberRepository lotteryNumberRepository;

    public LotteryResDTO checkRanking(String number) {
        LotteryNumber lotteryNumber = lotteryNumberRepository.findByNumber(number)
                .orElseThrow(() -> new ItDayException(ErrorCode.LOTTERY_NUMBER_NOT_FOUND));

        return new LotteryResDTO(lotteryNumber.getRanking());
    }

    public LotteryNumResDTO getMyLotteryNumber(Long memberId) {

        LotteryNumber lotteryNumber = lotteryNumberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new ItDayException(ErrorCode.LOTTERY_NUMBER_NOT_FOUND));

        return new LotteryNumResDTO(lotteryNumber.getNumber());
    }

    @Transactional
    public LotteryNumber assignLotteryNumber(Long memberId) {
        LotteryNumber lotteryNumber = lotteryNumberRepository.findRandomUnassigned()
                .orElseThrow(() -> new IllegalStateException("남은 복권 번호가 없습니다."));

        lotteryNumber.assignMember(memberId);

        return lotteryNumber;
    }
}
