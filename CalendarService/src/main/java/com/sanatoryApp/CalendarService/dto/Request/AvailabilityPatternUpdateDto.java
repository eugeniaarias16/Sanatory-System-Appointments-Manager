package com.sanatoryApp.CalendarService.dto.Request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record AvailabilityPatternUpdateDto(
        @NotNull LocalTime startTime,
        @NotNull LocalTime endTime
) {
}