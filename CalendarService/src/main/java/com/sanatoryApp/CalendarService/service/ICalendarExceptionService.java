package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.CalendarExceptionResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ICalendarExceptionService {

    CalendarExceptionResponseDto findById(Long id);
    CalendarExceptionResponseDto createCalendarException(CalendarExceptionCreateDto dto);
    CalendarExceptionResponseDto updateCalendarException(Long id, CalendarExceptionUpdateDto dto);
    void deleteCalendarException(Long id);

    List<CalendarExceptionResponseDto> findByDoctorCalendarId(Long doctorCalendarId);
    List<CalendarExceptionResponseDto> findApplicableExceptionsInTimeRange(Long calendarId, Long doctorId, LocalDate startTime, LocalDate endTime);
    List<CalendarExceptionResponseDto> findApplicableExceptionsForCalendar(Long calendarId, Long doctorId, LocalDate date);
    List<CalendarExceptionResponseDto> findGlobalExceptionsByDoctorId(Long doctorId);
    List<CalendarExceptionResponseDto> findFutureExceptions(Long calendarId, LocalDate currentDate);
}