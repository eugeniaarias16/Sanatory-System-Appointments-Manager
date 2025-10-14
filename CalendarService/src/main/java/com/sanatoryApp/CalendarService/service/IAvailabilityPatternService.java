package com.sanatoryApp.CalendarService.service;


import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.AvailabilityPatternResponseDto;

import java.time.DayOfWeek;
import java.util.List;

public interface IAvailabilityPatternService{
    //FIND METHODS
    AvailabilityPatternResponseDto findAvailabilityPatternById(Long id);
    List< AvailabilityPatternResponseDto> findAvailabilityPatternByDoctorCalendarId(Long id);
    List<AvailabilityPatternResponseDto> findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(Long calendarId, DayOfWeek dayOfWeek);
    List<AvailabilityPatternResponseDto>findByDoctorId(Long doctorId);
    List<AvailabilityPatternResponseDto>findByDoctorIdAndDay(Long doctorId,DayOfWeek dayOfWeek);


    AvailabilityPatternResponseDto createAvailabilityPattern(AvailabilityPatternCreateDto dto);
    AvailabilityPatternResponseDto updateAvailabilityPatternById(Long id, AvailabilityPatternUpdateDto dto);
    void deleteAvailabilityPatternById(Long id);
}
