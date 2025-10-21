package com.sanatoryApp.AppointmentService.dto.Response;


import com.sanatoryApp.AppointmentService.entity.AppointmentType;

import java.math.BigDecimal;

public record AppointmentTypeResponseDto(
        String name,
        String description,
        int durationMin,
        int bufferTimeMin,
        BigDecimal basePrice,
        boolean isActive
) {
    public static AppointmentTypeResponseDto fromEntity(AppointmentType appointmentType){
        return new AppointmentTypeResponseDto(
                appointmentType.getName(),
                appointmentType.getDescription(),
                appointmentType.getDurationMin(),
                appointmentType.getBufferTimeMin(),
                appointmentType.getBasePrice(),
                appointmentType.isActive()
        );
    }
}
