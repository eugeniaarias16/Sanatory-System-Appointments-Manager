package com.sanatoryApp.HealthInsuranceService.dto.Request;

public record CoveragePlanUpdateDto(
        String name,
        String description,
        Double coverageValue,
        boolean isActive
) {

}
