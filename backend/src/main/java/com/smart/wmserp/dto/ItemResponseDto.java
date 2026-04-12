package com.smart.wmserp.dto;

import com.smart.wmserp.domain.Item;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ItemResponseDto {
    private Long id;
    private String itemCode;
    private String itemName;
    private String description;
    private Integer price;
    private Integer stockQuantity;
    private String itemUnit;
    private String barcode;
    private String useYn;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ItemResponseDto(Item item) {
        this.id = item.getId();
        this.itemCode = item.getItemCode();
        this.itemName = item.getItemName();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.stockQuantity = item.getStockQuantity();
        this.itemUnit = item.getItemUnit();
        this.barcode = item.getBarcode();
        this.useYn = item.getUseYn();
        this.category = item.getCategory();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
    }
}
