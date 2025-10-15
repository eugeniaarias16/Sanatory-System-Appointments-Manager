package com.sanatoryApp.CalendarService.dto.Response;

import com.sanatoryApp.CalendarService.entity.DoctorCalendar;

import java.util.List;

public record DoctorCalendarResponseDto(
      Long id,
      Long doctorId,
      String name,
      boolean isActive,
      String timeZone

) {
    public static DoctorCalendarResponseDto fromEntity(DoctorCalendar doctorCalendar){
        return new DoctorCalendarResponseDto(
                doctorCalendar.getId(),
                doctorCalendar.getDoctorId(),
                doctorCalendar.getName(),
                doctorCalendar.isActive(),
                doctorCalendar.getTimeZone().getId()
        );
    }
}
