package com.sanatoryApp.CalendarService.dto.Request;

import com.sanatoryApp.CalendarService.entity.CalendarException;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import com.sanatoryApp.CalendarService.entity.ExceptionScope;
import com.sanatoryApp.CalendarService.entity.ExceptionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarExceptionCreateDto(

        Long doctorId,
        Long doctorCalendarId,
        @NotNull(message = "Start Date is required")
        @Future
        LocalDate startDate,
        @Schema(description = "If is a one-day exception, 'End Date' should be null or equals to start Date")
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime,
        @NotNull(message = "Exception type is required")
        ExceptionType exceptionType,
        String reason,
        @NotNull(message = "Exception scope is required")
        ExceptionScope scope


) {
    public CalendarException toEntity() {

        CalendarException calendarException = new CalendarException();
        calendarException.setScope(scope);

        switch (scope){
            case GLOBAL:
                // GLOBAL: no doctor, no calendar
                calendarException.setDoctorCalendar(null);
                calendarException.setDoctorId(null);
            break;
            case SEMI_GLOBAL:
                // SEMI_GLOBAL: only doctorId, no calendar
                calendarException.setDoctorId(doctorId);
                calendarException.setDoctorCalendar(null);
            break;
            case SPECIFIC:
                // SPECIFIC: doctorCalendar will be set by Service layer
                // Service must fetch DoctorCalendar by doctorCalendarId and set it
                calendarException.setDoctorCalendar(null); // Temporary, Service will set
                calendarException.setDoctorId(null); // Will be set from calendar by Service
            break;
        }

        calendarException.setStartDate(startDate);
        calendarException.setEndDate(endDate);
        calendarException.setStartTime(startTime);
        calendarException.setEndTime(endTime);
        calendarException.setExceptionType(exceptionType);

        if (reason != null && !reason.trim().isEmpty()) {
            calendarException.setReason(reason.trim().toLowerCase());
        }

        return calendarException;
    }


}
