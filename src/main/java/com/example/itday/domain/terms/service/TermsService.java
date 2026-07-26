package com.example.itday.domain.terms.service;

import com.example.itday.domain.terms.dto.TermsResDTO;
import com.example.itday.domain.terms.entity.Terms;
import com.example.itday.domain.terms.repository.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermsService {

    private final TermsRepository termsRepository;

    public List<TermsResDTO> getTerms() {

        List<Terms> termsList = termsRepository.findAll();

        List<TermsResDTO> result = termsList.stream().map(
                terms -> new TermsResDTO(
                        terms.getId(),
                        terms.getTitle(),
                        terms.getContent(),
                        terms.isRequired()
                ))
                .toList();

        return result; //
    }
}
