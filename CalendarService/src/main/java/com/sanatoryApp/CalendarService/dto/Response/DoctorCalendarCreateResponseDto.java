package com.sanatoryApp.CalendarService.dto.Response;

import com.sanatoryApp.CalendarService.dto.Request.externalService.DoctorDto;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;

public record DoctorCalendarCreateResponseDto(
        Long id,
        String name,
        Long doctorId,
        String doctorFirstName,
        String doctorLastName,
        boolean isActive,
        String timeZone
) {

    public static DoctorCalendarCreateResponseDto fromEntities(
            DoctorCalendar doctorCalendar,
            DoctorDto dto){
        return new DoctorCalendarCreateResponseDto(
                doctorCalendar.getId(),
                doctorCalendar.getName(),
                dto.id(),
                dto.firstName(),
                dto.lastName(),
                doctorCalendar.isActive(),
                doctorCalendar.getTimeZone().getId()
        );
    }

}
