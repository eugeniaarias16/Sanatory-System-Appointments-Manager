package com.sanatoryApp.HealthInsuranceService.dto.Request;

import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dto to represent requests to create Patient Insurance Entity.")
public class PatientInsuranceCreateDto {

    @NotNull(message = "Patient's dni is mandatory.")
    private Long patientDni;
    @NotBlank(message = "Credential Number is mandatory.")
    private String credentialNumber;
    @NotNull(message = "Patient's health insurance id is mandatory.")
    private Long healthInsuranceId;
    @NotNull(message = "Patient's coverage plan id is mandatory.")
    private Long coveragePlanId;


    public PatientInsurance toEntity(){
        PatientInsurance patientInsurance=new PatientInsurance();
        patientInsurance.setPatientDni(patientDni);
        patientInsurance.setCredentialNumber(credentialNumber);
        patientInsurance.setHealthInsuranceId(healthInsuranceId);
        patientInsurance.setCoveragePlanId(coveragePlanId);
        return patientInsurance;
    }

}
