package com.sanatoryApp.HealthInsuranceService.dto.Response;

import com.sanatoryApp.HealthInsuranceService.dto.Request.externalService.PatientDto;
import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;

import java.time.LocalDate;

public record PatientInsuranceCreateResponseDto (
        Long id,
        String patientDni,
        String patientFirstName,
        String patientLastName,
        String patientEmail,
        String credentialNumber,
        Long healthInsuranceId,
        Long coveragePlanId,
        LocalDate createdAt,
        Boolean isActive
) {
    // Method for combining PatientInsurance + PatientDto
    public static PatientInsuranceCreateResponseDto fromEntities(
            PatientInsurance patientInsurance,
            PatientDto patientDto) {

        return new PatientInsuranceCreateResponseDto(
                patientInsurance.getId(),
                patientInsurance.getPatientDni(),
                patientDto.firstName(),      // It comes from user-service.
                patientDto.lastName(),       // It comes from user-service.
                patientDto.email(),          // It comes from user-service.
                patientInsurance.getCredentialNumber(),
                patientInsurance.getHealthInsuranceId(),
                patientInsurance.getCoveragePlanId(),
                patientInsurance.getCreatedAt(),
                patientInsurance.getIsActive()
        );
    }
}
