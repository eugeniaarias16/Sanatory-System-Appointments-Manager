package com.sanatoryApp.AppointmentService.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class AppointmentTypeCreateRequestDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private int durationMin;
    @NotNull
    private int bufferTimeMin;
    @NotNull
    @Positive
    private BigDecimal basePrice;
    private boolean isActive;

}
