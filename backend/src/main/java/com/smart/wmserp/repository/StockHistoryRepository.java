package com.smart.wmserp.repository;

import com.smart.wmserp.domain.wms.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {
}
