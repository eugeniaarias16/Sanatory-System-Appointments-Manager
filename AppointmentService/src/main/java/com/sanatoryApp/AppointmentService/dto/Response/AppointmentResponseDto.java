package com.sanatoryApp.AppointmentService.dto.Response;

import com.sanatoryApp.AppointmentService.entity.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AppointmentResponseDto(

        Long id,
        Long doctorId,
        Long patientId,
        String appointmentTypeName,
        Long patientInsuranceId,
        LocalDateTime date,
        AppointmentStatus status,
        BigDecimal consultationCost,
        double coveragePercentage,
        BigDecimal amountToPay,
        String notes,
        LocalDate createdAt
) { }
