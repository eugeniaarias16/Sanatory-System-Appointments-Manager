package com.sanatoryApp.HealthInsuranceService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String companyName;
    @Column(unique = true)
    private Long companyCode;
    @Column(unique = true)
    private String phoneNumber;
    @Column(unique = true)
    private String email;
    private boolean isActive;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "health_insurance_id")
    private List<CoveragePlan>coveragePlans;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_insurance_id")
    private List<PatientInsurance>patientInsurances;

}
