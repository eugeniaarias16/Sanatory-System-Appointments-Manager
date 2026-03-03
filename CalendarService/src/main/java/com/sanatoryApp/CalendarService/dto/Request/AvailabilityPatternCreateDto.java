package com.sanatoryApp.CalendarService.dto.Request;

import com.sanatoryApp.CalendarService.entity.AvailabilityPattern;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record AvailabilityPatternCreateDto(
        @NotNull(message = "Doctor calendar ID is mandatory")
        Long doctorCalendarId,

        @NotNull(message = "Day of week is mandatory")
        @Schema(example = "MONDAY")
        DayOfWeek dayOfWeek,

        @NotNull(message = "Start time is mandatory")
        @Schema(example = "08:00:00")
        LocalTime startTime,

        @NotNull(message = "End time is mandatory")
        @Schema(example = "17:00:00")
        LocalTime endTime
) {
    public AvailabilityPattern toEntity(DoctorCalendar doctorCalendar) {
        AvailabilityPattern pattern = new AvailabilityPattern();
        pattern.setDoctorCalendar(doctorCalendar);
        pattern.setDayOfWeek(this.dayOfWeek);
        pattern.setStartTime(this.startTime);
        pattern.setEndTime(this.endTime);
        return pattern;
    }
}
