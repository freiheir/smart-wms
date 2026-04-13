package com.smart.wmserp.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String itemCode;

    @Column(nullable = false)
    private String itemName;

    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private String itemUnit; // EA, BOX, PLT 등

    @Column(unique = true)
    private String barcode;

    @Column(nullable = false)
    @Builder.Default
    private String useYn = "Y";

    private String category;

    /**
     * 재고 입고 처리
     */
    public void receive(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("입고 수량은 0보다 커야 합니다.");
        }
        if (this.stockQuantity == null) {
            this.stockQuantity = 0;
        }
        this.stockQuantity += quantity;
    }

    /**
     * 재고 출고 처리
     */
    public void release(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("출고 수량은 0보다 커야 합니다.");
        }
        if (this.stockQuantity == null || this.stockQuantity < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stockQuantity -= quantity;
    }
}
