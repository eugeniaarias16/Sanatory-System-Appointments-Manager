package com.sanatoryApp.CalendarService.entity;
import jakarta.persistence.*;
import java.time.ZoneId;
import java.util.List;

@Entity
@Table(name = "doctors_calendar")
public class DoctorCalendar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long doctorId;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    boolean isActive;
    @Column(nullable = false)
    private ZoneId timeZone;
    //Unilateral relation
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_calendar_id")
    private List<AvailabilityPattern>availabilityPatterns;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_calendar_id")
    private List<CalendarException>calendarExceptions;

}
