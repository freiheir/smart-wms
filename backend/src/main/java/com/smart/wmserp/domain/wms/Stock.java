package com.smart.wmserp.domain.wms;

import com.smart.wmserp.domain.master.Item;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stocks")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", unique = true)
    private Item item;

    @Builder.Default
    @Column(name = "quantity")
    private Integer quantity = 0; 

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void addQuantity(int amount) {
        this.quantity += amount;
        this.updatedAt = LocalDateTime.now();
    }

    public void setQuantityManually(int newQuantity) {
        this.quantity = newQuantity;
        this.updatedAt = LocalDateTime.now();
    }
}
