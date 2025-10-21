package com.sanatoryApp.AppointmentService.dto.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AppointmentTypeUpdateDto {


        private String name;

        private String description;

        @Min(value = 15, message = "Duration must be at least 15 minutes")
        private Integer durationMin;  // Integer (wrapper) permite null

        @Min(value = 0, message = "Buffer time cannot be negative")
        private Integer bufferTimeMin;

        @DecimalMin(value = "1.0", message = "Price must be at least 1.00")
        private BigDecimal basePrice;
}
