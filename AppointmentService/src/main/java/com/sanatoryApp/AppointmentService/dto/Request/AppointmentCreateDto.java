package com.sanatoryApp.AppointmentService.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentCreateDto {

    @NotNull(message = "The doctor's ID is mandatory.")
    private Long doctorId;

    @NotNull(message = "The patient ID is mandatory.")
    private Long patientId;

    @NotNull(message = "The appointment type is mandatory.")
    private Long appointmentTypeId;

    @Schema(description = "Patient insurance ID ")
    private Long patientInsuranceId;

    @NotNull(message = "The date is mandatory.")
    @FutureOrPresent(message = "The date must be present or future.")
    private LocalDateTime date;

    @Size(max = 200, message = "Notes cannot exceed 200 characters.")
    @Schema(description = "Additional notes (optional)")
    private String notes;


     //Consultation Cost, Coverage Amount, and Amount To Pay will be defined according to the logic and records in the database.


}
