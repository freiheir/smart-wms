package com.smart.wmserp.service;

import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.sales.Offer;
import com.smart.wmserp.domain.sales.OfferItem;
import com.smart.wmserp.repository.ItemRepository;
import com.smart.wmserp.repository.OfferRepository;
import com.smart.wmserp.util.PartNumberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final OfferRepository offerRepository;
    private final ItemRepository itemRepository;
    private final PricingService pricingService;

    /**
     * Offer 등록 (품번 자동 정제 및 가격 자동 산출)
     */
    @Transactional
    public Offer createOffer(Offer offer, List<OfferItemRequest> items, BigDecimal manualRate) {
        
        for (OfferItemRequest req : items) {
            // 1. 품번 정제 (최우선 실행)
            String cleanPartNumber = PartNumberUtil.clean(req.getPartNumber());

            // 2. 품목 마스터 조회 (정제된 품번 기준)
            Item item = itemRepository.findByPartNumber(cleanPartNumber)
                    .orElseThrow(() -> new RuntimeException("Item not found: " + cleanPartNumber));

            // 3. 가격 자동 산출 (배수 및 환율 적용)
            BigDecimal calculatedPrice = pricingService.calculateRetailPrice(
                    item.getWholesalePrice(),
                    item.getMultiplier(),
                    item.getCurrency(),
                    offer.getCurrency(),
                    manualRate
            );

            // 4. Offer Item 생성
            OfferItem offerItem = OfferItem.builder()
                    .item(item)
                    .quantity(req.getQuantity())
                    .unitPrice(calculatedPrice)
                    .marginRate(pricingService.calculateMarginRate(calculatedPrice, item.getWholesalePrice()))
                    .build();

            offer.addItem(offerItem);
        }

        // 총액 계산
        BigDecimal total = offer.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        offer.setTotalAmount(total);

        return offerRepository.save(offer);
    }

    /**
     * 오더 취소 (할당 해제)
     * 상태를 CANCELLED로 변경함으로써 StockQueryService의 가용 재고 계산에서 제외됨 (자동 할당 해제)
     */
    @Transactional
    public void cancelOffer(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Offer not found"));

        if ("SHIPPED".equals(offer.getStatus())) {
            throw new RuntimeException("Already shipped orders cannot be cancelled. Use Return process.");
        }

        offer.setStatus("CANCELLED");
        offerRepository.save(offer);
    }
}
