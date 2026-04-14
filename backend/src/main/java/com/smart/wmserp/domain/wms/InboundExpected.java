package com.smart.wmserp.domain.wms;

import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.purchase.PurchaseOrder;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inbound_expected")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InboundExpected {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "expected_quantity")
    private Integer expectedQuantity;

    @Builder.Default
    @Column(name = "received_quantity")
    private Integer receivedQuantity = 0;

    @Builder.Default
    @Column(name = "status")
    private String status = "WAITING";

    private LocalDateTime expectedDate;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
