package com.sanatoryApp.CalendarService.dto.Request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarExceptionUpdateDto(
        @NotNull Long doctorCalendarId,
        @NotNull LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        boolean isGlobal,
        String reason
        ) { }
