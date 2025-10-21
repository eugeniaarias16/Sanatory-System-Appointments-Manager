package com.sanatoryApp.AppointmentService.dto.Request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentCancelDto(
        @NotNull
        Long patientId,
        @NotNull
        LocalDateTime date,
        String doctorLastName,
        String doctorFirstName

) {
}
