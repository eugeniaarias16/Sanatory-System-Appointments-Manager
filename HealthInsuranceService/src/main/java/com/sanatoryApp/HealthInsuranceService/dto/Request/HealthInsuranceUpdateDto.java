package com.sanatoryApp.HealthInsuranceService.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "DTO for updating an existing Health Insurance entity")
public record HealthInsuranceUpdateDto(

        @Size(min = 5, max = 50, message = "Company name must be between 5 and 50 characters")
        @Schema(description = "Updated company name", example = "Blue Cross Health Insurance")
        String companyName,

        @Positive(message = "Company code must be positive")
        @Schema(description = "Updated company code", example = "12345")
        Long companyCode,

        @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in international format (E.164)")
        @Schema(description = "Updated phone number", example = "+5491112345678")
        String phoneNumber,

        @Email(message = "Email must be valid")
        @Schema(description = "Updated email address", example = "contact@insurance.com")
        String email
) { }