package com.sanatoryApp.AppointmentService.service;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentCreateDto;
import com.sanatoryApp.AppointmentService.dto.Request.externalService.*;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentCreateResponseDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentResponseDto;
import com.sanatoryApp.AppointmentService.entity.Appointment;
import com.sanatoryApp.AppointmentService.entity.AppointmentStatus;
import com.sanatoryApp.AppointmentService.entity.AppointmentType;
import com.sanatoryApp.AppointmentService.exception.BadRequest;
import com.sanatoryApp.AppointmentService.exception.ResourceNotFound;
import com.sanatoryApp.AppointmentService.repository.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {

    private final IAppointmentRepository appointmentRepository;
    private final IAppointmentTypeRepository appointmentTypeRepository;
    private final UserServiceApi userServiceApi;
    private final HealthInsuranceServiceApi healthInsuranceServiceApi;
    private final CalendarServiceApi calendarServiceApi;




    @Override
    public AppointmentResponseDto findAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Appointment not found with id " + id));
        return AppointmentResponseDto.fromEntity(appointment);
    }

    @Override
    @Transactional
    public AppointmentCreateResponseDto createAppointment(AppointmentCreateDto dto) throws ServiceUnavailableException {

        log.debug("Attempting to create a new Appointment");

        DoctorDto doctorDto;
        try {
            doctorDto = userServiceApi.getDoctorById(dto.getDoctorId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFound("Doctor with id: " + dto.getDoctorId() + " not found.");
        } catch (FeignException.ServiceUnavailable e) {
            throw new ServiceUnavailableException("User Service is down");
        } catch (FeignException e) {
            throw new RuntimeException("Error calling User Service: " + e.getMessage());
        }

        PatientDto patientDto;
        try {
            patientDto = userServiceApi.getPatientByDni(dto.getPatientDni());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFound("Patient with dni: " + dto.getPatientDni() + " not found.");
        } catch (FeignException.ServiceUnavailable e) {
            throw new ServiceUnavailableException("User Service is down");
        } catch (FeignException e) {
            throw new RuntimeException("Error calling User Service: " + e.getMessage());
        }

        PatientInsuranceDto patientInsuranceDto;
        try {
            patientInsuranceDto = healthInsuranceServiceApi.getPatientInsuranceByCredentialNumber(dto.getCredentialNumber());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFound("Patient Insurance with credential number : " + dto.getCredentialNumber() + " not found.");
        } catch (FeignException.ServiceUnavailable e) {
            throw new ServiceUnavailableException("Health Insurance Service is down");
        } catch (FeignException e) {
            throw new RuntimeException("Error calling Health Insurance Service: " + e.getMessage());
        }

        CoveragePlanDto coveragePlanDto = healthInsuranceServiceApi.getCoveragePlanById(patientInsuranceDto.coveragePlanId());

        DoctorCalendarDto doctorCalendarDto;
        try {
            doctorCalendarDto = calendarServiceApi.getDoctorCalendarById(dto.getDoctorCalendarId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFound("Doctor Calendar with id: " + dto.getDoctorCalendarId() + " not found.");
        } catch (FeignException.ServiceUnavailable e) {
            throw new ServiceUnavailableException("Calendar Service is down");
        } catch (FeignException e) {
            throw new RuntimeException("Error calling Calendar Service: " + e.getMessage());
        }

        AppointmentType appointmentType = appointmentTypeRepository.findById(dto.getAppointmentTypeId())
                .orElseThrow(() -> new ResourceNotFound("Appointment Type not found with id: " + dto.getAppointmentTypeId()));

        if(existsByPatientIdAndDoctorIdAndDate(patientDto.id(),doctorDto.id(),dto.getDate())){
            throw new BadRequest("Appointment already exists with doctor"+doctorDto.firstName()+" "+doctorDto.lastName()+" on day "+dto.getDate());
        }
        Appointment appointment = new Appointment();
        appointment.setDoctorId(doctorDto.id());
        appointment.setDoctorCalendarId(doctorCalendarDto.id());
        appointment.setPatientId(patientDto.id());
        appointment.setAppointmentType(appointmentType);
        appointment.setPatientInsuranceId(patientInsuranceDto.id());
        appointment.setConsultationCost(appointmentType.getBasePrice());
        appointment.setCoveragePercentage(coveragePlanDto.coverageValuePercentage());

        BigDecimal amountToPay = calculateAmountToPay(appointment.getConsultationCost(), appointment.getCoveragePercentage());
        appointment.setAmountToPay(amountToPay);

        appointment.setDate(dto.getDate());

        if (dto.getNotes() != null && !dto.getNotes().isEmpty()) {
            appointment.setNotes(dto.getNotes());
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment with id {} successfully created on date {} with doctor id {} and patient id {}", savedAppointment.getId(), savedAppointment.getDate(), savedAppointment.getDoctorId(), savedAppointment.getPatientId());
        return AppointmentCreateResponseDto.fromEntities(doctorDto, patientDto, savedAppointment);
    }

    @Override
    @Transactional
    public void cancelAppointmentById(Long id) {
        log.debug("Attempting to cancel appointment with id {}", id);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Appointment not found with id " + id));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        log.info("Appointment successfully cancelled.");
    }

    @Override
    @Transactional
    public void cancelAppointmentByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDate date) {
        Appointment appointment = appointmentRepository.findAppointmentByPatientIdAndDoctorIdAndDate(patientId, doctorId, date)
                .orElseThrow(() -> new ResourceNotFound("Appointment not found with patient id: " + patientId + ", doctor id: " + doctorId + " and date: " + date));
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        log.info("Appointment successfully cancelled with patient id: {}, doctor id: {} and date: {}.", patientId, doctorId, date);
    }

    @Override
    public List<AppointmentResponseDto> findByPatientIdAndDate(Long patientId, LocalDateTime date) {
        List<Appointment> appointmentList = appointmentRepository.findByPatientIdAndDate(patientId, date);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findByPatientId(Long patientId) {
        List<Appointment> appointmentList = appointmentRepository.findByPatientId(patientId);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findByPatientInsuranceId(Long insuranceId) {
        List<Appointment> appointmentList = appointmentRepository.findByPatientInsuranceId(insuranceId);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findByPatientIdAndDateBetween(Long patientId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Appointment> appointmentList = appointmentRepository.findByPatientIdAndDateBetween(patientId, startDate, endDate);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findUpcomingAppointmentsByPatientId(Long patientId, LocalDateTime now) {
        List<Appointment> appointmentList = appointmentRepository.findUpcomingAppointmentsByPatientId(patientId, now);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findByDoctorId(Long doctorId) {
        List<Appointment> appointmentList = appointmentRepository.findByDoctorId(doctorId);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findByDoctorIdAndDateBetween(Long doctorId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Appointment> appointmentList = appointmentRepository.findByDoctorIdAndDateBetween(doctorId, startDate, endDate);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findByDoctorIdAndDoctorCalendarId(Long doctorId, Long calendarId) {
        List<Appointment> appointmentList = appointmentRepository.findByDoctorIdAndDoctorCalendarId(doctorId, calendarId);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findTodayAppointmentsByDoctorId(Long doctorId, LocalDate today) {
        List<Appointment> appointmentList = appointmentRepository.findTodayAppointmentsByDoctorId(doctorId, today);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public AppointmentResponseDto findAppointmentByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDate date) {
        Appointment appointment = appointmentRepository.findAppointmentByPatientIdAndDoctorIdAndDate(patientId, doctorId, date)
                .orElseThrow(() -> new ResourceNotFound("Appointment not found with patient id: " + patientId + ", doctor id: " + doctorId + " and date: " + date));
        return AppointmentResponseDto.fromEntity(appointment);
    }

    @Override
    public boolean existsByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDateTime date) {
        return appointmentRepository.existsByPatientIdAndDoctorIdAndDate(patientId, doctorId, date);
    }

    private BigDecimal calculateAmountToPay(BigDecimal cost, BigDecimal coverPercentage) {
        BigDecimal coverPer = coverPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal coverAmount = cost.multiply(coverPer);
        BigDecimal amountToPay = cost.subtract(coverAmount);
        return amountToPay.setScale(2, RoundingMode.HALF_UP);
    }
}
