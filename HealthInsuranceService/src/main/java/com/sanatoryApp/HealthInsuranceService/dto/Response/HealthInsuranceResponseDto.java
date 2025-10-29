package com.sanatoryApp.HealthInsuranceService.dto.Response;

import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for Health Insurance entity responses")
public record HealthInsuranceResponseDto(

        @Schema(description = "Unique identifier", example = "1")
        Long id,

        @Schema(description = "Insurance company name", example = "Blue Cross Health Insurance")
        String companyName,

        @Schema(description = "Company identification code", example = "12345")
        Long companyCode,

        @Schema(description = "Contact phone number", example = "+5491112345678")
        String phoneNumber,

        @Schema(description = "Contact email address", example = "contact@insurance.com")
        String email,

        @Schema(description = "Indicates if the insurance is active", example = "true")
        boolean isActive
) {

    public static HealthInsuranceResponseDto fromEntity(HealthInsurance healthInsurance) {
        return new HealthInsuranceResponseDto(
                healthInsurance.getId(),
                healthInsurance.getCompanyName(),
                healthInsurance.getCompanyCode(),
                healthInsurance.getPhoneNumber(),
                healthInsurance.getEmail(),
                healthInsurance.isActive()
        );
    }
}