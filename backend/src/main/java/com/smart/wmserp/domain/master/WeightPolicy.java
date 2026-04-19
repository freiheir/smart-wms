package com.smart.wmserp.domain.master;

import com.smart.wmserp.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "weight_policy")
public class WeightPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String itemCode;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal multiplier;
}
