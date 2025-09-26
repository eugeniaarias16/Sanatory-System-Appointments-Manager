package com.sanatoryApp.CalendarService.dto.Response;

public record DoctorCalendarResponseDto(
      Long id,
      Long doctorId,
      String name,
      boolean isActive,
      String timeZone
) {
}
