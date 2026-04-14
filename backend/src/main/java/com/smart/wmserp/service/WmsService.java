package com.smart.wmserp.service;

import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.wms.InboundExpected;
import com.smart.wmserp.domain.wms.Stock;
import com.smart.wmserp.domain.wms.StockHistory;
import com.smart.wmserp.repository.InboundExpectedRepository;
import com.smart.wmserp.repository.ItemRepository;
import com.smart.wmserp.repository.StockRepository;
import com.smart.wmserp.repository.StockHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WmsService {

    private final InboundExpectedRepository inboundExpectedRepository;
    private final StockRepository stockRepository;
    private final StockHistoryRepository stockHistoryRepository;
    private final ItemRepository itemRepository;

    /**
     * 입고 확정 처리
     */
    @Transactional
    public void confirmInbound(Long expectedId, int receivedQty) {
        InboundExpected expected = inboundExpectedRepository.findById(expectedId)
                .orElseThrow(() -> new RuntimeException("Inbound data not found"));

        if ("RECEIVED".equals(expected.getStatus())) {
            throw new RuntimeException("Already received");
        }

        int totalReceived = expected.getReceivedQuantity() + receivedQty;
        expected.setReceivedQuantity(totalReceived);
        
        if (totalReceived >= expected.getExpectedQuantity()) {
            expected.setStatus("RECEIVED");
        } else {
            expected.setStatus("PARTIAL");
        }

        Item item = expected.getItem();
        Stock stock = stockRepository.findByItem(item)
                .orElseGet(() -> Stock.builder().item(item).quantity(0).build());
        
        stock.addQuantity(receivedQty);
        stockRepository.save(stock);

        StockHistory history = StockHistory.builder()
                .item(item)
                .changeQuantity(receivedQty)
                .afterQuantity(stock.getQuantity())
                .transactionType("INBOUND")
                .referenceId(expected.getPurchaseOrder().getId())
                .build();
        stockHistoryRepository.save(history);
    }

    /**
     * 반품 처리 (Sales Return)
     */
    @Transactional
    public void processReturn(Long offerId, Long itemId, int returnQty, String reason) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Stock stock = stockRepository.findByItem(item)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
        stock.addQuantity(returnQty);
        stockRepository.save(stock);

        StockHistory history = StockHistory.builder()
                .item(item)
                .changeQuantity(returnQty)
                .afterQuantity(stock.getQuantity())
                .transactionType("SALES_RETURN")
                .reason(reason)
                .referenceId(offerId)
                .build();
        stockHistoryRepository.save(history);
    }

    /**
     * 재고 실사 및 조정 (Manual Adjustment)
     */
    @Transactional
    public void adjustStock(Long itemId, int newQuantity, String reason) {
        Stock stock = stockRepository.findByItemId(itemId)
                .orElseThrow(() -> new RuntimeException("Stock not found for item: " + itemId));

        int oldQuantity = stock.getQuantity();
        int diff = newQuantity - oldQuantity;

        if (diff == 0) return;

        stock.setQuantityManually(newQuantity);
        stockRepository.save(stock);

        StockHistory history = StockHistory.builder()
                .item(stock.getItem())
                .changeQuantity(diff)
                .afterQuantity(newQuantity)
                .transactionType("ADJUSTMENT")
                .reason(reason)
                .build();
        stockHistoryRepository.save(history);
    }
}
