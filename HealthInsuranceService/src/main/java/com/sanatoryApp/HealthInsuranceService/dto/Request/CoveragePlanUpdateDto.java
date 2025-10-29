package com.sanatoryApp.HealthInsuranceService.dto.Request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record CoveragePlanUpdateDto(
        String name,
        String description,
        @DecimalMin(value = "0.00", message = "Coverage value must be at least 0.00")
        @DecimalMax(value = "100.00", message = "Coverage value must not exceed 100.00")
        BigDecimal coverageValuePercentage
) {
}