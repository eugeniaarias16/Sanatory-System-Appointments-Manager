package com.sanatoryApp.UserService.dto.Response;

import com.sanatoryApp.UserService.entity.Patient;

public record PatientResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
    public static PatientResponseDto fromEntity(Patient patient){
        return new PatientResponseDto(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getPhoneNumber()
        );
    }
}
