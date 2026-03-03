package com.sanatoryApp.AppointmentService.dto.externalService;

public record DoctorDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) { }
