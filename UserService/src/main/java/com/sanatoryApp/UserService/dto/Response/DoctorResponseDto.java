package com.sanatoryApp.UserService.dto.Response;

public record DoctorResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) { }
