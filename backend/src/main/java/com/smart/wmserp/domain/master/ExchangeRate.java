package com.smart.wmserp.domain.master;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchange_rates")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "currency_code", unique = true, nullable = false)
    private String currencyCode; // USD, EUR, JPY 등

    @Column(name = "rate_to_krw", precision = 18, scale = 4, nullable = false)
    private BigDecimal rateToKrw; // KRW 환산 환율

    private LocalDateTime updatedAt = LocalDateTime.now();
}
