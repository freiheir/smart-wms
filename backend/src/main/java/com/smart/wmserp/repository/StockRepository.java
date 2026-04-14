package com.smart.wmserp.repository;

import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.wms.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByItem(Item item);
    Optional<Stock> findByItemId(Long itemId);
}
