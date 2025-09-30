package com.sanatoryApp.HealthInsuranceService.dto.Request;


import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dto to represent requests to create Coverage Plan Entity.")
public class CoveragePlanCreateDto {


    @NotBlank(message = "Coverages Plan's name is mandatory.")
    private String name;
    @NotBlank(message = "Coverages Plan's description is mandatory.")
    private String description;
    @NotEmpty(message = "Coverages Plan's coverage is mandatory.")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "1.0")
    @Schema(description = "Value between 0.0 and 1.0", example = "0.75")
    private double coverageValue;
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
