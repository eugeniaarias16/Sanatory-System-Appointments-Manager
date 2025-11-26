package com.sanatoryApp.AppointmentService.repository;

import com.sanatoryApp.AppointmentService.dto.Request.externalService.DoctorDto;
import com.sanatoryApp.AppointmentService.dto.Request.externalService.PatientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceApi {

    @GetMapping("/doctor/{id}")
    DoctorDto getDoctorById(@PathVariable Long id);

    @GetMapping("/patient/dni/{dni}")
    PatientDto getPatientByDni(@PathVariable String dni);
}
