package com.sanatoryApp.AppointmentService.dto.Response;

import com.sanatoryApp.AppointmentService.entity.AppointmentStatus;
import com.sanatoryApp.AppointmentService.entity.AppointmentType;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailableAppointmentDto(
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        AppointmentType appointmentType,
        Long doctorCalendarId,
        AppointmentStatus appointmentStatus
) { }
