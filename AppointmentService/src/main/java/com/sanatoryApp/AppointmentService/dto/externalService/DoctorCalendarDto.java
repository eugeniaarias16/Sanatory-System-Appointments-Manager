package com.sanatoryApp.AppointmentService.dto.externalService;

import java.util.List;

public record DoctorCalendarDto(
        Long id,
        Long doctorId,
        String name,
        boolean isActive,
        String timeZone,
        List<CalendarExceptionDto> calendarExceptions,
        List<AvailabilityPatternDto> availabilityPatterns
) {
}
