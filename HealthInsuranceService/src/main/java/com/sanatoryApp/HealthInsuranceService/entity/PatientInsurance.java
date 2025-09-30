package com.sanatoryApp.HealthInsuranceService.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "patients_insurance")
public class PatientInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(unique = true, nullable = false)
    private String credentialNumber;

    @Column(nullable = false)
    private Long healthInsuranceId;

    private Long coveragePlanId;

    @Column(nullable = false,updatable = false)
    private LocalDate createdAt;


    @Column(nullable = false,columnDefinition = "boolean default true")
    private Boolean isActive;

    @PrePersist
    protected void onCreate(){
        if(createdAt==null){
            createdAt=LocalDate.now();
        }
    }

}
