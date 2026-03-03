package com.sanatoryApp.CalendarService.dto.Response;

import com.sanatoryApp.CalendarService.entity.CalendarException;
import com.sanatoryApp.CalendarService.entity.ExceptionScope;
import com.sanatoryApp.CalendarService.entity.ExceptionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record CalendarExceptionResponseDto(
        Long id,

        @Schema(description = "Scope of the exception: GLOBAL, SEMI_GLOBAL, or SPECIFIC")
        ExceptionScope scope,

        @Schema(description = "Doctor ID (null for GLOBAL, required for SEMI_GLOBAL and SPECIFIC)")
        Long doctorId,

        @Schema(description = "Calendar ID (null for GLOBAL and SEMI_GLOBAL, required for SPECIFIC)")
        Long doctorCalendarId,

        @Schema(description = "Calendar name (null for GLOBAL and SEMI_GLOBAL)")
        String doctorCalendarName,

        LocalDate startDate,

        @Schema(description = "End date (null for single-day exception)")
        LocalDate endDate,

        LocalTime startTime,

        LocalTime endTime,

        ExceptionType exceptionType,

        @Size(max = 200, message = "Reason cannot exceed 200 characters")
        String reason,

        boolean isActive,

        boolean isFullDay,

        boolean isSingleDay

) {
        public static CalendarExceptionResponseDto fromEntity(CalendarException calendarException){
                return new CalendarExceptionResponseDto(
                        calendarException.getId(),
                        calendarException.getScope(),
                        calendarException.getDoctorId(),
                        calendarException.getDoctorCalendar() != null ? calendarException.getDoctorCalendar().getId() : null,
                        calendarException.getDoctorCalendar() != null ? calendarException.getDoctorCalendar().getName() : null,
                        calendarException.getStartDate(),
                        calendarException.getEndDate(),
                        calendarException.getStartTime(),
                        calendarException.getEndTime(),
                        calendarException.getExceptionType(),
                        calendarException.getReason(),
                        calendarException.isActive(),
                        calendarException.isFullDay(),
                        calendarException.isSingleDay()
                );
        }
}
