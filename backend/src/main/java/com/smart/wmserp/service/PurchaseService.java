package com.smart.wmserp.service;

import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.master.Partner;
import com.smart.wmserp.domain.purchase.PurchaseOrder;
import com.smart.wmserp.domain.purchase.PurchaseOrderItem;
import com.smart.wmserp.domain.sales.Offer;
import com.smart.wmserp.domain.sales.OfferItem;
import com.smart.wmserp.domain.wms.InboundExpected;
import com.smart.wmserp.repository.OfferRepository;
import com.smart.wmserp.repository.PurchaseOrderRepository;
import com.smart.wmserp.repository.InboundExpectedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final OfferRepository offerRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InboundExpectedRepository inboundExpectedRepository;

    /**
     * P/I(Offer) 기반 P/O 자동 생성 및 WMS 입고 예정 등록
     */
    @Transactional
    public void convertPiToPo(Long offerId) {
        // 1. 확정된 P/I(Offer) 조회
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        if (!"PI_CONVERTED".equals(offer.getStatus())) {
            throw new RuntimeException("Only PI_CONVERTED offers can generate PO");
        }

        // 2. 품목별 매입처(Vendor) 그룹화
        // OfferItem들을 각각의 매입처(Partner)별로 모읍니다.
        Map<Partner, java.util.List<OfferItem>> vendorGroups = offer.getItems().stream()
                .collect(Collectors.groupingBy(oi -> oi.getItem().getPartner()));

        // 3. 매입처별로 P/O 생성
        for (Map.Entry<Partner, java.util.List<OfferItem>> entry : vendorGroups.entrySet()) {
            Partner vendor = entry.getKey();
            java.util.List<OfferItem> items = entry.getValue();

            // P/O 마스터 생성
            PurchaseOrder po = PurchaseOrder.builder()
                    .poNo("PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .vendor(vendor)
                    .piReferenceId(offer.getId())
                    .status("ORDERED")
                    .build();

            for (OfferItem oi : items) {
                Item item = oi.getItem();
                
                // P/O 상세 항목 추가
                PurchaseOrderItem poItem = PurchaseOrderItem.builder()
                        .item(item)
                        .quantity(oi.getQuantity())
                        .unitPrice(item.getWholesalePrice()) // 매입가는 도매가 기준
                        .build();
                po.addItem(poItem);

                // 4. WMS Inbound_Expected 데이터 자동 삽입
                // 재고가 부족하거나 즉시 수급이 어려운 품목은 'SHORTAGE' 상태로 관리 가능 (비즈니스 룰에 따라 확장)
                String initialStatus = "WAITING";
                // 예: 만약 특정 조건(재고0 등)이면 'SHORTAGE' 부여 로직 추가 가능
                
                InboundExpected expected = InboundExpected.builder()
                        .purchaseOrder(po)
                        .item(item)
                        .expectedQuantity(oi.getQuantity())
                        .status(initialStatus)
                        .expectedDate(LocalDateTime.now().plusDays(7)) // 기본 7일 후 입고 예정
                        .build();
                
                // P/O 저장 시 Cascade 혹은 수동 저장
                inboundExpectedRepository.save(expected);
            }
            purchaseOrderRepository.save(po);
        }
    }
}
