package com.example.itday.domain.lottery.service;

import com.example.itday.domain.lottery.dto.LotteryResDTO;
import com.example.itday.domain.lottery.entity.LotteryNumber;
import com.example.itday.domain.lottery.repository.LotteryNumberRepository;
import com.example.itday.global.exception.ErrorCode;
import com.example.itday.global.exception.ItDayException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LotteryService {

    private final LotteryNumberRepository lotteryNumberRepository;

    public LotteryResDTO checkRanking(String number) {
        LotteryNumber lotteryNumber = lotteryNumberRepository.findByNumber(number)
                .orElseThrow(() -> new ItDayException(ErrorCode.LOTTERY_NUMBER_NOT_FOUND));

        return new LotteryResDTO(lotteryNumber.getRanking());
    }
}
