package com.sanatoryApp.CalendarService.dto.Request;


import jakarta.validation.constraints.NotBlank;

public record DoctorCalendarUpdateDto(
        Long doctorId,
        String name,
        String timeZone
) { }
