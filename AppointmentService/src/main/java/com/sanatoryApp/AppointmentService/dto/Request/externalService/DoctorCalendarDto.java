package com.sanatoryApp.AppointmentService.dto.Request.externalService;

public record DoctorCalendarDto(
        Long id,
        Long doctorId,
        String name,
        boolean isActive,
        String timeZone
) {
}
