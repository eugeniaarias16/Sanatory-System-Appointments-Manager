package com.sanatoryApp.CalendarService.dto.Response;
import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityPatternResponseDto(
        Long id,
        Long doctorCalendarId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
}
