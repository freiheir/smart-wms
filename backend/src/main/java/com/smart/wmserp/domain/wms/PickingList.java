package com.smart.wmserp.domain.wms;

import com.smart.wmserp.domain.sales.Offer;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "picking_lists")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id")
    private Offer offer;

    @Builder.Default
    @Column(name = "status")
    private String status = "READY";

    @Builder.Default
    @OneToMany(mappedBy = "pickingList", cascade = CascadeType.ALL)
    private List<PickingItem> items = new ArrayList<>();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
