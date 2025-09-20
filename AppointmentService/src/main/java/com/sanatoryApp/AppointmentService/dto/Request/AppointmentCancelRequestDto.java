package com.sanatoryApp.AppointmentService.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AppointmentCancelRequestDto(
        @NotNull
        Long patientId,
        @NotNull
        LocalDateTime date,
        String doctorLastName,
        String doctorFirstName

) {
}
