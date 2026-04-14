package com.smart.wmserp.service;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferItemRequest {
    private String partNumber;
    private Integer quantity;
}
