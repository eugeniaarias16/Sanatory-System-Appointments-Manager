package com.sanatoryApp.CalendarService.dto.Request;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityPatternRequestDto(
        @NotNull
        Long doctorCalendarId,
        @NotNull
        DayOfWeek dayOfWeek,
        @NotNull
        LocalTime startTime,
        @NotNull
        LocalTime endTime
) { }
