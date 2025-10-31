package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarCreateResponseDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarResponseDto;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface IDoctorCalendarService {
    DoctorCalendar getDoctorCalendarEntityById(Long Id);
    DoctorCalendarResponseDto findDoctorCalendarById(Long id);
    DoctorCalendarCreateResponseDto createDoctorCalendar(DoctorCalendarCreateDto dto);
    DoctorCalendarResponseDto updateDoctorCalendar(Long id, DoctorCalendarUpdateDto dto);
    void deleteDoctorCalendar(Long id);

    List<DoctorCalendarResponseDto> findByDoctorIdAndIsActiveTrue(Long doctorId);
    List<DoctorCalendarResponseDto> findByDoctorId(Long doctorId);
    DoctorCalendarResponseDto findByDoctorIdAndName(Long doctorId, String name);
    boolean existsByDoctorIdAndNameAndIsActiveTrue(Long doctorId, String name);
    boolean existsByDoctorIdAndNameExcludingId(Long doctorId, String name, Long excludeId);
    boolean existsById(Long calendarId);


}