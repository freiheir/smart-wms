package com.smart.wmserp.common.dto;

import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryUploadResponse {
    private int successCount;
    private int failureCount;
    private List<InquiryErrorDetail> errors = new ArrayList<>();
    private Long offerId; // 성공 시 생성된 Offer ID

    @Getter @Setter
    @Builder
    @AllArgsConstructor
    public static class InquiryErrorDetail {
        private int rowNum;
        private String rawPartNumber;
        private String errorMessage;
    }
}
