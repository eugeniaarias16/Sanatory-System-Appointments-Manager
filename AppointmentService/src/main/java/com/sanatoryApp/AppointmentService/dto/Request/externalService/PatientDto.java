package com.sanatoryApp.AppointmentService.dto.Request.externalService;

public record PatientDto(
        Long id,
        String firstName,
        String lastName,
        String dni,
        String email,
        String phoneNumber
) {
}
