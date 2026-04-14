package com.smart.wmserp.repository;

import com.smart.wmserp.domain.master.Item;
import com.smart.wmserp.domain.wms.StockLot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockLotRepository extends JpaRepository<StockLot, Long> {
    List<StockLot> findByItemAndQuantityGreaterThanOrderByInboundDateAsc(Item item, int quantity);
}
