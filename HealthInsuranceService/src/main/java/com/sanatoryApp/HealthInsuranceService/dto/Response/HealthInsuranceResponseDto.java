package com.sanatoryApp.HealthInsuranceService.dto.Response;


import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Dto to return public responses about Health Insurance Entity.")
public record HealthInsuranceResponseDto(
        @Schema(description = "Unique insurance ID")
        Long id,

        @Schema(description = "Company name", example = "Health Insurance Company")
        String companyName,

        @Schema(description = "Company code")
        int companyCode,

        @Schema(description = "Contact telephone number")
        String phoneNumber,

        @Schema(description = "Contact email")
        String email,

        @Schema(description = "Active/inactive status")
        boolean isActive
) {

    public static HealthInsuranceResponseDto fromEntity(HealthInsurance healthInsurance) {
        return new HealthInsuranceResponseDto(
                healthInsurance.getId(),
                healthInsurance.getCompanyName(),
                healthInsurance.getCompanyCode(),
                healthInsurance.getPhoneNumber(),
                healthInsurance.getEmail(),
                healthInsurance.isActive());
    }
}
