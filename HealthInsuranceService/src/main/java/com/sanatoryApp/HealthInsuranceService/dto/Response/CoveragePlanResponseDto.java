package com.sanatoryApp.HealthInsuranceService.dto.Response;

import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;

public record CoveragePlanResponseDto(
        Long id,
        Long healthInsuranceId,
        String name,
        String description,
        Double coverageValue,
        boolean isActive
) {
    public static CoveragePlanResponseDto fromEntity(CoveragePlan  coveragePlan){
        return new CoveragePlanResponseDto(
                coveragePlan.getId(),
                coveragePlan.getHealthInsuranceId(),
                coveragePlan.getName(),
                coveragePlan.getDescription(),
                coveragePlan.getCoverageValuePercentage(),
                coveragePlan.isActive()
        );
    }
}
