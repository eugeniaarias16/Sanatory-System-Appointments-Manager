package com.sanatoryApp.HealthInsuranceService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "coverages_plans")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CoveragePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long healthInsuranceId;

    @Column(unique = true)
    private String name;

    private String description;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal coverageValuePercentage;

    @Column(nullable = false)
    private boolean isActive = true;
}