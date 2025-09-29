package com.sanatoryApp.UserService.dto.Response;

import com.sanatoryApp.UserService.entity.Doctor;

public record DoctorResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phoneNumber
) {
    public static DoctorResponseDto fromEntity(Doctor doctor){
        return new DoctorResponseDto(
                doctor.getId(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getEmail(),
                doctor.getPhoneNumber()
        );
    }
}
