package com.smart.wmserp.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private String partNumber;
    private String rawPartNumber;
    private String itemName;
    private BigDecimal wholesalePrice;
    private BigDecimal multiplier;
    private BigDecimal weightKg;
    private BigDecimal cbm;
    private String currency;
    private Long partnerId;
    private String useYn;
    private Integer safetyStock;
}
