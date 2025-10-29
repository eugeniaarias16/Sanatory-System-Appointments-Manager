package com.sanatoryApp.AppointmentService.dto.Response;

import com.sanatoryApp.AppointmentService.dto.Request.externalService.DoctorDto;
import com.sanatoryApp.AppointmentService.dto.Request.externalService.PatientDto;
import com.sanatoryApp.AppointmentService.entity.Appointment;
import com.sanatoryApp.AppointmentService.entity.AppointmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record AppointmentCreateResponseDto(
        Long id,
        Long doctorId,
        String doctorFirstName,
        String doctorLastName,
        Long patientId,
        String patientFirstName,
        String patientLastName,
        String patientDni,
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
    public static AppointmentCreateResponseDto fromEntities(DoctorDto doctorDto, PatientDto patientDto, Appointment appointment){
        return new AppointmentCreateResponseDto(
                appointment.getId(),
                appointment.getDoctorId(),
                doctorDto.firstName(),
                doctorDto.lastName(),
                appointment.getPatientId(),
                patientDto.firstName(),
                patientDto.lastName(),
                patientDto.dni(),
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
    }
}
