package com.sanatoryApp.AppointmentService.dto.Response;

import com.sanatoryApp.AppointmentService.entity.Appointment;
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
        BigDecimal coveragePercentage,
        BigDecimal amountToPay,
        String notes,
        LocalDate createdAt
) {
    public static AppointmentResponseDto fromEntity(Appointment appointment){
        return new AppointmentResponseDto(
                appointment.getId(),
                appointment.getDoctorId(),
                appointment.getPatientId(),
                appointment.getAppointmentType().getName(),
                appointment.getPatientInsuranceId(),
                appointment.getDate(),
                appointment.getStatus(),
                appointment.getConsultationCost(),
                appointment.getCoveragePercentage(),
                appointment.getAmountToPay(),
                appointment.getNotes(),
                appointment.getCreatedAt()
        );

    };
}
