package com.sanatoryApp.HealthInsuranceService.dto.Response;

import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Response with patient insurance information")
public record PatientInsuranceResponseDto(

        @Schema(description = "Patient insurance unique identifier")
        Long id,

        @Schema(description = "Patient unique identifier")
        Long patientId,

        @Schema(description = "Insurance credential number")
        String credentialNumber,

        @Schema(description = "Health insurance company identifier")
        Long healthInsuranceId,

        @Schema(description = "Coverage plan identifier")
        Long coveragePlanId,

        @Schema(description = "Record creation date")
        LocalDate createdAt,

        @Schema(description = "Insurance active status")
        Boolean isActive
) {
    public static PatientInsuranceResponseDto fromEntity(PatientInsurance patientInsurance){
        return new PatientInsuranceResponseDto(
                patientInsurance.getId(),
                patientInsurance.getPatientId(),
                patientInsurance.getCredentialNumber(),
                patientInsurance.getHealthInsuranceId(),
                patientInsurance.getCoveragePlanId(),
                patientInsurance.getCreatedAt(),
                patientInsurance.getIsActive()
        );
    }
}
