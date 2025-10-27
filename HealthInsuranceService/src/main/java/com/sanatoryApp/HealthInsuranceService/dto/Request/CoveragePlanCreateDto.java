package com.sanatoryApp.HealthInsuranceService.dto.Request;


import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dto to represent requests to create Coverage Plan Entity.")
public class CoveragePlanCreateDto {


    @NotBlank(message = "Coverages Plan's name is mandatory.")
    private String name;

    @NotBlank(message = "Coverages Plan's health insurance id is mandatory.")
    private Long healthInsuranceId;

    @NotBlank(message = "Coverages Plan's description is mandatory.")
    private String description;

    @NotNull(message = "Coverages Plan's coverage is mandatory.")
    @DecimalMin(value = "00.00")
    @DecimalMax(value = "100.00")
    @Schema(description = "Value between 00.0 and 100.0", example = "75.00")
    private BigDecimal coverageValue;

    private  boolean isActive;

    public CoveragePlan toEntity(){
        CoveragePlan coveragePlan=new CoveragePlan();
        coveragePlan.setName(name);
        coveragePlan.setDescription(description);
        coveragePlan.setCoverageValuePercentage(coverageValue);
        coveragePlan.setActive(true);
        return coveragePlan;
    }
}
