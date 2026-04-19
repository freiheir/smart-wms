package com.smart.wmserp.service;

import com.smart.wmserp.domain.master.ExchangeRate;
import com.smart.wmserp.domain.master.WeightPolicy;
import com.smart.wmserp.repository.ExchangeRateRepository;
import com.smart.wmserp.repository.WeightPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final WeightPolicyRepository weightPolicyRepository;

    /**
     * 구매가 산출 (도매가 * 가중치)
     */
    public BigDecimal calculatePurchasePrice(BigDecimal wholesalePrice, String itemCode) {
        if (wholesalePrice == null) return BigDecimal.ZERO;

        BigDecimal multiplier = weightPolicyRepository.findByItemCode(itemCode)
                .map(WeightPolicy::getMultiplier)
                .orElse(BigDecimal.ONE);

        return wholesalePrice.multiply(multiplier).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 판매가 산출 (구매가 * 환율 변환)
     */
    public BigDecimal calculateRetailPrice(BigDecimal purchasePrice,
                                          String sourceCurrency, String targetCurrency,
                                          BigDecimal manualRate) {
        
        if (purchasePrice == null) return BigDecimal.ZERO;

        // 환율 결정 로직
        BigDecimal sourceRateToKrw = getRateToKrw(sourceCurrency, null);
        BigDecimal targetRateToKrw = getRateToKrw(targetCurrency, manualRate);

        // 통화 변환
        int scale = "KRW".equalsIgnoreCase(targetCurrency) ? 0 : 2;
        return purchasePrice.multiply(sourceRateToKrw)
                        .divide(targetRateToKrw, scale, RoundingMode.HALF_UP);
    }

    public BigDecimal getRateToKrw(String currencyCode, BigDecimal manualRate) {
        if ("KRW".equalsIgnoreCase(currencyCode)) return BigDecimal.ONE;
        if (manualRate != null) return manualRate; // 수기 입력 환율 우선 적용

        return exchangeRateRepository.findByCurrencyCode(currencyCode)
                .map(ExchangeRate::getRateToKrw)
                .orElse(new BigDecimal("1400")); // 환율 정보가 없으면 기본값 1400원 적용
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
