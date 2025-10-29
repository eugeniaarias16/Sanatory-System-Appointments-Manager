package com.sanatoryApp.AppointmentService.dto.Request.externalService;

import java.math.BigDecimal;

public record CoveragePlanDto(
        Long id,
        Long healthInsuranceId,
        String name,
        String description,
        BigDecimal coverageValuePercentage,
        boolean isActive
) {
}
