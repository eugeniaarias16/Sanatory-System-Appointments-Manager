package com.sanatoryApp.HealthInsuranceService.dto.Request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO to modify existing health insurance policies")
public record HealthInsuranceUpdateDto (
        @Size(min = 5, max = 50, message = "The name must be between 5 and 50 characters long.")
        String companyName,

        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid international phone format")
        String phoneNumber,

        @Email(message = "Invalid email format")
        String email,

        Boolean isActive
){ }
