package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.CalendarExceptionResponseDto;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ICalendarExceptionService {

    //Create CE
    CalendarExceptionResponseDto createCalendarException(CalendarExceptionCreateDto dto);

    //Update CE
    CalendarExceptionResponseDto updateCalendarException(Long id,CalendarExceptionUpdateDto dto);

    //Get by CE id
    CalendarExceptionResponseDto findByIdAndIsActive(Long id);


    //Get all CE by DoctorId
    List<CalendarExceptionResponseDto>findBydDoctorId(Long doctorId);

    //Get all CE globals
    List<CalendarExceptionResponseDto>findAllGlobalAndIsActive();

    //Get all CEs by calendarId
    List<CalendarExceptionResponseDto>findSemiGlobalByDoctorIdAndIsActive(Long doctorId);

    //Get all CEs by doctorId
    List<CalendarExceptionResponseDto>findSpecificByDoctorCalendarIdAndIsActive(Long doctorCalendarId);

    //Get all CEs by date and time by doctorId
    List<CalendarExceptionResponseDto>findByDoctorIdAndDateAndHour(Long doctorId, LocalDate date);


    //Get ALL existing CE Coincidences (can return multiple conflicts: GLOBAL + SEMI_GLOBAL + SPECIFIC)
    List<CalendarExceptionResponseDto> findExistingCalendarExceptionCoincidence(
            @Param("doctorId") Long doctorId,
            @Param("calendarId") Long calendarId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId
    );

    //Delete CE
    void deleteCalendarExceptionById(Long id);
}