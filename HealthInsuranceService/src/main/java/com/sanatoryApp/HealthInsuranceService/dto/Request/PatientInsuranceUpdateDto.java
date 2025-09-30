package com.sanatoryApp.HealthInsuranceService.dto.Request;

import java.time.LocalDate;

public record PatientInsuranceUpdateDto(
        Long id,
        Long patientId,
        String credentialNumber,
        Long healthInsuranceId,
        Long coveragePlanId,
        LocalDate createdAt,
        Boolean isActive
) { }
