package com.smart.wmserp.repository;

import com.smart.wmserp.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByItemCode(String itemCode);
    Optional<Item> findByBarcode(String barcode);
}
