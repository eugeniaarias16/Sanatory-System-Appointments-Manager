package com.sanatoryApp.AppointmentService.dto.Request.externalService;

public record DoctorDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) { }
