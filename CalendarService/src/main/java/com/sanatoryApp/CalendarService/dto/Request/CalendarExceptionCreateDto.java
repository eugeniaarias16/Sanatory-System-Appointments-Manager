package com.sanatoryApp.CalendarService.dto.Request;

import com.sanatoryApp.CalendarService.entity.CalendarException;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import com.sanatoryApp.CalendarService.entity.ExceptionType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarExceptionCreateDto(
        @NotNull(message = "Doctor Calendar ID is required")
        Long doctorCalendarId,

        @NotNull(message = "Date is required")
        LocalDate date,

        LocalTime startTime,

        LocalTime endTime,

        @NotNull(message = "Exception type is required")
        ExceptionType exceptionType,

        String reason,

        @NotNull(message = "isGlobal flag is required")
        boolean isGlobal
) {
    public CalendarException toEntity(DoctorCalendar doctorCalendar) {
        CalendarException calendarException = new CalendarException();
        calendarException.setDoctorCalendar(doctorCalendar);
        calendarException.setDate(date);
        calendarException.setStartTime(startTime);
        calendarException.setEndTime(endTime);
        calendarException.setExceptionType(exceptionType);

        if (reason != null && !reason.trim().isEmpty()) {
            calendarException.setReason(reason.trim().toLowerCase());
        }

        calendarException.setGlobal(isGlobal);
        return calendarException;
    }
}
