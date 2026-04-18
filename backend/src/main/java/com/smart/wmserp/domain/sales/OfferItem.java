package com.smart.wmserp.domain.sales;

import com.smart.wmserp.domain.master.Item;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "offer_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // --- 엑셀 매핑 필드 추가 ---
    @Column(name = "buyer_part_no")
    private String buyerPartNo;       // B: Buyer Part No

    @Column(name = "item_code")
    private String itemCode;          // C: CODE

    @Column(name = "g_class")
    private String gClass;            // D: G

    @Column(name = "h_class")
    private String hClass;            // E: H Class

    @Column(name = "k_class")
    private String kClass;            // F: K Class

    @Column(name = "company")
    private String company;           // G: Company

    @Column(name = "car_code")
    private String carCode;           // H: Car Code

    @Column(name = "car_name")
    private String carName;           // I: Car Name

    @Column(name = "ordered_part_no")
    private String orderedPartNo;     // J: Ordered Part No

    @Column(name = "supply_no")
    private String supplyNo;          // K: Supply No.

    @Column(name = "item_class")
    private String itemClass;         // L: CLASS

    @Column(name = "part_name_eng")
    private String partNameEng;       // M: Part Name Eng

    @Column(name = "quantity", nullable = false)
    private Integer quantity;         // N: QTY REQ

    @Column(name = "unit_price", precision = 18, scale = 4)
    private BigDecimal unitPrice;     // O: PRICE (판매가)

    @Column(name = "amount", precision = 18, scale = 4)
    private BigDecimal amount;        // P: Amount (수량 * 판매가)

    @Column(name = "original_purchase_price", precision = 18, scale = 4)
    private BigDecimal originalPurchasePrice; // Q: 원매가

    @Column(name = "purchase_price", precision = 18, scale = 4)
    private BigDecimal purchasePrice; // R: 구매가

    @Column(name = "purchase_amount", precision = 18, scale = 4)
    private BigDecimal purchaseAmount; // S: 구매금액 (수량 * 구매가)

    @Column(name = "market_price", precision = 18, scale = 4)
    private BigDecimal marketPrice;   // T: 시가

    @Column(name = "market_amount", precision = 18, scale = 4)
    private BigDecimal marketAmount;  // U: 시가총액 (수량 * 시가)

    @Column(name = "margin", precision = 18, scale = 4)
    private BigDecimal margin;        // V: 마진 (매출액 - 구매금액)

    @Column(name = "margin_rate", precision = 10, scale = 4)
    private BigDecimal marginRate;    // W: 마진율 (마진 / 매출액)

    @Column(name = "buyer_item_name")
    private String buyerItemName;

    @Column(name = "line_remarks", length = 1000)
    private String lineRemarks;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
