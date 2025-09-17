package com.sanatoryApp.UserService.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record DoctorUpdateDto(
        String firstName,
        String lastName,
        @Email(message = "Invalid format")
        String email,
        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid international phone format")
        @Schema(description = "Phone Number in international format ",example = "+5491112345678")
        String phoneNumber

) { }
