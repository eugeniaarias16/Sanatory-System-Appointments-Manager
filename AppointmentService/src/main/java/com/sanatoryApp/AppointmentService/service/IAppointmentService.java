package com.sanatoryApp.AppointmentService.service;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentCreateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentCreateResponseDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentResponseDto;
import com.sanatoryApp.AppointmentService.exception.ServiceUnavailableException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface IAppointmentService {


    AppointmentResponseDto findAppointmentById(Long id);

    List<AppointmentResponseDto> findByPatientDni(String dni);

    List<AppointmentResponseDto> findByPatientId(Long patientId);

    List<AppointmentResponseDto> findUpcomingAppointmentsByPatientDni(String patientDni);

    List<AppointmentResponseDto> findUpcomingAppointmentsByPatientDni(
            String patientDni,
            LocalDate date
    );
    List<AppointmentResponseDto> findUpcomingAppointmentsByPatientId(Long patientId);

    List<AppointmentResponseDto> findUpcomingAppointmentsByPatientId(
            Long patientId,
            LocalDate date
    );

    List<AppointmentResponseDto> findByPatientDniAndDateBetween(
            String patientDni,
            LocalDate startDate,
            LocalDate endDate
    );

    /***** Get Appointments by Doctor's Information *****/

    List<AppointmentResponseDto> findByDoctorId(Long doctorId);
    List<AppointmentResponseDto> findByDoctorIdAndDateBetween(
            Long doctorId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<AppointmentResponseDto> findByDoctorCalendarId(Long doctorCalendarId);


    List<AppointmentResponseDto> findTodayAppointmentsByDoctorId(Long doctorId);

    AppointmentResponseDto findAppointmentByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDate date);

    /***** Other methods *****/
    void cancelAppointmentById(Long id);

    AppointmentCreateResponseDto createAppointment(AppointmentCreateDto dto) throws ServiceUnavailableException;

    boolean existsByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDate date, LocalTime time);
}

