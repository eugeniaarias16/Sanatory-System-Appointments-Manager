package com.sanatoryApp.CalendarService.dto.Request;

import com.sanatoryApp.CalendarService.entity.ExceptionType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarExceptionUpdateDto(
        Long doctorCalendarId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        ExceptionType exceptionType,
        Boolean isGlobal,
        String reason
        ) { }
