package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarResponseDto;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IDoctorCalendarService {

    //BASIC CRUD
    DoctorCalendarResponseDto findDoctorCalendarById(Long id);
    DoctorCalendarResponseDto createDoctorCalendar(DoctorCalendarCreateDto dto);
    DoctorCalendarResponseDto updateDoctorCalendar(Long id,DoctorCalendarUpdateDto dto);
    void deleteDoctorCalendar(Long id);

    //OTHER METHODS
    List<DoctorCalendarResponseDto> findByDoctorIdAndIsActiveTrue(Long doctorId);
    List<DoctorCalendarResponseDto>findByDoctorId(Long doctorId);
    boolean existsByDoctorIdAndNameAndIsActiveTrue(Long doctorId, String name);
    boolean existsByDoctorIdAndNameExcludingId(Long doctorId, String name, Long excludeId);
    boolean existsById(Long calendarId);
}
