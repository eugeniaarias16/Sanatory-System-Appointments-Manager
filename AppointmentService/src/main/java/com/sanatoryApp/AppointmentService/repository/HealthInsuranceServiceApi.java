package com.sanatoryApp.AppointmentService.repository;

import com.sanatoryApp.AppointmentService.dto.Request.externalService.CoveragePlanDto;
import com.sanatoryApp.AppointmentService.dto.Request.externalService.PatientInsuranceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "health-insurance-service",url = "${health-insurance-service.url}")
public interface HealthInsuranceServiceApi {

    @GetMapping("/patientInsurance/credentialNumber/{credentialNumber}")
    PatientInsuranceDto getPatientInsuranceByCredentialNumber(String credentialNumber);

    @GetMapping("/coveragePlan/{id}")
    CoveragePlanDto getCoveragePlanById(@Param("id")Long id);
}
