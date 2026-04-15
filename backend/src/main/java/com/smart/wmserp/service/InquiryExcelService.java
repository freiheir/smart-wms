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
                    // 컬럼 매핑: 0:품번, 1:수량, 2:바이어단가, 3:이메일, 4:바이어품명, 5:비고, 6:거래처코드
                    String rawPartNumber = getCellValueAsString(row.getCell(0));
                    int quantity = (int) row.getCell(1).getNumericCellValue();
                    String managerEmail = getCellValueAsString(row.getCell(3));
                    String buyerItemName = getCellValueAsString(row.getCell(4));
                    String lineRemarks = getCellValueAsString(row.getCell(5));
                    String excelPartnerCode = getCellValueAsString(row.getCell(6));
                    
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
                            .buyerItemName(buyerItemName)
                            .lineRemarks(lineRemarks)
                            .build());

                    // 거래처 코드 검증 (선택된 거래처와 엑셀 내 거래처가 일치하는지 확인)
                    if (!excelPartnerCode.isEmpty() && !excelPartnerCode.equals(partner.getPartnerCode())) {
                        // 일치하지 않을 때 경고를 주거나 처리할 수 있음
                    }

                } catch (Exception e) {
                    errors.add(InquiryUploadResponse.InquiryErrorDetail.builder()
                            .rowNum(i + 1)
                            .errorMessage("Invalid data format in row " + (i + 1))
                            .build());
                }
            }

            // 에러가 있어도 성공한 품목이 있으면 Offer 생성 (부분 성공 허용)
            if (!validItems.isEmpty()) {
                // 첫 번째 행의 데이터를 헤더 정보로 활용
                Row firstDataRow = sheet.getRow(1);
                String managerEmail = getCellValueAsString(firstDataRow.getCell(3));
                String totalRemarks = getCellValueAsString(firstDataRow.getCell(5));

                Offer offer = Offer.builder()
                        .partner(partner)
                        .status("INQUIRY")
                        .currency(partner.getCurrency())
                        .managerEmail(managerEmail)
                        .remarks(totalRemarks)
                        .build();
                
                // OfferItem 연동
                for (OfferItem oi : validItems) {
                    offer.addItem(oi);
                }
                
                // OfferService를 통해 실제 저장 (오퍼 번호 생성 등)
                Offer saved = offerService.createOffer(offer);
                System.out.println(">>> Inquiry Upload - Saved Offer ID: " + saved.getId());
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
