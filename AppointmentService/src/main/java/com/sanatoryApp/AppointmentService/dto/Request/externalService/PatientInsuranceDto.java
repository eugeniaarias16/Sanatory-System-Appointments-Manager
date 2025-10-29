package com.sanatoryApp.AppointmentService.dto.Request.externalService;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record PatientInsuranceDto(

        Long id,
        String patientDni,
        String credentialNumber,
        Long healthInsuranceId,
        Long coveragePlanId,
        LocalDate createdAt,
        Boolean isActive
) {
}
