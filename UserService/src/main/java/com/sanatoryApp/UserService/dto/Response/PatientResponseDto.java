package com.sanatoryApp.UserService.dto.Response;

public record PatientResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) { }
