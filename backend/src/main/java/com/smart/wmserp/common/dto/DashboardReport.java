package com.smart.wmserp.common.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardReport {
    private double offerSuccessRate; // 오퍼 성공률
    private List<UnshippedOffer> unshippedOffers; // 미출고 현황
    private List<StockAlert> stockAlerts; // 부족 재고 알림
    private FinancialStats financialStats; // 기간별 매출/매입 통계

    @Getter @Setter
    @AllArgsConstructor
    public static class UnshippedOffer {
        private Long offerId;
        private String inquiryNo;
        private String partnerName;
        private BigDecimal totalAmount;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class StockAlert {
        private String partNumber;
        private String itemName;
        private int availableStock;
        private int safetyStock;
    }

    @Getter @Setter
    @Builder
    @AllArgsConstructor
    public static class FinancialStats {
        private BigDecimal totalSales;
        private BigDecimal totalPurchase;
        private BigDecimal expectedMargin;
        private double marginRate;
    }
}
