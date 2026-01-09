package com.sanatoryApp.UserService.dto.Request;

import com.sanatoryApp.UserService.entity.Patient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PatientCreateDto(
        @NotBlank(message = "First Name is mandatory")
        String firstName,

        @NotBlank(message = "Last Name is mandatory")
        String lastName,

        @NotBlank(message = "Dni is mandatory")
        String dni,

        @Email(message = "Invalid Format")
        @NotBlank(message = "Email is mandatory")
        String email,

        @NotBlank
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid international phone format")
        String phoneNumber
) {
    public Patient toEntity(){
        Patient patient = new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setDni(dni);
        patient.setEmail(email);
        patient.setPhoneNumber(phoneNumber);
        return patient;
    }
}
