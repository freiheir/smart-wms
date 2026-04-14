package com.smart.wmserp.service;

import com.smart.wmserp.repository.ItemRepository;
import com.smart.wmserp.repository.OfferItemRepository;
import com.smart.wmserp.repository.StockRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockQueryService {

    private final StockRepository stockRepository;
    private final OfferItemRepository offerItemRepository;
    private final ItemRepository itemRepository;

    /**
     * 특정 품목의 가용 재고 산출
     */
    public Integer getAvailableStock(Long itemId) {
        Integer physicalStock = stockRepository.findByItemId(itemId)
                .map(s -> s.getQuantity())
                .orElse(0);

        Integer committedStock = offerItemRepository.sumQuantityByItemIdAndOfferStatus(itemId, "PI_CONVERTED");
        if (committedStock == null) committedStock = 0;

        return Math.max(0, physicalStock - committedStock);
    }

    /**
     * 전체 품목의 재고 현황 조회 (실재고 및 가용재고)
     */
    public List<StockStatusDto> getAllStockStatuses() {
        return itemRepository.findAll().stream()
                .map(item -> {
                    Integer physical = stockRepository.findByItemId(item.getId())
                            .map(s -> s.getQuantity())
                            .orElse(0);
                    Integer committed = offerItemRepository.sumQuantityByItemIdAndOfferStatus(item.getId(), "PI_CONVERTED");
                    if (committed == null) committed = 0;
                    
                    return new StockStatusDto(
                            item.getId(),
                            item.getPartNumber(),
                            item.getItemName(),
                            physical,
                            Math.max(0, physical - committed),
                            item.getSafetyStock()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 현재 창고의 총 재고 자산 가액 계산
     */
    public BigDecimal calculateTotalInventoryValue() {
        return stockRepository.findAll().stream()
                .map(stock -> {
                    BigDecimal qty = new BigDecimal(stock.getQuantity());
                    BigDecimal price = stock.getItem().getWholesalePrice();
                    return qty.multiply(price);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @AllArgsConstructor
    @Getter
    public static class StockStatusDto {
        private Long itemId;
        private String partNumber;
        private String itemName;
        private Integer physicalStock;
        private Integer availableStock;
        private Integer safetyStock;
    }
}
