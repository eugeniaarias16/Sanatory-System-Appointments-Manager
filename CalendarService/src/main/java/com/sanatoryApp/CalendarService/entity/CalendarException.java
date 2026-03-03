package com.sanatoryApp.CalendarService.entity;

import com.sanatoryApp.CalendarService.utils.TimeConstants;
import com.sanatoryApp.CalendarService.utils.TimeValidationUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
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


    /*
     * The specific calendar this exception applies to.
     * - GLOBAL: NULL (applies to all calendars)
     * - SEMI_GLOBAL: NULL (applies to all calendars of same doctor)
     * - SPECIFIC: NOT NULL (applies to this calendar only)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_calendar_id", nullable = true)
    private DoctorCalendar doctorCalendar;

    /*
     * The doctor ID for SEMI_GLOBAL exceptions.
     * - GLOBAL: NULL
     * - SEMI_GLOBAL: NOT NULL (used to find all calendars of this doctor)
     * - SPECIFIC: doctorCalendar.getDoctorId() (redundant but useful for queries)
     */
    @Column(name = "doctor_id", nullable = true)
    private Long doctorId;

    /*
     * Defines the scope/reach of this exception
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExceptionScope scope;

    /* Start date of the exception (inclusive)*/
    @Column(name = "start_date", nullable = false)
    @Future(message = "Start date must be future")
    private LocalDate startDate;

    /*
     * End date of the exception (inclusive)
     * - NULL: single-day exception (use startDate only)
     * - NOT NULL: multi-day exception (range from startDate to endDate)
     */
    @Column(name = "end_date", nullable = true)
    @Future(message = "End date must be future")
    private LocalDate endDate;

    private LocalTime startTime;

    private LocalTime endTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ExceptionType exceptionType;

    private String reason;

    @Column(nullable = false, name = "is_full_day")
    private boolean isFullDay;

    @Column
    private boolean isSingleDay;

    @Column(nullable = false)
    private boolean isActive;


    /*
     * Get the effective end date (use startDate if endDate is null)
     * @return endDate if not null, otherwise startDate
     */
    public LocalDate getEffectiveEndDate() {
        return endDate != null ? endDate : startDate;
    }

    @PrePersist
    @PreUpdate
    public void validateConsistency(){
        // Validate scope consistency
        switch (scope){
            case GLOBAL:
                if(doctorCalendar!=null || doctorId!=null){
                    throw new IllegalStateException("GLOBAL exceptions must have null doctorCalendar and null doctorId");
                }
            break;
            case SEMI_GLOBAL:
                if (doctorCalendar!=null||doctorId==null){
                    throw new IllegalStateException("SEMI_GLOBAL exceptions must have null doctorCalendar and non-null doctorId");
                }
            break;
            case SPECIFIC:
                if(doctorCalendar==null){
                    throw new IllegalStateException("SPECIFIC exceptions must have non-null doctorCalendar");
                }
                // Set doctorId from calendar for query optimization
                if(doctorId == null && doctorCalendar != null){
                    this.doctorId = doctorCalendar.getDoctorId();
                }
            break;
        }

        // Validate date range
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalStateException("End date must be equal to or after start date");
        }

        // Set default active state
        if (!this.isActive) {
            this.isActive = true;
        }

        this.isSingleDay = (endDate == null);

        this.isFullDay = TimeValidationUtils.isFullDayRange(startTime, endTime);
        if (this.isFullDay) {
            this.startTime = TimeConstants.START_OF_DAY;
            this.endTime = TimeConstants.END_OF_DAY;
        }
    }
}