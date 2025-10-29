package com.sanatoryApp.CalendarService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.util.List;

@Data
@Entity
@Table(name = "doctors_calendar")
@AllArgsConstructor
@NoArgsConstructor
public class DoctorCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long doctorId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private ZoneId timeZone;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "doctor_calendar_id")
    private List<AvailabilityPattern> availabilityPatterns;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "doctor_calendar_id")
    private List<CalendarException> calendarExceptions;

    @PrePersist
    public void prePersist() {
        if (!this.isActive) {
            this.isActive = true;
        }
    }
}