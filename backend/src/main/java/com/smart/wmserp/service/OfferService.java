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

    @Transactional
    public Offer createOffer(Offer offer) {
        // 총액 계산
        BigDecimal total = offer.getItems().stream()
                .map(i -> i.getUnitPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        offer.setTotalAmount(total);

        // 인콰이어리 번호 자동 생성 (예: INQ-20260415-001)
        long currentCount = offerRepository.count();
        String inquiryNo = "INQ-" + java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd").format(java.time.LocalDateTime.now()) 
                         + "-" + String.format("%03d", currentCount + 1);
        offer.setInquiryNo(inquiryNo);

        Offer savedOffer = offerRepository.save(offer);
        System.out.println(">>> Offer saved successfully: " + savedOffer.getInquiryNo());
        return savedOffer;
    }

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

            // 3. 가격 자동 산출 (구매가 및 판매가 산출)
            BigDecimal purchasePrice = pricingService.calculatePurchasePrice(
                    item.getWholesalePrice(),
                    req.getItemCode()
            );

            System.out.println(">>> [DEBUG] ItemCode: " + req.getItemCode() + ", OriginalPrice: " + item.getWholesalePrice() + ", CalculatedPurchasePrice: " + purchasePrice);

            BigDecimal salesPrice = pricingService.calculateRetailPrice(
                    purchasePrice,
                    item.getCurrency(),
                    offer.getCurrency(),
                    manualRate
            );

            // 4. Offer Item 생성
            OfferItem offerItem = OfferItem.builder()
                    .item(item)
                    .quantity(req.getQuantity())
                    .unitPrice(salesPrice)
                    .purchasePrice(purchasePrice)
                    .marginRate(pricingService.calculateMarginRate(salesPrice, purchasePrice))
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
