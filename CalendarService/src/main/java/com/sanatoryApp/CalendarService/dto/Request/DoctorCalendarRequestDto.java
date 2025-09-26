package com.sanatoryApp.CalendarService.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DoctorCalendarRequestDto(
        @NotNull(message = "Doctor ID is mandatory")
        Long doctorId,

        @NotBlank(message = "Calendar name is mandatory")
        String name,

        @NotBlank(message = "TimeZone is mandatory")
        String timeZone
) { }
