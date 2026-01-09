package com.sanatoryApp.AppointmentService.dto.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AppointmentTypeUpdateDto(
        String name,

        String description,

        @Min(value = 15, message = "Duration must be at least 15 minutes")
        Integer durationMin,

        @Positive(message = "Buffer time cannot be negative")
        Integer bufferTimeMin,

        @DecimalMin(value = "1.0", message = "Price must be at least 1.00")
        BigDecimal basePrice
) {
}
