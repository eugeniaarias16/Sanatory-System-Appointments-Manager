package com.sanatoryApp.CalendarService.dto.Request;

import com.sanatoryApp.CalendarService.entity.AvailabilityPattern;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
public class AvailabilityPatternCreateDto {

        @NotNull(message = "Doctor calendar ID is mandatory")
        private Long doctorCalendarId;

        @NotNull(message = "Day of week is mandatory")
        private DayOfWeek dayOfWeek;

        @NotNull(message = "Start time is mandatory")
        private LocalTime startTime;

        @NotNull(message = "End time is mandatory")
        private LocalTime endTime;

        public AvailabilityPattern toEntity(DoctorCalendar doctorCalendar) {
                AvailabilityPattern pattern = new AvailabilityPattern();
                pattern.setDoctorCalendar(doctorCalendar);
                pattern.setDayOfWeek(this.dayOfWeek);
                pattern.setStartTime(this.startTime);
                pattern.setEndTime(this.endTime);
                return pattern;
        }
}