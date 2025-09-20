package com.sanatoryApp.AppointmentService.dto.Response;


import java.math.BigDecimal;

public record AppointmentTypeResponseDto(
        String name,
        String description,
        int durationMin,
        int bufferTimeMin,
        BigDecimal basePrice,
        boolean isActive
) { }
