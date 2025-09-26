package com.sanatoryApp.HealthInsuranceService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private double coverageValuePercentage;
    private  boolean isActive;
}
