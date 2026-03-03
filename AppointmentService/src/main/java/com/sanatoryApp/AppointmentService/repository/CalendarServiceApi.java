package com.sanatoryApp.AppointmentService.repository;

import com.sanatoryApp.AppointmentService.dto.externalService.AvailabilityPatternDto;
import com.sanatoryApp.AppointmentService.dto.externalService.CalendarExceptionDto;
import com.sanatoryApp.AppointmentService.dto.externalService.DoctorCalendarDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "calendar-service")
public interface CalendarServiceApi {

    //basic calendar information
    @GetMapping("/doctorCalendar/{id}")
    DoctorCalendarDto getDoctorCalendarById(@PathVariable @Param("id")Long id);


    //weekly availability patterns
    @GetMapping("/availabilityPattern/doctor/{doctorId}")
    List<AvailabilityPatternDto> getPatternsByDoctorId(@PathVariable Long doctorId);


    @GetMapping("/availabilityPattern/search/calendarAndDay")
    List<AvailabilityPatternDto> findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(
            @RequestParam Long calendarId,
            @RequestParam DayOfWeek dayOfWeek
    );

    //all doctor's CEs (any date)
    @GetMapping("/calendarException/doctor/{doctorId}")
    List<CalendarExceptionDto> getExceptionsByDoctorId(@PathVariable Long doctorId);

    //Doctor's EC on a specific date ← to validate appointment
    @GetMapping("/calendarException/doctor/{doctorId}/date/{date}")
    List<CalendarExceptionDto> getExceptionsByDoctorIdAndDate(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

}
