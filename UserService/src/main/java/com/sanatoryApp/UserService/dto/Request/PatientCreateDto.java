package com.sanatoryApp.UserService.dto.Request;

import com.sanatoryApp.UserService.entity.Patient;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PatientCreateDto {
    @NotBlank(message = "First Name is mandatory")
    private String firstName;

    @NotBlank(message = "Last Name is mandatory")
    private String lastName;

    @Email(message = "Invalid Format")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid international phone format")
    @Schema(description = "Phone Number in international format ",example = "+5491112345678")
    private String phoneNumber;

    public Patient toEntity(){
        Patient patient=new Patient();
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        patient.setEmail(email);
        patient.setPhoneNumber(phoneNumber);
        return patient;
    }
}
