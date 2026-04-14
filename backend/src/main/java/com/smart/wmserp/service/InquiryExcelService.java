package com.smart.wmserp.service;

import com.smart.wmserp.common.dto.InquiryUploadResponse;
import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.master.Partner;
import com.smart.wmserp.domain.sales.Offer;
import com.smart.wmserp.domain.sales.OfferItem;
import com.smart.wmserp.repository.ItemRepository;
import com.smart.wmserp.repository.PartnerRepository;
import com.smart.wmserp.util.PartNumberUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InquiryExcelService {

    private final ItemRepository itemRepository;
    private final PartnerRepository partnerRepository;
    private final OfferService offerService;
    private final PricingService pricingService;

    @Transactional
    public InquiryUploadResponse uploadInquiry(MultipartFile file, Long partnerId) {
        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner not found"));

        List<InquiryUploadResponse.InquiryErrorDetail> errors = new ArrayList<>();
        List<OfferItem> validItems = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            
            // 0번 행(Header) 건너뛰고 1번 행부터 시작
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    // 컬럼 매핑: 0:품번, 1:수량, 2:바이어단가(옵션)
                    String rawPartNumber = getCellValueAsString(row.getCell(0));
                    int quantity = (int) row.getCell(1).getNumericCellValue();
                    
                    // 1. 품번 정제
                    String cleanPartNumber = PartNumberUtil.clean(rawPartNumber);

                    // 2. 시스템 존재 여부 확인
                    Optional<Item> itemOpt = itemRepository.findByPartNumber(cleanPartNumber);
                    
                    if (itemOpt.isEmpty()) {
                        errors.add(InquiryUploadResponse.InquiryErrorDetail.builder()
                                .rowNum(i + 1)
                                .rawPartNumber(rawPartNumber)
                                .errorMessage("Item not found in master data")
                                .build());
                        continue;
                    }

                    Item item = itemOpt.get();
                    
                    // 3. 가격 산출 (시스템 기본 배수 적용)
                    BigDecimal calculatedPrice = pricingService.calculateRetailPrice(
                            item.getWholesalePrice(),
                            item.getMultiplier(),
                            item.getCurrency(),
                            partner.getCurrency(),
                            null
                    );

                    validItems.add(OfferItem.builder()
                            .item(item)
                            .quantity(quantity)
                            .unitPrice(calculatedPrice)
                            .marginRate(pricingService.calculateMarginRate(calculatedPrice, item.getWholesalePrice()))
                            .build());

                } catch (Exception e) {
                    errors.add(InquiryUploadResponse.InquiryErrorDetail.builder()
                            .rowNum(i + 1)
                            .errorMessage("Invalid data format in row " + (i + 1))
                            .build());
                }
            }

            // 모든 행이 정상이 아니어도 일부 성공한 것만 Offer로 생성하거나,
            // 전체 성공 시에만 생성하게 할 수 있습니다. (여기선 에러 리포트 반환에 집중)
            if (errors.isEmpty() && !validItems.isEmpty()) {
                Offer offer = Offer.builder()
                        .partner(partner)
                        .status("INQUIRY")
                        .currency(partner.getCurrency())
                        .build();
                
                // OfferItem 연동 및 저장 로직 (OfferService 활용)
                // ... 생략 (실제 저장 로직)
                
                return InquiryUploadResponse.builder()
                        .successCount(validItems.size())
                        .failureCount(errors.size())
                        .errors(errors)
                        .build();
            }

        } catch (Exception e) {
            throw new RuntimeException("Excel parsing failed: " + e.getMessage());
        }

        return InquiryUploadResponse.builder()
                .successCount(validItems.size())
                .failureCount(errors.size())
                .errors(errors)
                .build();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }
}
