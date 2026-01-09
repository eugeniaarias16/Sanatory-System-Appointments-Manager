package com.sanatoryApp.AppointmentService.dto.Request;

import com.sanatoryApp.AppointmentService.entity.AppointmentType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AppointmentTypeCreateDto(
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        @Min(value = 15, message = "Duration must be at least 15 minutes")
        int durationMin,

        @NotNull
        @Positive(message = "Buffer time cannot be negative")
        int bufferTimeMin,

        @NotNull
        @Positive
        @DecimalMin(value = "1.0", message = "Price must be at least 1.00")
        BigDecimal basePrice
) {
    public AppointmentType toEntity(){
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setName(name);
        appointmentType.setDescription(description);
        appointmentType.setDurationMin(durationMin);
        appointmentType.setBufferTimeMin(bufferTimeMin);
        appointmentType.setBasePrice(basePrice);
        appointmentType.setActive(true);
        return appointmentType;
    }
}
