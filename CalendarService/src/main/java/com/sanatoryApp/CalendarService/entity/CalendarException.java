package com.sanatoryApp.CalendarService.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "calendar_exceptions")
public class CalendarException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long doctorCalendarId;

    @Column(nullable = false)
    @FutureOrPresent(message = "Date must be present or future")
    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExceptionType exceptionType;

    private String reason;

    @Column(nullable = false)
    private boolean isGlobal;

    @Column(nullable = false, name = "is_full_day")
    private boolean isFullDay;

    @Column(nullable = false)
    private boolean isActive;

    @PrePersist
    public void prePersist() {
        if (!this.isActive) {
            this.isActive = true;
        }
    }
}