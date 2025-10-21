package com.sanatoryApp.AppointmentService.service;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentCreateDto;
import com.sanatoryApp.AppointmentService.dto.Request.AppointmentUpdateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentResponseDto;
import com.sanatoryApp.AppointmentService.entity.Appointment;

import java.util.Date;
import java.util.List;

public interface IAppointmentService {

    AppointmentResponseDto findAppointmentById(Long id);
    AppointmentResponseDto createAppointment(AppointmentCreateDto dto);
    AppointmentResponseDto updateAppointment(Long id, AppointmentUpdateDto dto);
    void cancelledAppointmentById(Long id);
    void cancelledAppointmentByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, Date date);

    List<AppointmentResponseDto> findByPatientId(Long patientId);
    List<AppointmentResponseDto>findByPatientInsuranceId(Long insuranceId);
    List<AppointmentResponseDto> findByPatientIdAndDate(Long patientId, Date date);
}
