package com.smart.wmserp.repository;

import com.smart.wmserp.domain.master.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByPartNumber(String partNumber);
}
