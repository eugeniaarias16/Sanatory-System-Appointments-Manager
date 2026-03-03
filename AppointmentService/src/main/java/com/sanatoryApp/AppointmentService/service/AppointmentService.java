package com.sanatoryApp.AppointmentService.service;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentCreateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentCreateResponseDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentResponseDto;
import com.sanatoryApp.AppointmentService.dto.Response.AvailableAppointmentDto;
import com.sanatoryApp.AppointmentService.dto.externalService.*;
import com.sanatoryApp.AppointmentService.entity.Appointment;
import com.sanatoryApp.AppointmentService.entity.AppointmentStatus;
import com.sanatoryApp.AppointmentService.entity.AppointmentType;
import com.sanatoryApp.AppointmentService.exception.BadRequest;
import com.sanatoryApp.AppointmentService.exception.ResourceNotFound;
import com.sanatoryApp.AppointmentService.exception.ServiceUnavailableException;
import com.sanatoryApp.AppointmentService.repository.*;
import com.sanatoryApp.AppointmentService.utils.CalculateAmountToPay;
import com.sanatoryApp.AppointmentService.utils.CountWorkingDay;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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


    /* METHODS CALLING EXTERNAL SERVICES */

    @CircuitBreaker(name = "user-service", fallbackMethod = "getDoctorByIdFallback")
    private DoctorDto getDoctorById(Long id) {
        log.debug("Calling User Service to get doctor with id: {}", id);
        try {
            return userServiceApi.getDoctorById(id);
        } catch (FeignException.NotFound e) {
            log.error("Doctor with id {} not found in User Service", id);
            throw new ResourceNotFound("Doctor with id: " + id + " not found.");
        }
    }

    private DoctorDto getDoctorByIdFallback(Long id, Exception e) {
        log.error("Circuit Breaker activated for getDoctorById. Doctor id: {}, Error: {}", id, e.getMessage());
        throw new ServiceUnavailableException("User Service is currently unavailable. Please try again later.");
    }

    @CircuitBreaker(name = "user-service", fallbackMethod = "getPatientByDniFallback")
    private PatientDto getPatientByDni(String dni) {
        log.debug("Calling User Service to get patient with dni: {}", dni);
        try {
            return userServiceApi.getPatientByDni(dni);
        } catch (FeignException.NotFound e) {
            log.error("Patient with dni {} not found in User Service", dni);
            throw new ResourceNotFound("Patient with dni: " + dni + " not found.");
        }
    }

    private PatientDto getPatientByDniFallback(String dni, Exception e) {
        log.error("Circuit Breaker activated for getPatientByDni. Patient dni: {}, Error: {}", dni, e.getMessage());
        throw new ServiceUnavailableException("User Service is currently unavailable. Please try again later.");
    }

    @CircuitBreaker(name = "health-insurance-service", fallbackMethod = "getPatientInsuranceByCredentialNumberFallback")
    private PatientInsuranceDto getPatientInsuranceByCredentialNumber(String credentialNum) {
        log.debug("Calling Health Insurance Service to get patient insurance with credential number: {}", credentialNum);
        try {
            return healthInsuranceServiceApi.getPatientInsuranceByCredentialNumber(credentialNum);
        } catch (FeignException.NotFound e) {
            log.error("Patient Insurance with credential number {} not found in Health Insurance Service", credentialNum);
            throw new ResourceNotFound("Patient Insurance with credential number: " + credentialNum + " not found.");
        }
    }

    private PatientInsuranceDto getPatientInsuranceByCredentialNumberFallback(String credentialNum, Exception e) {
        log.error("Circuit Breaker activated for getPatientInsuranceByCredentialNumber. Credential: {}, Error: {}", credentialNum, e.getMessage());
        throw new ServiceUnavailableException("Health Insurance Service is currently unavailable. Please try again later.");
    }

    @CircuitBreaker(name = "health-insurance-service", fallbackMethod = "getCoveragePlanByIdFallback")
    private CoveragePlanDto getCoveragePlanById(Long coveragePlanId) {
        log.debug("Calling Health Insurance Service to get coverage plan with id: {}", coveragePlanId);
        try {
            return healthInsuranceServiceApi.getCoveragePlanById(coveragePlanId);
        } catch (FeignException.NotFound e) {
            log.error("Coverage Plan with id {} not found in Health Insurance Service", coveragePlanId);
            throw new ResourceNotFound("Coverage Plan with id: " + coveragePlanId + " not found.");
        }
    }

    private CoveragePlanDto getCoveragePlanByIdFallback(Long coveragePlanId, Exception e) {
        log.error("Circuit Breaker activated for getCoveragePlanById. Coverage Plan id: {}, Error: {}", coveragePlanId, e.getMessage());
        throw new ServiceUnavailableException("Health Insurance Service is currently unavailable. Please try again later.");
    }

    @CircuitBreaker(name = "calendar-service", fallbackMethod = "getDoctorCalendarByIdFallback")
    private DoctorCalendarDto getDoctorCalendarById(Long calendarId) {
        log.debug("Calling Calendar Service to get doctor calendar with id: {}", calendarId);
        try {
            return calendarServiceApi.getDoctorCalendarById(calendarId);
        } catch (FeignException.NotFound e) {
            log.error("Doctor Calendar with id {} not found in Calendar Service", calendarId);
            throw new ResourceNotFound("Doctor Calendar with id: " + calendarId + " not found.");
        }
    }

    private DoctorCalendarDto getDoctorCalendarByIdFallback(Long calendarId, Exception e) {
        log.error("Circuit Breaker activated for getDoctorCalendarById. Calendar id: {}, Error: {}", calendarId, e.getMessage());
        throw new ServiceUnavailableException("Calendar Service is currently unavailable. Please try again later.");
    }

    /*------------------ GET METHODS ----------------------*/

    @Override
    public AppointmentResponseDto findAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Appointment not found with id " + id));
        return AppointmentResponseDto.fromEntity(appointment);
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
        PatientDto patientDto = getPatientByDni(dni);
        List<Appointment> appointmentList = appointmentRepository.findByPatientId(patientDto.id());
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findUpcomingAppointmentsByPatientId(Long patientId) {
        LocalDateTime date = LocalDateTime.now();
        List<Appointment> appointmentList = appointmentRepository.findUpcomingAppointmentsByPatientId(patientId, date);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
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
    public List<AppointmentResponseDto> findUpcomingAppointmentsByPatientDni(String patientDni) {
        PatientDto patientDto = getPatientByDni(patientDni);
        return findUpcomingAppointmentsByPatientId(patientDto.id(), LocalDate.now());
    }

    @Override
    public List<AppointmentResponseDto> findUpcomingAppointmentsByPatientDni(String patientDni, LocalDate date) {
        PatientDto patientDto = getPatientByDni(patientDni);
        return findUpcomingAppointmentsByPatientId(patientDto.id(), date);
    }

    @Override
    public List<AppointmentResponseDto> findByPatientDniAndDateBetween(String patientDni, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        PatientDto patientDto = getPatientByDni(patientDni);
        List<Appointment> appointmentList = appointmentRepository.findByPatientIdAndDateBetween(patientDto.id(), start, end);
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
    public List<AppointmentResponseDto> findByDoctorIdAndDateBetween(Long doctorId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();
        List<Appointment> appointmentList = appointmentRepository.findByDoctorIdAndDateBetween(doctorId, start, end);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findByDoctorCalendarId(Long doctorCalendarId) {
        List<Appointment> appointmentList = appointmentRepository.findByDoctorCalendarId(doctorCalendarId);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentResponseDto> findTodayAppointmentsByDoctorId(Long doctorId) {
        LocalDate today = LocalDate.now();
        List<Appointment> appointmentList = appointmentRepository.findTodayAppointmentsByDoctorId(doctorId, today);
        return appointmentList.stream()
                .map(AppointmentResponseDto::fromEntity)
                .toList();
    }

    @Override
    public AppointmentResponseDto findAppointmentByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDate date) {
        Appointment appointment = appointmentRepository.findAppointmentByPatientIdAndDoctorIdAndDate(patientId, doctorId, date)
                .orElseThrow(() -> new ResourceNotFound("Appointment not found for patient id: " + patientId
                        + ", doctor id: " + doctorId + " on date: " + date));
        return AppointmentResponseDto.fromEntity(appointment);
    }

    /*------------------ CREATE / CANCEL METHODS ----------------------*/

    @Override
    @Transactional
    public AppointmentCreateResponseDto createAppointment(AppointmentCreateDto dto) throws ServiceUnavailableException {
        log.debug("Attempting to create a new Appointment");

        // 1) Validate weekend
        if (dto.date().getDayOfWeek() == DayOfWeek.SATURDAY || dto.date().getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Appointments cannot be scheduled on weekends.");
        }

        // 2) Build appointment datetime (date + time)
        LocalDateTime appointmentDateTime = LocalDateTime.of(dto.date(), dto.time());

        // 3) Get patient early (needed for duplicate check — only 1 external call before failing fast)
        PatientDto patientDto = getPatientByDni(dto.patientDni());

        // 4) Fail fast: duplicate check (same patient + same doctor + exact slot)
        if (existsByPatientIdAndDoctorIdAndDate(patientDto.id(), dto.doctorId(), dto.date(), dto.time())) {
            throw new BadRequest("Patient already has an appointment with Doctor id: " + dto.doctorId()
                    + " on " + dto.date() + " at " + dto.time() + ".");
        }

        // 5) Get AppointmentType
        AppointmentType appointmentType = appointmentTypeRepository.findByIdAndActive(dto.appointmentTypeId())
                .orElseThrow(() -> new ResourceNotFound("Appointment Type not found with id: " + dto.appointmentTypeId()));

        int slotTotalMin = appointmentType.getDurationMin() + appointmentType.getBufferTimeMin();
        LocalTime requestedStart = dto.time();
        LocalTime requestedEnd = requestedStart.plusMinutes(slotTotalMin);

        // 6a) Validate patterns: does the doctor work that day and does the slot fit?
        List<AvailabilityPatternDto> patterns = calendarServiceApi
                .findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(dto.doctorCalendarId(), dto.date().getDayOfWeek());
        if (patterns == null || patterns.isEmpty()) {
            throw new BadRequest("The doctor does not have availability on "
                    + dto.date().getDayOfWeek() + " for this calendar.");
        }
        boolean fitsInPattern = patterns.stream()
                .anyMatch(p -> !requestedStart.isBefore(p.startTime()) && !requestedEnd.isAfter(p.endTime()));
        if (!fitsInPattern) {
            throw new BadRequest("The requested time " + dto.time()
                    + " does not fit within the doctor's schedule for that day.");
        }

        // 6b) Validate exceptions: any CE blocking this slot?
        List<CalendarExceptionDto> exceptions = calendarServiceApi
                .getExceptionsByDoctorIdAndDate(dto.doctorId(), dto.date());
        if (exceptions != null && !exceptions.isEmpty()) {
            for (CalendarExceptionDto ce : exceptions) {
                if (isBlocked(ce, requestedStart, requestedEnd)) {
                    throw new BadRequest("The requested slot is blocked by a calendar exception: "
                            + ce.exceptionType() + " on " + dto.date() + ".");
                }
            }
        }

        // 6c) Validate existing appointments: is the slot already taken by another patient?
        LocalDateTime dayStart = dto.date().atStartOfDay();
        LocalDateTime dayEnd = dto.date().plusDays(1).atStartOfDay().minusNanos(1);
        List<Appointment> scheduledAppointments = appointmentRepository
                .findByDoctorCalendarIdAndStatusAndDateBetween(
                        dto.doctorCalendarId(), AppointmentStatus.SCHEDULED, dayStart, dayEnd);
        if (isBlockedByAppointments(scheduledAppointments, appointmentDateTime,
                appointmentDateTime.plusMinutes(slotTotalMin))) {
            throw new BadRequest("The slot at " + dto.time() + " on " + dto.date() + " is already taken.");
        }

        // 7) Get remaining external service data (after all local validations pass)
        DoctorDto doctorDto = getDoctorById(dto.doctorId());
        PatientInsuranceDto patientInsuranceDto = getPatientInsuranceByCredentialNumber(dto.credentialNumber());
        CoveragePlanDto coveragePlanDto = getCoveragePlanById(patientInsuranceDto.coveragePlanId());
        DoctorCalendarDto doctorCalendarDto = getDoctorCalendarById(dto.doctorCalendarId());

        // 8) Build appointment entity
        Appointment appointment = new Appointment();
        appointment.setDoctorId(doctorDto.id());
        appointment.setDoctorCalendarId(doctorCalendarDto.id());
        appointment.setPatientId(patientDto.id());
        appointment.setAppointmentType(appointmentType);
        appointment.setPatientInsuranceId(patientInsuranceDto.id());
        appointment.setConsultationCost(appointmentType.getBasePrice());
        appointment.setCoveragePercentage(coveragePlanDto.coverageValuePercentage());
        appointment.setAmountToPay(CalculateAmountToPay.calculateAmountToPay(
                appointment.getConsultationCost(), appointment.getCoveragePercentage()));
        appointment.setDate(appointmentDateTime);
        if (dto.notes() != null && !dto.notes().isEmpty()) {
            appointment.setNotes(dto.notes());
        }

        // 9) Save and return
        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment with id {} created on {} at {} - Doctor id: {}, Patient id: {}",
                savedAppointment.getId(), dto.date(), dto.time(), doctorDto.id(), patientDto.id());
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
        log.info("Appointment with id {} successfully cancelled.", id);
    }


    @Override
    public boolean existsByPatientIdAndDoctorIdAndDate(Long patientId, Long doctorId, LocalDate date, LocalTime time) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);
        return appointmentRepository.existsByPatientIdAndDoctorIdAndDate(patientId, doctorId, appointmentDateTime);
    }

    /*------------------ AVAILABLE SLOTS ----------------------*/

    List<AvailableAppointmentDto> availableAppointmentsByDoctorCalendarId(
            Long doctorCalendarId, LocalDate startDate, Integer workingDays, AppointmentType appointmentType) {

        // 1) Get DoctorCalendar
        DoctorCalendarDto doctorCalendarDto = getDoctorCalendarById(doctorCalendarId);
        Long doctorId = doctorCalendarDto.doctorId();

        // 2) Set effective dates
        LocalDate effectiveStartDate = LocalDate.now();
        int effectiveWorkingDays = workingDays != null ? workingDays : 10;

        if (startDate != null) {
            if (startDate.getDayOfWeek() == DayOfWeek.SATURDAY || startDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                throw new IllegalArgumentException("The date cannot be weekend's days.");
            }
            effectiveStartDate = startDate;
        }
        LocalDate effectiveEndDate = CountWorkingDay.getEndDate(effectiveStartDate, effectiveWorkingDays);

        // 3) Prepare result list
        List<AvailableAppointmentDto> availableAppointmentsList = new ArrayList<>();

        // 4) Slot size = duration + buffer
        int slotTotalMin = appointmentType.getDurationMin() + appointmentType.getBufferTimeMin();

        // 5) Iterate days
        LocalDate day = effectiveStartDate;
        while (!day.isAfter(effectiveEndDate)) {

            // A) Get patterns for this day
            List<AvailabilityPatternDto> patterns = calendarServiceApi
                    .findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(doctorCalendarId, day.getDayOfWeek());
            if (patterns == null || patterns.isEmpty()) {
                day = day.plusDays(1);
                continue;
            }

            // B) Get calendar exceptions on this day
            List<CalendarExceptionDto> exceptions = calendarServiceApi
                    .getExceptionsByDoctorIdAndDate(doctorId, day);

            boolean fullDayBlocked = exceptions != null
                    && exceptions.stream().anyMatch(CalendarExceptionDto::isFullDay);
            if (fullDayBlocked) {
                day = day.plusDays(1);
                continue;
            }

            // C) Load scheduled appointments for this day
            LocalDateTime dayStart = day.atStartOfDay();
            LocalDateTime dayEnd = day.plusDays(1).atStartOfDay().minusNanos(1);
            List<Appointment> scheduledAppointments = appointmentRepository
                    .findByDoctorCalendarIdAndStatusAndDateBetween(
                            doctorCalendarId, AppointmentStatus.SCHEDULED, dayStart, dayEnd);

            // D) For each pattern, generate slots
            for (AvailabilityPatternDto p : patterns) {
                LocalTime slotStart = p.startTime();
                LocalTime slotEnd = slotStart.plusMinutes(slotTotalMin);

                while (!slotEnd.isAfter(p.endTime())) {

                    boolean blockedByException = false;
                    if (exceptions != null && !exceptions.isEmpty()) {
                        for (CalendarExceptionDto ce : exceptions) {
                            if (isBlocked(ce, slotStart, slotEnd)) {
                                blockedByException = true;
                                break;
                            }
                        }
                    }

                    boolean blockedByAppointment = false;
                    if (!blockedByException) {
                        LocalDateTime slotStartDt = LocalDateTime.of(day, slotStart);
                        LocalDateTime slotEndDt = LocalDateTime.of(day, slotEnd);
                        blockedByAppointment = isBlockedByAppointments(
                                scheduledAppointments, slotStartDt, slotEndDt);
                    }

                    if (!blockedByException && !blockedByAppointment) {
                        availableAppointmentsList.add(new AvailableAppointmentDto(
                                day, slotStart, slotEnd, appointmentType,
                                doctorCalendarId, AppointmentStatus.AVAILABLE));
                    }

                    slotStart = slotStart.plusMinutes(slotTotalMin);
                    slotEnd = slotStart.plusMinutes(slotTotalMin);
                }
            }
            day = day.plusDays(1);
        }
        return availableAppointmentsList;
    }

    /*------------------ HELPER METHODS ----------------------*/

    private boolean isBlocked(CalendarExceptionDto ce, LocalTime slotStart, LocalTime slotEnd) {
        if (ce.isFullDay()) return true;
        LocalTime ceStart = ce.startTime();
        LocalTime ceEnd = ce.endTime();
        boolean noOverlap = !ceEnd.isAfter(slotStart) || !ceStart.isBefore(slotEnd);
        return !noOverlap;
    }

    private boolean overlaps(LocalDateTime apStart, LocalDateTime apEnd,
                              LocalDateTime slotStart, LocalDateTime slotEnd) {
        boolean noOverlap = !apEnd.isAfter(slotStart) || !apStart.isBefore(slotEnd);
        return !noOverlap;
    }

    private boolean isBlockedByAppointments(List<Appointment> scheduledAppointments,
                                             LocalDateTime slotStart, LocalDateTime slotEnd) {
        if (scheduledAppointments == null || scheduledAppointments.isEmpty()) return false;
        for (Appointment ap : scheduledAppointments) {
            int apTotalMin = ap.getAppointmentType().getDurationMin()
                    + ap.getAppointmentType().getBufferTimeMin();
            LocalDateTime apStart = ap.getDate();
            LocalDateTime apEnd = apStart.plusMinutes(apTotalMin);
            if (overlaps(apStart, apEnd, slotStart, slotEnd)) {
                return true;
            }
        }
        return false;
    }
}
