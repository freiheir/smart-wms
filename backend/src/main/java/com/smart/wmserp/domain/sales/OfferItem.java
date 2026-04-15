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

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 18, scale = 4)
    private BigDecimal unitPrice;

    @Column(name = "margin_rate", precision = 10, scale = 2)
    private BigDecimal marginRate;

    @Column(name = "buyer_item_name")
    private String buyerItemName;

    @Column(name = "line_remarks", length = 1000)
    private String lineRemarks;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
