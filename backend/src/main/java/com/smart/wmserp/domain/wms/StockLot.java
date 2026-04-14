package com.smart.wmserp.domain.wms;

import com.smart.wmserp.domain.master.Item;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_lots")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockLot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "inbound_date")
    private LocalDateTime inboundDate;

    @Column(name = "po_id")
    private Long poId;

    private LocalDateTime updatedAt = LocalDateTime.now();

    public void deduct(int amount) {
        this.quantity -= amount;
        this.updatedAt = LocalDateTime.now();
    }
}
