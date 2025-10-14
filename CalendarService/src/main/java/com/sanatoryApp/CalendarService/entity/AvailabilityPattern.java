package com.sanatoryApp.CalendarService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "availability_patterns")
public class AvailabilityPattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long doctorCalendarId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private java.time.DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private boolean isActive=true;

    public void setIsActive(boolean isActive){
        this.isActive=isActive;
    }


}
