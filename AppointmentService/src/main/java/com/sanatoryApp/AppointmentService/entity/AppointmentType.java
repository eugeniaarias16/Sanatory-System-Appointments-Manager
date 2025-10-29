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
    @Column(nullable = false,unique = true)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private int durationMin;
    @Column(nullable = false)
    private int bufferTimeMin;
    @Column(nullable = false)
    private BigDecimal basePrice;
    @Column(nullable = false)
    private boolean isActive;

    @PrePersist
    public void prePersist() {
        if (!this.isActive) {
            this.isActive = true;
        }
    }
}
