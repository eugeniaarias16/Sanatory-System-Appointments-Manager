package com.sanatoryApp.HealthInsuranceService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Health_Insurances")


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
    private int companyCode;
    @Column(unique = true)
    private String phoneNumber;
    @Column(unique = true)
    private String email;
    private boolean isActive;


}
