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
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanatoryApp.AppointmentService.exception.ServiceUnavailableException;
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

    /* METHODS CALLING EXTERNAL SERVICES */

    //METHODS CALLING USER-SERVICE

    //Doctor Information
    @CircuitBreaker(name = "user-service",fallbackMethod = "getDoctorByIdFallback")
    private DoctorDto getDoctorById(Long id) {
        log.debug("Calling User Service to get doctor with id: {}", id);
        try {
            return userServiceApi.getDoctorById(id);
        } catch (FeignException.NotFound e) {
            log.error("Doctor with id {} not found in User Service", id);
            throw new ResourceNotFound("Doctor with id: " + id + " not found.");
        }
    }

    private DoctorDto getDoctorByIdFallback(Long id, Exception e) throws ServiceUnavailableException {
        log.error("Circuit Breaker activated for getDoctorById. Doctor id: {}, Error: {}", id, e.getMessage());
       throw new ServiceUnavailableException("User Service is currently unavailable. Please try again later.");
    }


    //Patient information
    @CircuitBreaker(name = "user-service",fallbackMethod = "getPatientByDniFallback")
    private PatientDto getPatientByDni(String dni){
        log.debug("Calling User Service to get patient with dni: {}", dni);
        try {
            return userServiceApi.getPatientByDni(dni);
        } catch (FeignException.NotFound e) {
            log.error("Patient with dni {} not found in User Service", dni);
            throw new ResourceNotFound("Patient with dni: " + dni + " not found.");
        }
    }
    private PatientDto getPatientByDniFallback(String dni,Exception e) throws ServiceUnavailableException {
        log.error("Circuit Breaker activated for getPatientByDni. Patient dni: {}, Error: {}", dni, e.getMessage());
        throw new ServiceUnavailableException("User Service is currently unavailable. Please try again later.");
    }


    //METHODS CALLING HEALTH-INSURANCE-SERVICE
    //Patient Insurance Information
    @CircuitBreaker(name = "health-insurance-service",fallbackMethod = "getPatientInsuranceByCredentialNumberFallback")
    private PatientInsuranceDto getPatientInsuranceByCredentialNumber(String credentialNum){
        log.debug("Calling Health Insurance Service to get patient insurance with credential number: {}", credentialNum);
        try {
            return healthInsuranceServiceApi.getPatientInsuranceByCredentialNumber(credentialNum);
        } catch (FeignException.NotFound e) {
            log.error("Patient Insurance with credential number {} not found in Health Insurance Service", credentialNum);
            throw new ResourceNotFound("Patient Insurance with credential number: " + credentialNum + " not found.");
        }
    }

    private PatientInsuranceDto getPatientInsuranceByCredentialNumberFallback(String credentialNum, Exception e) throws ServiceUnavailableException {
        log.error("Circuit Breaker activated for getPatientInsuranceByCredentialNumber. Patient Insurance with credential number: {}, Error: {}", credentialNum, e.getMessage());
        throw new ServiceUnavailableException("Health Insurance Service is currently unavailable. Please try again later.");
    }

    //Coverage Plan Information
    @CircuitBreaker(name = "health-insurance-service",fallbackMethod = "getCoveragePlanByIdFallback")
    private CoveragePlanDto getCoveragePlanById(Long coveragePlanId){
        log.debug("Calling Health Insurance Service to get coverage plan with id: {}", coveragePlanId);
        try {
            return healthInsuranceServiceApi.getCoveragePlanById(coveragePlanId);
        } catch (FeignException.NotFound e) {
            log.error("Coverage Plan with id {} not found in Health Insurance Service",coveragePlanId);
            throw new ResourceNotFound("Coverage Plan with id: " +coveragePlanId + " not found.");
        }
    }

    private CoveragePlanDto getCoveragePlanByIdFallback(Long coveragePlanId,Exception e) throws ServiceUnavailableException {
        log.error("Circuit Breaker activated for getCoveragePlanById. Coverage Plan with id: {}, Error: {}", coveragePlanId, e.getMessage());
        throw new ServiceUnavailableException("Health Insurance Service is currently unavailable. Please try again later.");
    }

    //METHODS CALLING CALENDAR-SERVICE
    //Doctor Calendar Information
    @CircuitBreaker(name = "calendar-service",fallbackMethod = "getDoctorCalendarByIdFallback")
    private DoctorCalendarDto getDoctorCalendarById(Long calendarId){
        log.debug("Calling Calendar Service to get doctor calendar with id: {}", calendarId);
        try {
            return calendarServiceApi.getDoctorCalendarById(calendarId);
        } catch (FeignException.NotFound e) {
            log.error("Doctor Calendar with id {} not found in Calendar Service", calendarId);
            throw new ResourceNotFound("Doctor Calendar with id: " + calendarId + " not found.");
        }
    }

    private DoctorCalendarDto getDoctorCalendarByIdFallback(Long calendarId,Exception e) throws ServiceUnavailableException {
        log.error("Circuit Breaker activated for getDoctorCalendarById. Doctor Calendar with id: {}, Error: {}", calendarId, e.getMessage());
        throw new ServiceUnavailableException("Calendar Service is currently unavailable. Please try again later.");
    }



    @Override
    @Transactional
    public AppointmentCreateResponseDto createAppointment(AppointmentCreateDto dto) throws ServiceUnavailableException {

        log.debug("Attempting to create a new Appointment");

        DoctorDto doctorDto = getDoctorById(dto.getDoctorId());
        PatientDto patientDto = getPatientByDni(dto.getPatientDni());
        PatientInsuranceDto patientInsuranceDto = getPatientInsuranceByCredentialNumber(dto.getCredentialNumber());
        CoveragePlanDto coveragePlanDto = getCoveragePlanById(patientInsuranceDto.coveragePlanId());
        DoctorCalendarDto doctorCalendarDto = getDoctorCalendarById(dto.getDoctorCalendarId());


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

        LocalDateTime date = dto.getDate().atStartOfDay();
        appointment.setDate(date);

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
    public List<AppointmentResponseDto> findByPatientIdAndDate(Long patientId, LocalDate date) {

        LocalDateTime start = date.atStartOfDay();

        List<Appointment> appointmentList = appointmentRepository.findByPatientIdAndDate(patientId, start);
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
    public List<AppointmentResponseDto> findByPatientDni(String dni) {
        PatientDto patientDto=getPatientByDni(dni);
        List<Appointment>appointmentList=appointmentRepository.findByPatientId(patientDto.id());
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
    public List<AppointmentResponseDto> findByPatientIdAndDateBetween(Long patientId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        List<Appointment> appointmentList = appointmentRepository.findByPatientIdAndDateBetween(patientId, start, end);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findByPatientDniAndDateBetween(String patientDni, LocalDate startDate, LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();

        PatientDto patientDto=getPatientByDni(patientDni);
        return findByPatientIdAndDateBetween(patientDto.id(), startDate,endDate);
    }

    @Override
    public List<AppointmentResponseDto> findUpcomingAppointmentsByPatientId(Long patientId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        List<Appointment> appointmentList = appointmentRepository.findUpcomingAppointmentsByPatientId(patientId, start);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findUpcomingAppointmentsByPatientId(Long patientId) {

        LocalDateTime date=LocalDateTime.now();
        List<Appointment> appointmentList = appointmentRepository.findUpcomingAppointmentsByPatientId(patientId,date);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findUpcomingAppointmentsByPatientDni(String patientDni, LocalDate date) {

        PatientDto patientDto=getPatientByDni(patientDni);
        return findUpcomingAppointmentsByPatientId(patientDto.id(), date);
    }

    @Override
    public List<AppointmentResponseDto> findUpcomingAppointmentsByPatientDni(String patientDni) {
        PatientDto patientDto=getPatientByDni(patientDni);
        return findUpcomingAppointmentsByPatientId(patientDto.id(), LocalDate.now());
    }

    @Override
    public List<AppointmentResponseDto> findByDoctorId(Long doctorId) {
        List<Appointment> appointmentList = appointmentRepository.findByDoctorId(doctorId);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findByDoctorIdAndDateBetween(Long doctorId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        List<Appointment> appointmentList = appointmentRepository.findByDoctorIdAndDateBetween(doctorId, start, end);
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
    public List<AppointmentResponseDto> findTodayAppointmentsByDoctorId(Long doctorId) {
        LocalDate today=LocalDate.now();
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
    public boolean existsByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();

        return appointmentRepository.existsByPatientIdAndDoctorIdAndDate(patientId, doctorId,start);
    }

    private BigDecimal calculateAmountToPay(BigDecimal cost, BigDecimal coverPercentage) {
        BigDecimal coverPer = coverPercentage.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal coverAmount = cost.multiply(coverPer);
        BigDecimal amountToPay = cost.subtract(coverAmount);
        return amountToPay.setScale(2, RoundingMode.HALF_UP);
    }
}
