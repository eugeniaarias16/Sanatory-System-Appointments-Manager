package com.sanatoryApp.AppointmentService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long doctorId;

    @Column(nullable = false)
    private Long doctorCalendarId;

    @Column(nullable = false)
    private Long patientId;


    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "appointment_type_id",nullable = false)
    private AppointmentType appointmentType;

    private Long patientInsuranceId;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(nullable = false)
    private BigDecimal consultationCost;

    @Column(nullable = false)
    private  BigDecimal coveragePercentage;

    @Column(nullable = false)
    private  BigDecimal amountToPay;

    private String notes;

    @Column(nullable = false,updatable = false)
    private LocalDate createdAt;


    //AUTOMATIC METHOD FOR SETTING THE DATE
    @PrePersist
    protected void onCreate(){
        if(createdAt==null){
            createdAt=LocalDate.now();
        }
        if(status==null){
            status=AppointmentStatus.SCHEDULED;

        }
    }

}
