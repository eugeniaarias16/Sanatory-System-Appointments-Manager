package com.sanatoryApp.HealthInsuranceService.repository;

import com.sanatoryApp.HealthInsuranceService.dto.Request.externalService.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service",url = "http://localhost:8084/api/v1")
public interface UserServiceApi {

    //Patient Service endpoints
    @GetMapping("/patient/dni/{dni}")
    PatientDto getPatientByDni(@PathVariable("dni")String dni );
}
