package com.smart.wmserp.domain.wms;

import com.smart.wmserp.domain.master.Item;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_history")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "change_quantity")
    private Integer changeQuantity;

    @Column(name = "after_quantity")
    private Integer afterQuantity;

    @Column(name = "transaction_type")
    private String transactionType; // INBOUND, OUTBOUND, ADJUSTMENT

    @Column(name = "reason")
    private String reason; // 조정 사유 (파손, 분실, 전산오류 등)

    @Column(name = "reference_id")
    private Long referenceId;

    private LocalDateTime createdAt = LocalDateTime.now();
}
