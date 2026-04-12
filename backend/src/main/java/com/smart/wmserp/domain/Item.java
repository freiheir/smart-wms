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
}
