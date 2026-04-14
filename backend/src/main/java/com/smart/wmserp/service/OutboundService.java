package com.smart.wmserp.service;

import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.sales.Offer;
import com.smart.wmserp.domain.sales.OfferItem;
import com.smart.wmserp.domain.wms.*;
import com.smart.wmserp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboundService {

    private final PickingListRepository pickingListRepository;
    private final StockLotRepository stockLotRepository;
    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final OfferRepository offerRepository;

    /**
     * 1. 출고 지시서(Picking List) 생성 및 FIFO 재고 할당
     */
    @Transactional
    public PickingList createPickingList(Long offerId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("P/I not found"));

        PickingList pickingList = PickingList.builder()
                .offer(offer)
                .status("READY")
                .build();

        for (OfferItem oi : offer.getItems()) {
            allocateFIFO(pickingList, oi.getItem(), oi.getQuantity());
        }

        return pickingListRepository.save(pickingList);
    }

    private void allocateFIFO(PickingList pl, Item item, int requiredQty) {
        // 입고일(inboundDate) 오름차순으로 가용 로트 조회 (FIFO)
        List<StockLot> lots = stockLotRepository.findByItemAndQuantityGreaterThanOrderByInboundDateAsc(item, 0);
        
        int remaining = requiredQty;
        for (StockLot lot : lots) {
            if (remaining <= 0) break;

            int allocQty = Math.min(lot.getQuantity(), remaining);
            
            PickingItem pi = PickingItem.builder()
                    .pickingList(pl)
                    .item(item)
                    .stockLot(lot)
                    .quantity(allocQty)
                    .build();
            pl.getItems().add(pi);

            remaining -= allocQty;
        }

        if (remaining > 0) {
            throw new RuntimeException("Insufficient stock for item: " + item.getPartNumber());
        }
    }

    /**
     * 2. 최종 출고 확정 (Stock 차감 및 이력 기록)
     */
    @Transactional
    public void confirmShipment(Long pickingListId) {
        PickingList pl = pickingListRepository.findById(pickingListId)
                .orElseThrow(() -> new RuntimeException("Picking list not found"));

        if ("SHIPPED".equals(pl.getStatus())) return;

        for (PickingItem pi : pl.getItems()) {
            // A. StockLot 차감
            StockLot lot = pi.getStockLot();
            lot.deduct(pi.getQuantity());

            // B. 메인 Stock(실재고) 차감
            Stock stock = stockRepository.findByItem(pi.getItem())
                    .orElseThrow(() -> new RuntimeException("Stock record missing"));
            stock.addQuantity(-pi.getQuantity());

            // C. 재고 이력 기록 (Sales Out)
            StockHistory history = StockHistory.builder()
                    .item(pi.getItem())
                    .changeQuantity(-pi.getQuantity())
                    .afterQuantity(stock.getQuantity())
                    .transactionType("SALES_OUT")
                    .referenceId(pl.getOffer().getId())
                    .build();
            stockHistoryRepository.save(history);
        }

        pl.setStatus("SHIPPED");
        pl.getOffer().setStatus("SHIPPED"); // P/I 상태도 출고 완료로 변경
    }
}
