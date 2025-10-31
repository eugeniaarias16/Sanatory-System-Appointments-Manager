package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.CalendarExceptionResponseDto;
import com.sanatoryApp.CalendarService.entity.CalendarException;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import com.sanatoryApp.CalendarService.entity.ExceptionType;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.repository.ICalendarExceptionRepository;
import com.sanatoryApp.CalendarService.utils.TimeConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.sanatoryApp.CalendarService.utils.TimeValidationUtils.isFullDayRange;
import static com.sanatoryApp.CalendarService.utils.TimeValidationUtils.validateTimeRange;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarExceptionService implements ICalendarExceptionService {

    private final ICalendarExceptionRepository calendarExceptionRepository;
    private final DoctorCalendarService doctorCalendarService;

    @Override
    public CalendarExceptionResponseDto findById(Long id) {
        CalendarException calendarException = calendarExceptionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFound("No calendar exception found with id " + id));
        return CalendarExceptionResponseDto.fromEntity(calendarException);
    }

    @Override
    @Transactional
    public CalendarExceptionResponseDto createCalendarException(CalendarExceptionCreateDto dto) {
        validateTimeRange(dto.getStartTime(), dto.getEndTime());

        DoctorCalendar doctorCalendar =doctorCalendarService.getDoctorCalendarEntityById(dto.getDoctorCalendarId());

        validateFutureDate(dto.getDate());

        validateExceptionTypeAndReason(dto.getExceptionType(), dto.getReason());

        validateNoConflictException(dto.getDoctorCalendarId(), dto.getDate(), 0L);

        CalendarException calendarException = dto.toEntity(doctorCalendar);

        if (isFullDayRange(dto.getStartTime(), dto.getEndTime())) {
            log.info("Creating full-day exception for {}", dto.getDate());
            calendarException.setStartTime(TimeConstants.START_OF_DAY);
            calendarException.setEndTime(TimeConstants.END_OF_DAY);
            calendarException.setFullDay(true);
        } else {
            log.info("Creating partial-day exception for {} from {} to {}",
                    dto.getDate(), dto.getStartTime(), dto.getEndTime());
            calendarException.setFullDay(false);
        }

        CalendarException saved = calendarExceptionRepository.save(calendarException);
        log.info("Calendar exception created with id: {} - Type: {}",
                saved.getId(), saved.getExceptionType());

        return CalendarExceptionResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public CalendarExceptionResponseDto updateCalendarException(Long id, CalendarExceptionUpdateDto dto) {
        CalendarException existingCalendarException = calendarExceptionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFound("No calendar exception found with id " + id));

        if (dto.doctorCalendarId() != null) {
            DoctorCalendar doctorCalendar =doctorCalendarService.getDoctorCalendarEntityById(dto.doctorCalendarId());
            existingCalendarException.setDoctorCalendar(doctorCalendar);
        }

        if (dto.date() != null) {
            validateFutureDate(dto.date());

            Long calendarIdToValidate =existingCalendarException.getDoctorCalendar().getId();

            validateNoConflictException(calendarIdToValidate, dto.date(), id);
            existingCalendarException.setDate(dto.date());
        }

        if (dto.exceptionType() != null) {
            validateExceptionTypeAndReason(
                    dto.exceptionType(),
                    dto.reason() != null ? dto.reason() : existingCalendarException.getReason()
            );
            existingCalendarException.setExceptionType(dto.exceptionType());
        }

        if (dto.isGlobal() != null) {
            existingCalendarException.setGlobal(dto.isGlobal());
        }

        boolean startTimeProvided = dto.startTime() != null;
        boolean endTimeProvided = dto.endTime() != null;

        if (startTimeProvided != endTimeProvided) {
            throw new IllegalArgumentException(
                    "Both startTime and endTime must be provided together, or neither should be provided"
            );
        }

        if (startTimeProvided && endTimeProvided) {
            validateTimeRange(dto.startTime(), dto.endTime());

            if (isFullDayRange(dto.startTime(), dto.endTime())) {
                log.info("Updating to full-day exception for {}", existingCalendarException.getDate());
                existingCalendarException.setStartTime(TimeConstants.START_OF_DAY);
                existingCalendarException.setEndTime(TimeConstants.END_OF_DAY);
                existingCalendarException.setFullDay(true);
            } else {
                log.info("Updating to partial-day exception for {} from {} to {}",
                        existingCalendarException.getDate(), dto.startTime(), dto.endTime());
                existingCalendarException.setStartTime(dto.startTime());
                existingCalendarException.setEndTime(dto.endTime());
                existingCalendarException.setFullDay(false);
            }
        }

        if (dto.reason() != null && !dto.reason().trim().isEmpty()) {
            existingCalendarException.setReason(dto.reason().trim().toLowerCase());
        }

        CalendarException saved = calendarExceptionRepository.save(existingCalendarException);
        log.info("Calendar Exception successfully updated with id: {} - Type: {}",
                saved.getId(), saved.getExceptionType());
        return CalendarExceptionResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    public void deleteCalendarException(Long id) {
        log.debug("Attempting to delete Calendar Exception with id {}", id);

        CalendarException existingCalendarException = calendarExceptionRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFound("No calendar exception found with id " + id));

        existingCalendarException.setActive(false);
        calendarExceptionRepository.save(existingCalendarException);

        log.info("Calendar Exception with id {} successfully deactivated (soft delete)", id);
    }

    @Override
    public List<CalendarExceptionResponseDto> findByDoctorCalendarId(Long doctorCalendarId) {
        List<CalendarException> calendarExceptionList =
                calendarExceptionRepository.findByDoctorCalendarId(doctorCalendarId);

        if (calendarExceptionList.isEmpty()) {
            log.info("No Calendar Exception found with Doctor Calendar id {}", doctorCalendarId);
        }

        return calendarExceptionList.stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CalendarExceptionResponseDto> findApplicableExceptionsInTimeRange(
            Long calendarId, Long doctorId, LocalDate startTime, LocalDate endTime) {

        List<CalendarException> calendarExceptionList =
                calendarExceptionRepository.findApplicableExceptionsInTimeRange(
                        calendarId, doctorId, startTime, endTime);

        if (calendarExceptionList.isEmpty()) {
            log.info("No Calendar Exception found with calendar id {} or doctor id {} between dates {}-{}",
                    calendarId, doctorId, startTime, endTime);
        }

        return calendarExceptionList.stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CalendarExceptionResponseDto> findApplicableExceptionsForCalendar(
            Long calendarId, Long doctorId, LocalDate date) {

        List<CalendarException> calendarExceptionList =
                calendarExceptionRepository.findApplicableExceptionsForCalendar(calendarId, doctorId, date);

        if (calendarExceptionList.isEmpty()) {
            log.info("No Calendar Exception found for calendar id {} and date {}", calendarId, date);
        }

        return calendarExceptionList.stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CalendarExceptionResponseDto> findGlobalExceptionsByDoctorId(Long doctorId) {
        List<CalendarException> calendarExceptionList =
                calendarExceptionRepository.findGlobalExceptionsByDoctorId(doctorId);

        if (calendarExceptionList.isEmpty()) {
            log.info("No Global Exception found for Doctor with id {}", doctorId);
        }

        return calendarExceptionList.stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CalendarExceptionResponseDto> findFutureExceptions(Long calendarId, LocalDate currentDate) {
        List<CalendarException> calendarExceptionList =
                calendarExceptionRepository.findFutureExceptions(calendarId, currentDate);

        if (calendarExceptionList.isEmpty()) {
            log.info("No future Calendar Exception found for calendar id {} with current date {}",
                    calendarId, currentDate);
        }

        return calendarExceptionList.stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    private void validateNoConflictException(Long calendarId, LocalDate date, Long excludedId) {
        log.debug("Validating no conflicting exception: calendar={}, date={}, excluded id={}",
                calendarId, date, excludedId);

        boolean hasConflict = calendarExceptionRepository.existsConflictingException(
                calendarId, date, excludedId);

        if (hasConflict) {
            throw new IllegalArgumentException(
                    "A conflicting exception already exists for this calendar on " + date + ". " +
                            "Please update the existing exception instead of creating a new one."
            );
        }

        log.debug("No conflicts found");
    }

    private void validateFutureDate(LocalDate date) {
        log.debug("Validating future date: {}", date);

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Cannot create exceptions for past dates. Date provided: " + date
            );
        }
    }

    private void validateExceptionTypeAndReason(ExceptionType exceptionType, String reason) {
        if (exceptionType == ExceptionType.CUSTOM) {
            if (reason == null || reason.trim().isEmpty()) {
                throw new IllegalArgumentException(
                        "When exception type is CUSTOM, a reason must be provided"
                );
            }
        }

        log.debug("Exception type {} validated successfully", exceptionType);
    }
}