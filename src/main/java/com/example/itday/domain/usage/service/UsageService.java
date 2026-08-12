package com.example.itday.domain.usage.service;

import com.example.itday.domain.member.entity.Member;
import com.example.itday.domain.member.repository.MemberRepository;
import com.example.itday.domain.store.entity.Store;
import com.example.itday.domain.store.repository.StoreRepository;
import com.example.itday.domain.usage.dto.UsageReqDTO;
import com.example.itday.domain.usage.entity.Usage;
import com.example.itday.domain.usage.repository.UsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UsageService {

    private final UsageRepository usageRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void recordUsage(Long memberId, UsageReqDTO request) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 매장입니다."));

        Usage usage = Usage.builder()
                .member(member)
                .store(store)
                .usedAt(LocalDateTime.now())
                .build();

        usageRepository.save(usage);
    }
}
