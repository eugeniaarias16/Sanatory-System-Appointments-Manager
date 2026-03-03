package com.sanatoryApp.AppointmentService.dto.externalService;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityPatternDto(
        Long id,
        Long doctorCalendarId,
        String doctorCalendarName,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        boolean isActive
) {
}
