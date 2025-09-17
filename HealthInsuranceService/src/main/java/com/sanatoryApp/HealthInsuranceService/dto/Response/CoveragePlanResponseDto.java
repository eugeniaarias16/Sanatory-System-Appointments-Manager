package com.sanatoryApp.HealthInsuranceService.dto.Response;

public record CoveragePlanResponseDto(
        Long id,
        Long healthInsuranceId,
        String name,
        String description,
        Double coverageValue,
        boolean isActive
) {
}
