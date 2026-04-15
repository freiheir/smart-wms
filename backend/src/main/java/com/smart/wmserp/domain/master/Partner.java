package com.smart.wmserp.domain.master;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partners")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Partner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partner_code", unique = true, nullable = false)
    private String partnerCode;

    @Column(name = "partner_name", nullable = false)
    private String partnerName;

    @Column(name = "partner_type")
    private String partnerType; // BUY, SELL, BOTH

    @Column(name = "currency")
    private String currency; // USD, EUR, JPY, KRW

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Builder.Default
    @Column(name = "use_yn")
    private String useYn = "Y";

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
