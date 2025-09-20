package com.sanatoryApp.AppointmentService.dto.Request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AppointmentTypeUpdateRequest(
        Long id,
        String name,
        String description,
        @Min(value = 15)
        int durationMin,
        @Min(value = 5)
        int bufferTimeMin,
        @Positive
        BigDecimal basePrice
) {
}
