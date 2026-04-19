package com.smart.wmserp.repository;

import com.smart.wmserp.domain.master.WeightPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WeightPolicyRepository extends JpaRepository<WeightPolicy, Long> {
    Optional<WeightPolicy> findByItemCode(String itemCode);
}
