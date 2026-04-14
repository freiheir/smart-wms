package com.smart.wmserp.service;

import com.smart.wmserp.domain.master.ExchangeRate;
import com.smart.wmserp.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final ExchangeRateRepository exchangeRateRepository;

    /**
     * 가격 산출 및 환율 적용 (수기 환율 우선 적용)
     * 
     * @param wholesalePrice 도매가 (Base)
     * @param multiplier 배수 (Multiplier - 업체별 또는 품목별 지정)
     * @param sourceCurrency 도매가 통화
     * @param targetCurrency 목표 통화
     * @param manualRate 수기 입력 환율 (null이면 DB 참조)
     * @return 산출된 단가
     */
    public BigDecimal calculateRetailPrice(BigDecimal wholesalePrice, BigDecimal multiplier, 
                                          String sourceCurrency, String targetCurrency,
                                          BigDecimal manualRate) {
        
        if (wholesalePrice == null || multiplier == null) return BigDecimal.ZERO;

        // 1. 배수 적용 (Base Price 산출)
        BigDecimal basePrice = wholesalePrice.multiply(multiplier);

        // 2. 환율 결정 로직
        BigDecimal sourceRateToKrw = getRateToKrw(sourceCurrency, null); // 원본은 DB 기준
        BigDecimal targetRateToKrw = getRateToKrw(targetCurrency, manualRate); // 목표는 수기 우선

        // 3. 통화 변환 (KRW 환산 후 목표 통화로 변환)
        // RetailPrice = (BasePrice * SourceRateToKrw) / TargetRateToKrw
        return basePrice.multiply(sourceRateToKrw)
                        .divide(targetRateToKrw, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal getRateToKrw(String currencyCode, BigDecimal manualRate) {
        if ("KRW".equalsIgnoreCase(currencyCode)) return BigDecimal.ONE;
        if (manualRate != null) return manualRate; // 수기 입력 환율 우선 적용

        return exchangeRateRepository.findByCurrencyCode(currencyCode)
                .map(ExchangeRate::getRateToKrw)
                .orElse(BigDecimal.ONE); // 환율 정보가 없으면 1:1 대응 (경고성 로직 필요)
    }

    /**
     * 마진율 계산
     */
    public BigDecimal calculateMarginRate(BigDecimal retailPrice, BigDecimal wholesalePrice) {
        if (retailPrice == null || retailPrice.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        
        return retailPrice.subtract(wholesalePrice)
                          .divide(retailPrice, 4, RoundingMode.HALF_UP)
                          .multiply(new BigDecimal("100"));
    }
}
