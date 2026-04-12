package com.smart.wmserp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDto {
    private String itemCode;
    private String itemName;
    private String description;
    private Integer price;
    private Integer stockQuantity;
    private String itemUnit;
    private String barcode;
    private String useYn;
    private String category;
}
