package com.sanatoryApp.HealthInsuranceService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "health_insurances")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(unique = true, nullable = false)
    private Long companyCode;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    @Column(unique = true, nullable = false)
    private String email;

    private boolean isActive;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    @JoinColumn(name = "health_insurance_id")
    private List<CoveragePlan> coveragePlans = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_insurance_id")
    private List<PatientInsurance> patientInsurances = new ArrayList<>();
}