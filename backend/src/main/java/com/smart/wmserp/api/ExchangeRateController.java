package com.smart.wmserp.api;

import com.smart.wmserp.domain.master.ExchangeRate;
import com.smart.wmserp.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/settings/exchange-rates")
@RequiredArgsConstructor
public class ExchangeRateController {
    private final ExchangeRateService exchangeRateService;

    @GetMapping
    public List<ExchangeRate> getRates() {
        return exchangeRateService.findAll();
    }

    @PostMapping
    public ExchangeRate saveRate(@RequestBody ExchangeRate exchangeRate) {
        return exchangeRateService.save(exchangeRate);
    }
}
