package com.sanatoryApp.AppointmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "appointment_types")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AppointmentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private int durationMin;
    private int bufferTimeMin;
    private BigDecimal basePrice;
    private boolean isActive;
}
