package com.sanatoryApp.HealthInsuranceService.dto.Request.externalService;

public record PatientDto(
        Long id,
        String dni,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) { }
