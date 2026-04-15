package com.smart.wmserp.repository;

import com.smart.wmserp.domain.master.Partner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {
    Optional<Partner> findByPartnerCode(String partnerCode);
}
