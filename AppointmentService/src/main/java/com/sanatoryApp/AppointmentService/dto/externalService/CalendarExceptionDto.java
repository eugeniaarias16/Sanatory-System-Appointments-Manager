package com.sanatoryApp.AppointmentService.dto.externalService;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarExceptionDto(
        Long id,
        String scope,
        Long doctorId,
        Long doctorCalendarId,
        String doctorCalendarName,
        LocalDate startDate,
        LocalDate endDate,
        LocalTime startTime,
        LocalTime endTime,
        String exceptionType,
        String reason,
        boolean isActive,
        boolean isFullDay,
        boolean isSingleDay
) {
}
