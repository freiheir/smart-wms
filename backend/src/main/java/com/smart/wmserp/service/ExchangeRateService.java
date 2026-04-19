package com.smart.wmserp.service;

import com.smart.wmserp.domain.master.ExchangeRate;
import com.smart.wmserp.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExchangeRateService {
    private final ExchangeRateRepository exchangeRateRepository;

    public List<ExchangeRate> findAll() {
        return exchangeRateRepository.findAll();
    }

    public ExchangeRate save(ExchangeRate exchangeRate) {
        exchangeRate.setUpdatedAt(LocalDateTime.now());
        return exchangeRateRepository.save(exchangeRate);
    }
}
