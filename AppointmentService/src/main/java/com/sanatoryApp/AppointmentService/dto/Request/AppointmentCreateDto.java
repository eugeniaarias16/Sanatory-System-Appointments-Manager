package com.sanatoryApp.AppointmentService.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AppointmentCreateDto(
        @NotNull(message = "The doctor's ID is mandatory.")
        Long doctorId,

        @NotNull(message = "The patient dni is mandatory.")
        String patientDni,

        @NotNull(message = "The appointment type is mandatory.")
        Long appointmentTypeId,

        @NotNull
        @Schema(description = "Patient insurance credential number is mandatory ")
        String credentialNumber,

        @NotNull
        @Schema(description = "Doctor Calendar Id is mandatory")
        Long doctorCalendarId,

        @NotNull(message = "The date is mandatory.")
        @FutureOrPresent(message = "The date must be present or future.")
        LocalDate date,

        @Size(max = 200, message = "Notes cannot exceed 200 characters.")
        @Schema(description = "Additional notes (optional)")
        String notes
) {
    //Consultation Cost, Coverage Amount, and Amount To Pay will be defined according to the logic and records in the database.
}
