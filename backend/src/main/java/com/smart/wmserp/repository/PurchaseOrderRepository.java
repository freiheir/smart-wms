package com.smart.wmserp.repository;

import com.smart.wmserp.domain.purchase.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
}
