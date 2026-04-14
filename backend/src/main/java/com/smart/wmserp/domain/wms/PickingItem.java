package com.smart.wmserp.domain.wms;

import com.smart.wmserp.domain.master.Item;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "picking_items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickingItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picking_list_id")
    private PickingList pickingList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_lot_id")
    private StockLot stockLot;

    @Column(name = "quantity")
    private Integer quantity;
}
