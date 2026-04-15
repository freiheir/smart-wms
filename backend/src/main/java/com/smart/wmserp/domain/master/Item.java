package com.smart.wmserp.domain.master;

import com.smart.wmserp.util.PartNumberUtil;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "part_number", unique = true, nullable = false)
    private String partNumber; // 정제된 품번

    @Column(name = "raw_part_number")
    private String rawPartNumber; // 원본 품번

    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(name = "wholesale_price", precision = 18, scale = 4)
    private BigDecimal wholesalePrice; // 도매가 (Base)

    @Column(name = "multiplier", precision = 10, scale = 2)
    private BigDecimal multiplier; // 배수

    @Column(name = "weight_kg", precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(name = "cbm", precision = 10, scale = 4)
    private BigDecimal cbm;

    @Column(name = "currency")
    private String currency; // 통화

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @Builder.Default
    @Column(name = "use_yn")
    private String useYn = "Y";

    @Builder.Default
    @Column(name = "safety_stock")
    private Integer safetyStock = 0; // 안전 재고

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * 품번 정제 로직 적용 (Before Persist)
     */
    @PrePersist
    @PreUpdate
    public void preparePartNumber() {
        if (this.rawPartNumber != null) {
            this.partNumber = PartNumberUtil.clean(this.rawPartNumber);
        }
    }
}
