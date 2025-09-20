package com.sanatoryApp.AppointmentService.dto.Response;


import java.time.LocalDateTime;

public record AppointmentCancelResponseDto(
        Long id,
        Long doctorId,
        Long patientId,
        LocalDateTime date,
        String notes
) { }
