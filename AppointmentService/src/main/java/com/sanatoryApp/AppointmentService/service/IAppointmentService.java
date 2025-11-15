package com.sanatoryApp.AppointmentService.service;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentCreateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentCreateResponseDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentResponseDto;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService {

    AppointmentResponseDto findAppointmentById(Long id);
    AppointmentCreateResponseDto createAppointment(AppointmentCreateDto dto) throws ServiceUnavailableException;
    void cancelAppointmentById(Long id);
    void cancelAppointmentByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDate date);

    List<AppointmentResponseDto> findByPatientIdAndDate(Long patientId, LocalDateTime date);
    List<AppointmentResponseDto> findByPatientId(Long patientId);
    List<AppointmentResponseDto> findByPatientInsuranceId(Long insuranceId);

    List<AppointmentResponseDto> findByPatientIdAndDateBetween(
            Long patientId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<AppointmentResponseDto> findUpcomingAppointmentsByPatientId(
            Long patientId,
            LocalDateTime now
    );

    List<AppointmentResponseDto> findByDoctorId(Long doctorId);
    List<AppointmentResponseDto> findByDoctorIdAndDateBetween(
            Long doctorId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
    List<AppointmentResponseDto> findByDoctorIdAndDoctorCalendarId(Long doctorId, Long calendarId);

    List<AppointmentResponseDto> findTodayAppointmentsByDoctorId(
            Long doctorId,
            LocalDate today
    );

    AppointmentResponseDto findAppointmentByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDate date);

    boolean existsByPatientIdAndDoctorIdAndDate(
            Long patientId,
            Long doctorId,
            LocalDateTime date
    );

}

