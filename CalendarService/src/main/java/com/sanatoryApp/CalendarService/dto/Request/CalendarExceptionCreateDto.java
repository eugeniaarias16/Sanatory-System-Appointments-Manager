package com.sanatoryApp.CalendarService.dto.Request;

import com.sanatoryApp.CalendarService.entity.CalendarException;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import com.sanatoryApp.CalendarService.entity.ExceptionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CalendarExceptionCreateDto {

        @NotNull(message = "Doctor Calendar ID is required")
        private Long doctorCalendarId;

        @NotNull(message = "Date is required")
        private LocalDate date;

        private LocalTime startTime;


        private LocalTime endTime;

        @NotNull(message = "Exception type is required")
        private ExceptionType exceptionType;

        private String reason;

        @NotNull(message = "isGlobal flag is required")
        private boolean isGlobal;

        public CalendarException toEntity(DoctorCalendar doctorCalendar) {
                CalendarException calendarException = new CalendarException();
                calendarException.setDoctorCalendar(doctorCalendar);
                calendarException.setDate(date);
                calendarException.setStartTime(startTime);
                calendarException.setEndTime(endTime);
                calendarException.setExceptionType(exceptionType);

                if (reason != null && !reason.trim().isEmpty()) {
                        calendarException.setReason(reason.trim().toLowerCase());
                }

                calendarException.setGlobal(isGlobal);
                return calendarException;
        }
}