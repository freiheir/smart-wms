package com.smart.wmserp.domain.sales;

import com.smart.wmserp.domain.master.Partner;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "offers")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inquiry_no", unique = true)
    private String inquiryNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @Builder.Default
    @Column(name = "status")
    private String status = "INQUIRY"; // INQUIRY, OFFER, PI_CONVERTED

    @Column(name = "total_amount", precision = 18, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "manager_email")
    private String managerEmail;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @com.fasterxml.jackson.annotation.JsonManagedReference
    @Builder.Default
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfferItem> items = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    public void addItem(OfferItem item) {
        items.add(item);
        item.setOffer(this);
    }
}
