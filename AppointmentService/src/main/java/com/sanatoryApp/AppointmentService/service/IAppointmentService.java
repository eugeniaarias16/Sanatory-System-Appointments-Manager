package com.sanatoryApp.AppointmentService.service;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentCreateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentCreateResponseDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentResponseDto;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDate;
import java.util.List;

public interface IAppointmentService {

    AppointmentResponseDto findAppointmentById(Long id);
    AppointmentCreateResponseDto createAppointment(AppointmentCreateDto dto) throws ServiceUnavailableException;
    void cancelAppointmentById(Long id);
    void cancelAppointmentByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDate date);

    List<AppointmentResponseDto> findByPatientIdAndDate(Long patientId, LocalDate date);
    List<AppointmentResponseDto> findByPatientId(Long patientId);
    List<AppointmentResponseDto>findByPatientDni(String dni);
    List<AppointmentResponseDto> findByPatientInsuranceId(Long insuranceId);

    List<AppointmentResponseDto> findByPatientIdAndDateBetween(
            Long patientId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<AppointmentResponseDto> findByPatientDniAndDateBetween(
            String patientDni,
            LocalDate startDate,
            LocalDate endDate
    );

    List<AppointmentResponseDto> findUpcomingAppointmentsByPatientId(
            Long patientId,
            LocalDate date
    );
    List<AppointmentResponseDto> findUpcomingAppointmentsByPatientId(Long patientId);
    List<AppointmentResponseDto> findUpcomingAppointmentsByPatientDni(
            String patientDni,
            LocalDate date
    );

    List<AppointmentResponseDto> findUpcomingAppointmentsByPatientDni(String patientDni);

    List<AppointmentResponseDto> findByDoctorId(Long doctorId);
    List<AppointmentResponseDto> findByDoctorIdAndDateBetween(
            Long doctorId,
            LocalDate startDate,
            LocalDate endDate
    );
    List<AppointmentResponseDto> findByDoctorIdAndDoctorCalendarId(Long doctorId, Long calendarId);

    List<AppointmentResponseDto> findTodayAppointmentsByDoctorId(Long doctorId);

    AppointmentResponseDto findAppointmentByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDate date);

    boolean existsByPatientIdAndDoctorIdAndDate(
            Long patientId,
            Long doctorId,
            LocalDate date
    );

}

