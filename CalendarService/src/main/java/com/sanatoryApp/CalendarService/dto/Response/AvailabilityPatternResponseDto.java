package com.sanatoryApp.CalendarService.dto.Response;

import com.sanatoryApp.CalendarService.entity.AvailabilityPattern;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityPatternResponseDto(
        Long id,
        Long doctorCalendarId,
        String doctorCalendarName,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        boolean isActive
) {
    public static AvailabilityPatternResponseDto fromEntity(AvailabilityPattern availabilityPattern) {
        return new AvailabilityPatternResponseDto(
                availabilityPattern.getId(),
                availabilityPattern.getDoctorCalendar().getId(),
                availabilityPattern.getDoctorCalendar().getName(),
                availabilityPattern.getDayOfWeek(),
                availabilityPattern.getStartTime(),
                availabilityPattern.getEndTime(),
                availabilityPattern.isActive()
        );
    }
}