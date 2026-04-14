package com.smart.wmserp.api;

import com.smart.wmserp.service.StockQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockQueryService stockQueryService;

    /**
     * 전체 재고 현황 조회 (실재고 vs 가용재고)
     */
    @GetMapping("/status")
    public ResponseEntity<List<StockQueryService.StockStatusDto>> getStockStatus() {
        return ResponseEntity.ok(stockQueryService.getAllStockStatuses());
    }

    /**
     * 총 재고 자산 가액 조회
     */
    @GetMapping("/valuation")
    public ResponseEntity<BigDecimal> getInventoryValuation() {
        return ResponseEntity.ok(stockQueryService.calculateTotalInventoryValue());
    }
}
