package com.sanatoryApp.HealthInsuranceService.dto.Request;

import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "DTO to represent requests to create Patient Insurance Entity")
public record PatientInsuranceCreateDto(
        @NotBlank(message = "Patient's DNI is mandatory.")
        @Schema(description = "Patient's national identification number", example = "12345678")
        String patientDni,

        @NotBlank(message = "Credential number is mandatory.")
        @Schema(description = "Insurance credential number", example = "CRED-2024-001")
        String credentialNumber,

        @NotNull(message = "Patient's health insurance ID is mandatory.")
        @Schema(description = "Health insurance company identifier")
        Long healthInsuranceId,

        @NotNull(message = "Patient's coverage plan ID is mandatory.")
        @Schema(description = "Coverage plan identifier")
        Long coveragePlanId
) {
    public PatientInsurance toEntity(HealthInsurance healthInsurance, CoveragePlan coveragePlan) {
        PatientInsurance patientInsurance = new PatientInsurance();
        patientInsurance.setPatientDni(patientDni);
        patientInsurance.setCredentialNumber(credentialNumber);
        patientInsurance.setHealthInsurance(healthInsurance);
        patientInsurance.setCoveragePlan(coveragePlan);
        patientInsurance.setIsActive(true);
        return patientInsurance;
    }
}