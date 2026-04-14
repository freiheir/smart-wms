package com.smart.wmserp.repository;

import com.smart.wmserp.domain.sales.OfferItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OfferItemRepository extends JpaRepository<OfferItem, Long> {
    @Query("SELECT SUM(oi.quantity) FROM OfferItem oi JOIN oi.offer o WHERE oi.item.id = :itemId AND o.status = :status")
    Integer sumQuantityByItemIdAndOfferStatus(@Param("itemId") Long itemId, @Param("status") String status);
}
