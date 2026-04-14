package com.smart.wmserp.service;

import com.smart.wmserp.common.dto.DashboardReport;
import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.sales.Offer;
import com.smart.wmserp.domain.sales.OfferItem;
import com.smart.wmserp.repository.ItemRepository;
import com.smart.wmserp.repository.OfferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OfferRepository offerRepository;
    private final ItemRepository itemRepository;
    private final StockQueryService stockQueryService;

    @Transactional(readOnly = true)
    public DashboardReport getDashboardReport(LocalDateTime startDate, LocalDateTime endDate) {
        return DashboardReport.builder()
                .offerSuccessRate(calculateSuccessRate())
                .unshippedOffers(getUnshippedOffers())
                .stockAlerts(getStockAlerts())
                .financialStats(calculateFinancialStats(startDate, endDate))
                .build();
    }

    /**
     * 1. 오퍼 성공률: (P/I 전환 건수 / 전체 오퍼 건수) * 100
     */
    private double calculateSuccessRate() {
        long total = offerRepository.count();
        if (total == 0) return 0.0;
        long converted = offerRepository.countByStatus("PI_CONVERTED") + offerRepository.countByStatus("SHIPPED");
        return (double) converted / total * 100;
    }

    /**
     * 2. 미출고 현황: P/I는 확정되었으나 아직 창고에서 나가지 못한 오더 리스트
     */
    private List<DashboardReport.UnshippedOffer> getUnshippedOffers() {
        return offerRepository.findByStatus("PI_CONVERTED").stream()
                .map(o -> new DashboardReport.UnshippedOffer(
                        o.getId(),
                        o.getInquiryNo(),
                        o.getPartner().getPartnerName(),
                        o.getTotalAmount()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 3. 부족 재고 알림: 가용 재고가 safetyStock 이하인 품목 리스트
     */
    private List<DashboardReport.StockAlert> getStockAlerts() {
        return itemRepository.findAll().stream()
                .filter(item -> {
                    int available = stockQueryService.getAvailableStock(item.getId());
                    return available <= item.getSafetyStock();
                })
                .map(item -> new DashboardReport.StockAlert(
                        item.getPartNumber(),
                        item.getItemName(),
                        stockQueryService.getAvailableStock(item.getId()),
                        item.getSafetyStock()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 4. 기간별 매출/매입 통계: 총 매출액, 총 매입액, 예상 마진 집계
     */
    private DashboardReport.FinancialStats calculateFinancialStats(LocalDateTime start, LocalDateTime end) {
        List<Offer> confirmedOffers = offerRepository.findConfirmedByDateRange(start, end);

        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalPurchase = BigDecimal.ZERO;

        for (Offer offer : confirmedOffers) {
            totalSales = totalSales.add(offer.getTotalAmount());
            for (OfferItem oi : offer.getItems()) {
                // 매입가 = 수량 * 도매가 (Base Currency 기준 환산 로직 필요 시 추가 가능)
                BigDecimal purchaseCost = oi.getItem().getWholesalePrice()
                        .multiply(new BigDecimal(oi.getQuantity()));
                totalPurchase = totalPurchase.add(purchaseCost);
            }
        }

        BigDecimal margin = totalSales.subtract(totalPurchase);
        double marginRate = totalSales.compareTo(BigDecimal.ZERO) > 0 ?
                margin.divide(totalSales, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue() : 0.0;

        return DashboardReport.FinancialStats.builder()
                .totalSales(totalSales)
                .totalPurchase(totalPurchase)
                .expectedMargin(margin)
                .marginRate(marginRate)
                .build();
    }
}
