package com.sanatoryApp.CalendarService.repository;

import com.sanatoryApp.CalendarService.dto.Request.externalService.DoctorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service",url = "http://localhost:8084/api/v1")
public interface UserServiceApi {


    @GetMapping("/doctor/{id}")
    DoctorDto getDoctorById(@PathVariable("id")Long id);

}
