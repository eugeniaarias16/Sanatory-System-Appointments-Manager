package com.sanatoryApp.CalendarService.dto.Response;
import com.sanatoryApp.CalendarService.entity.AvailabilityPattern;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityPatternResponseDto(
        Long id,
        Long doctorCalendarId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {
    public static AvailabilityPatternResponseDto fromEntity(AvailabilityPattern availabilityPattern){
        return new AvailabilityPatternResponseDto(
                availabilityPattern.getId(),
                availabilityPattern.getDoctorCalendarId(),
                availabilityPattern.getDayOfWeek(),
                availabilityPattern.getStartTime(),
                availabilityPattern.getEndTime()
        );
    }
}
