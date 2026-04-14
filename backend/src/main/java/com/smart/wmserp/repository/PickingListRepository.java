package com.smart.wmserp.repository;

import com.smart.wmserp.domain.wms.PickingList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PickingListRepository extends JpaRepository<PickingList, Long> {
}
