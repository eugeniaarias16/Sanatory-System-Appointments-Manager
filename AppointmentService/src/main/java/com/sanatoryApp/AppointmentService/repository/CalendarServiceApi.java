package com.sanatoryApp.AppointmentService.repository;

import com.sanatoryApp.AppointmentService.dto.Request.externalService.DoctorCalendarDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "calendar-service",url = "${calendar-service.url}")
public interface CalendarServiceApi {

    @GetMapping("/doctorCalendar/{id}")
    DoctorCalendarDto getDoctorCalendarById(@PathVariable @Param("id")Long id);


}
