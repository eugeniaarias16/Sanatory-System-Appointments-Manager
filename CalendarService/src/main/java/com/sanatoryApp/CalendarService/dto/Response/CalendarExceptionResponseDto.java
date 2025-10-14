package com.sanatoryApp.CalendarService.dto.Response;

import com.sanatoryApp.CalendarService.entity.CalendarException;
import com.sanatoryApp.CalendarService.entity.ExceptionType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarExceptionResponseDto(
        Long id,
        Long doctorCalendarId,
        @FutureOrPresent(message = "Exception date cannot be in the past")
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        ExceptionType exceptionType,
        @Size(max = 200, message = "Reason cannot exceed 200 characters")
        String reason,
        boolean isActive,
        boolean isGlobal
) {
        public static CalendarExceptionResponseDto fromEntity(CalendarException calendarException){
                return new CalendarExceptionResponseDto(
                        calendarException.getId(),
                        calendarException.getDoctorCalendarId(),
                        calendarException.getDate(),
                        calendarException.getStartTime(),
                        calendarException.getEndTime(),
                        calendarException.getExceptionType(),
                        calendarException.getReason(),
                        calendarException.isActive(),
                        calendarException.isGlobal()
                );
        }
}
