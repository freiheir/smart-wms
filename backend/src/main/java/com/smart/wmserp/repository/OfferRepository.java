package com.smart.wmserp.repository;

import com.smart.wmserp.domain.sales.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface OfferRepository extends JpaRepository<Offer, Long> {
    long countByStatus(String status);
    long count();
    
    List<Offer> findByStatus(String status); // 미출고(PI_CONVERTED) 조회를 위해 사용

    @Query("SELECT o FROM Offer o WHERE o.createdAt BETWEEN :start AND :end AND o.status IN ('PI_CONVERTED', 'SHIPPED')")
    List<Offer> findConfirmedByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
