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
@Table(name = "Patients_Insurance")
public class PatientInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long credentialNumber;
    private Long healthInsuranceId;
    private Long coveragePlanId;
    private LocalDate createdAt;
    private LocalDate expirationDate;
    private boolean isActive;

    private void setCreatedDate(){
        createdAt=LocalDate.now();
    }



}
