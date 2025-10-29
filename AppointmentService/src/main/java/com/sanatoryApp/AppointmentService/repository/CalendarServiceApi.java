package com.sanatoryApp.AppointmentService.repository;

import com.sanatoryApp.AppointmentService.dto.Request.externalService.DoctorCalendarDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "calendar-service",url = "http://localhost:8082/api/v1")
public interface CalendarServiceApi {

    @GetMapping("/doctorCalendar/{id}")
    DoctorCalendarDto getDoctorCalendarById(@Param("id")Long id);


}
