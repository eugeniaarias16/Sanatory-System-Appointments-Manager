package com.sanatoryApp.HealthInsuranceService.dto.Request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CoveragePlanUpdateDto(
        String name,
        String description,
        @Positive
        BigDecimal coverageValuePercentage
) { }
