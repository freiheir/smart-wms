package com.smart.wmserp.dto;

import com.smart.wmserp.domain.master.Item;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ItemResponseDto {
    private Long id;
    private String partNumber;
    private String rawPartNumber;
    private String itemName;
    private BigDecimal wholesalePrice;
    private BigDecimal multiplier;
    private BigDecimal weightKg;
    private BigDecimal cbm;
    private String currency;
    private String partnerName;
    private String useYn;
    private Integer safetyStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ItemResponseDto(Item item) {
        this.id = item.getId();
        this.partNumber = item.getPartNumber();
        this.rawPartNumber = item.getRawPartNumber();
        this.itemName = item.getItemName();
        this.wholesalePrice = item.getWholesalePrice();
        this.multiplier = item.getMultiplier();
        this.weightKg = item.getWeightKg();
        this.cbm = item.getCbm();
        this.currency = item.getCurrency();
        this.partnerName = item.getPartner() != null ? item.getPartner().getPartnerName() : null;
        this.useYn = item.getUseYn();
        this.safetyStock = item.getSafetyStock();
        this.createdAt = item.getCreatedAt();
        this.updatedAt = item.getUpdatedAt();
    }
}
