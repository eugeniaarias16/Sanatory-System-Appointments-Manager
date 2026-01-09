package com.sanatoryApp.HealthInsuranceService.dto.Request;

import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;


@Schema(description = "Dto to represent requests to create Coverage Plan Entity.")
public record CoveragePlanCreateDto(
        @NotNull(message = "Coverage Plan's health insurance id is mandatory.")
        Long healthInsuranceId,

        @NotBlank(message = "Coverage Plan's name is mandatory.")
        String name,

        @NotBlank(message = "Coverage Plan's description is mandatory.")
        String description,

        @NotNull(message = "Coverage Plan's coverage value is mandatory.")
        @DecimalMin(value = "0.00", message = "Coverage value must be at least 0.00")
        @DecimalMax(value = "100.00", message = "Coverage value must not exceed 100.00")
        @Schema(description = "Value between 0.00 and 100.00", example = "75.00")
        BigDecimal coverageValue
) {

    public CoveragePlan toEntity(HealthInsurance healthInsurance) {
        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setHealthInsurance(healthInsurance);
        coveragePlan.setName(this.name);
        coveragePlan.setDescription(this.description);
        coveragePlan.setCoverageValuePercentage(this.coverageValue);
        coveragePlan.setActive(true);
        return coveragePlan;
    }
}