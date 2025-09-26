package com.sanatoryApp.CalendarService.dto.Request;

import com.sanatoryApp.CalendarService.entity.ExceptionType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarExceptionRequestDto(
        @NotNull
        Long doctorCalendarId,
        @NotNull
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        @NotNull
        ExceptionType exceptionType,
        String reason
) { }
