package com.smart.wmserp.api;

import com.smart.wmserp.domain.sales.Offer;
import com.smart.wmserp.repository.OfferRepository;
import com.smart.wmserp.service.OfferService;
import com.smart.wmserp.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferRepository offerRepository;
    private final OfferService offerService;
    private final PurchaseService purchaseService;
    private final com.smart.wmserp.service.InquiryExcelService inquiryExcelService;

    /**
     * export_data.xlsx 원본 업로드 (거래처 자동 매핑 시도)
     */
    @PostMapping("/upload-export")
    public ResponseEntity<com.smart.wmserp.common.dto.InquiryUploadResponse> uploadExport(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "inquiryNo", required = false) String inquiryNo) {
        return ResponseEntity.ok(inquiryExcelService.uploadExportData(file, inquiryNo));
    }

    /**
     * 가공된 upload_data.xlsx 다운로드
     */
    @GetMapping("/{id}/export-upload-data")
    public ResponseEntity<byte[]> exportUploadData(@PathVariable Long id) throws java.io.IOException {
        byte[] excelContent = inquiryExcelService.generateUploadDataExcel(id);
        return org.springframework.http.ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=upload_data_" + id + ".xlsx")
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .body(excelContent);
    }

    /**
     * 엑셀 파일을 통한 인콰이어리 대량 업로드 (기존 방식)
     */
    @PostMapping("/upload-excel")
    public ResponseEntity<com.smart.wmserp.common.dto.InquiryUploadResponse> uploadExcel(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam("partnerId") Long partnerId) {
        return ResponseEntity.ok(inquiryExcelService.uploadInquiry(file, partnerId));
    }

    /**
     * 오퍼 전체 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<Offer>> getOffers() {
        List<Offer> offers = offerRepository.findAllWithPartner();
        return ResponseEntity.ok(offers);
    }

    /**
     * 오퍼 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOffer(@PathVariable Long id) {
        return offerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Offer를 P/I로 확정 (Status 변경)
     */
    @PostMapping("/{id}/convert-to-pi")
    public ResponseEntity<Void> convertToPI(@PathVariable Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offer not found"));
        offer.setStatus("PI_CONVERTED");
        offerRepository.save(offer);
        return ResponseEntity.ok().build();
    }

    /**
     * P/I 기반 P/O(발주서) 자동 생성 및 WMS 연동
     */
    @PostMapping("/{id}/generate-po")
    public ResponseEntity<Void> generatePO(@PathVariable Long id) {
        purchaseService.convertPiToPo(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 오퍼 삭제 (완전 삭제)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 오퍼 취소
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOffer(@PathVariable Long id) {
        offerService.cancelOffer(id);
        return ResponseEntity.ok().build();
    }
}
