package com.smart.wmserp.service;

import com.smart.wmserp.common.dto.InquiryUploadResponse;
import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.master.Partner;
import com.smart.wmserp.domain.sales.Offer;
import com.smart.wmserp.domain.sales.OfferItem;
import com.smart.wmserp.repository.ItemRepository;
import com.smart.wmserp.repository.PartnerRepository;
import com.smart.wmserp.repository.OfferRepository;
import com.smart.wmserp.util.PartNumberUtil;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InquiryExcelService {

    private final ItemRepository itemRepository;
    private final PartnerRepository partnerRepository;
    private final OfferRepository offerRepository;
    private final OfferService offerService;
    private final PricingService pricingService;

    @Transactional
    public InquiryUploadResponse uploadExportData(MultipartFile file, String inquiryNo) {
        List<OfferItem> validItems = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i); if (row == null) continue;
                try {
                    String buyerPartNo = getCellValueAsString(row.getCell(2));
                    String carName = getCellValueAsString(row.getCell(6));
                    String orderedPartNo = getCellValueAsString(row.getCell(7));
                    String itemClass = getCellValueAsString(row.getCell(8));
                    String partNameEng = getCellValueAsString(row.getCell(11));
                    String qtyStr = getCellValueAsString(row.getCell(12));
                    String costStr = getCellValueAsString(row.getCell(13));
                    String itemCode = getCellValueAsString(row.getCell(74));
                    String hClass = getCellValueAsString(row.getCell(75));
                    String kClass = getCellValueAsString(row.getCell(76));

                    if (buyerPartNo == null || buyerPartNo.trim().isEmpty()) continue;

                    BigDecimal quantity = parseBigDecimal(qtyStr, BigDecimal.ONE);
                    BigDecimal purchasePrice = parseBigDecimal(costStr, BigDecimal.ZERO);
                    BigDecimal marginRate = new BigDecimal("0.15"); 
                    BigDecimal unitPrice = purchasePrice.divide(BigDecimal.ONE.subtract(marginRate), 2, RoundingMode.HALF_UP);

                    String cleanPartNumber = PartNumberUtil.clean(orderedPartNo.isEmpty() ? buyerPartNo : orderedPartNo);
                    Item item = itemRepository.findByPartNumber(cleanPartNumber).orElse(null);

                    validItems.add(OfferItem.builder()
                            .item(item).buyerPartNo(buyerPartNo).orderedPartNo(cleanPartNumber)
                            .itemCode(itemCode).carName(carName).itemClass(itemClass).partNameEng(partNameEng)
                            .hClass(hClass).kClass(kClass).quantity(quantity.intValue())
                            .purchasePrice(purchasePrice).originalPurchasePrice(purchasePrice)
                            .unitPrice(unitPrice).amount(unitPrice.multiply(quantity))
                            .purchaseAmount(purchasePrice.multiply(quantity))
                            .margin(unitPrice.multiply(quantity).subtract(purchasePrice.multiply(quantity)))
                            .marginRate(marginRate).createdAt(LocalDateTime.now()).build());
                } catch (Exception e) {}
            }
            if (!validItems.isEmpty()) {
                Offer offer = Offer.builder().inquiryNo(inquiryNo != null && !inquiryNo.isEmpty() ? inquiryNo : "REQ-" + System.currentTimeMillis())
                        .status("INQUIRY").currency("USD").createdAt(LocalDateTime.now()).build();
                for (OfferItem oi : validItems) { offer.addItem(oi); }
                offerService.createOffer(offer);
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return InquiryUploadResponse.builder().successCount(validItems.size()).build();
    }

    @Transactional
    public InquiryUploadResponse uploadInquiry(MultipartFile file, Long partnerId) {
        Partner partner = partnerRepository.findById(partnerId).orElseThrow(() -> new RuntimeException("Partner not found: " + partnerId));
        List<OfferItem> validItems = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 2; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i); if (row == null) continue;
                try {
                    String buyerPartNo = getCellValueAsString(row.getCell(2));
                    String carName = getCellValueAsString(row.getCell(6));
                    String orderedPartNo = getCellValueAsString(row.getCell(7));
                    String itemClass = getCellValueAsString(row.getCell(8));
                    String partNameEng = getCellValueAsString(row.getCell(11));
                    String qtyStr = getCellValueAsString(row.getCell(12));
                    String costStr = getCellValueAsString(row.getCell(13));
                    String itemCode = getCellValueAsString(row.getCell(74));
                    String hClass = getCellValueAsString(row.getCell(75));
                    String kClass = getCellValueAsString(row.getCell(76));

                    if (buyerPartNo == null || buyerPartNo.trim().isEmpty()) continue;

                    BigDecimal quantity = parseBigDecimal(qtyStr, BigDecimal.ONE);
                    BigDecimal purchasePrice = parseBigDecimal(costStr, BigDecimal.ZERO);
                    BigDecimal marginRate = new BigDecimal("0.15");
                    BigDecimal unitPrice = purchasePrice.divide(BigDecimal.ONE.subtract(marginRate), 2, RoundingMode.HALF_UP);

                    String cleanPartNumber = PartNumberUtil.clean(orderedPartNo.isEmpty() ? buyerPartNo : orderedPartNo);
                    Item item = itemRepository.findByPartNumber(cleanPartNumber).orElse(null);

                    validItems.add(OfferItem.builder()
                            .item(item).buyerPartNo(buyerPartNo).orderedPartNo(cleanPartNumber)
                            .itemCode(itemCode).carName(carName).itemClass(itemClass).partNameEng(partNameEng)
                            .hClass(hClass).kClass(kClass).quantity(quantity.intValue())
                            .purchasePrice(purchasePrice).originalPurchasePrice(purchasePrice)
                            .unitPrice(unitPrice).amount(unitPrice.multiply(quantity))
                            .purchaseAmount(purchasePrice.multiply(quantity))
                            .margin(unitPrice.multiply(quantity).subtract(purchasePrice.multiply(quantity)))
                            .marginRate(marginRate).createdAt(LocalDateTime.now()).build());
                } catch (Exception e) {}
            }
            if (!validItems.isEmpty()) {
                Offer offer = Offer.builder()
                        .inquiryNo("REQ-" + System.currentTimeMillis())
                        .partner(partner)
                        .status("INQUIRY").currency(partner.getCurrency() != null ? partner.getCurrency() : "USD")
                        .createdAt(LocalDateTime.now()).build();
                for (OfferItem oi : validItems) { offer.addItem(oi); }
                offerService.createOffer(offer);
            }
        } catch (Exception e) { throw new RuntimeException(e); }
        return InquiryUploadResponse.builder().successCount(validItems.size()).build();
    }

    public byte[] generateUploadDataExcel(Long offerId) throws IOException {
        Offer offer = offerRepository.findById(offerId).orElseThrow();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Upload Data");
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle percentStyle = workbook.createCellStyle();
            percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));

            // 1행: 요약 (SUBTOTAL)
            Row summaryRow = sheet.createRow(0);
            summaryRow.createCell(12).setCellValue("Total Summary:");
            summaryRow.getCell(12).setCellStyle(headerStyle);

            // 2행: 헤더 (오빠 요청대로 순서 조정!)
            Row headerRow = sheet.createRow(1);
            String[] headers = {"No.", "Buyer Part No", "CODE", "G", "H Class", "K Class", "Company", "Car Code", "Car Name", "Ordered Part No", "Supply No.", "CLASS", "Part Name Eng", "QTY REQ", "도매가", "구매가", "구매금액", "판매가", "판매금액", "마진", "마진율"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int startRow = 2; int rowIdx = startRow;
            for (OfferItem item : offer.getItems()) {
                Row row = sheet.createRow(rowIdx++);
                int excelRow = rowIdx; 
                row.createCell(0).setCellValue(rowIdx - startRow);
                row.createCell(1).setCellValue(item.getBuyerPartNo());
                row.createCell(2).setCellValue(item.getItemCode());
                row.createCell(3).setCellValue(item.getGClass());
                row.createCell(4).setCellValue(item.getHClass());
                row.createCell(5).setCellValue(item.getKClass());
                row.createCell(6).setCellValue(item.getCompany());
                row.createCell(8).setCellValue(item.getCarName());
                row.createCell(9).setCellValue(item.getOrderedPartNo());
                row.createCell(11).setCellValue(item.getItemClass());
                row.createCell(12).setCellValue(item.getPartNameEng());
                
                row.createCell(13).setCellValue(item.getQuantity() != null ? item.getQuantity() : 0); // N: 수량
                row.createCell(14).setCellValue(item.getOriginalPurchasePrice() != null ? item.getOriginalPurchasePrice().doubleValue() : 0); // O: 도매가
                row.createCell(15).setCellValue(item.getPurchasePrice() != null ? item.getPurchasePrice().doubleValue() : 0); // P: 구매가
                
                // Q: 구매금액 = N * P
                row.createCell(16).setCellFormula("N" + excelRow + "*P" + excelRow);
                
                row.createCell(17).setCellValue(item.getUnitPrice() != null ? item.getUnitPrice().doubleValue() : 0); // R: 판매가
                
                // S: 판매금액 = N * R
                row.createCell(18).setCellFormula("N" + excelRow + "*R" + excelRow);
                
                // T: 마진 = S - Q
                row.createCell(19).setCellFormula("S" + excelRow + "-Q" + excelRow);
                
                // U: 마진율 = T / S
                Cell rateCell = row.createCell(20);
                rateCell.setCellFormula("IFERROR(T" + excelRow + "/S" + excelRow + ", 0)");
                rateCell.setCellStyle(percentStyle);
            }

            if (rowIdx > startRow) {
                summaryRow.createCell(13).setCellFormula("SUBTOTAL(9,N3:N" + rowIdx + ")"); // Qty
                summaryRow.createCell(16).setCellFormula("SUBTOTAL(9,Q3:Q" + rowIdx + ")"); // Purchase Amount
                summaryRow.createCell(18).setCellFormula("SUBTOTAL(9,S3:S" + rowIdx + ")"); // Sales Amount
                summaryRow.createCell(19).setCellFormula("SUBTOTAL(9,T3:T" + rowIdx + ")"); // Margin
                Cell totalRateCell = summaryRow.createCell(20);
                totalRateCell.setCellFormula("T1/S1");
                totalRateCell.setCellStyle(percentStyle);
            }
            workbook.write(out); return out.toByteArray();
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont(); font.setBold(true); style.setFont(font);
        return style;
    }

    private BigDecimal parseBigDecimal(String val, BigDecimal defaultVal) {
        try {
            if (val == null || val.isEmpty()) return defaultVal;
            String cleanVal = val.replaceAll("[^0-9.]", "");
            return cleanVal.isEmpty() ? defaultVal : new BigDecimal(cleanVal);
        } catch (Exception e) { return defaultVal; }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case FORMULA -> {
                try { yield String.valueOf(cell.getNumericCellValue()); }
                catch (Exception e) { yield cell.getCellFormula(); }
            }
            default -> "";
        };
    }
}
