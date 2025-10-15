package com.sanatoryApp.CalendarService.dto.Request;

import com.sanatoryApp.CalendarService.entity.CalendarException;
import com.sanatoryApp.CalendarService.entity.ExceptionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CalendarExceptionCreateDto {
        @NotNull
        private Long doctorCalendarId;
        @NotNull
        private LocalDate date;
        @NotNull
        private LocalTime startTime;
        @NotNull
        private LocalTime endTime;
        @NotNull
        private ExceptionType exceptionType;
        private String reason;
        @NotNull
        private boolean isGlobal;

        public CalendarException toEntity(){
                CalendarException calendarException=new CalendarException();
                calendarException.setDoctorCalendarId(doctorCalendarId);
                calendarException.setDate(date);
                calendarException.setStartTime(startTime);
                calendarException.setEndTime(endTime);
                calendarException.setExceptionType(exceptionType);
                calendarException.setReason(reason.toLowerCase());
                calendarException.setGlobal(isGlobal);

                return calendarException;
        }
}
