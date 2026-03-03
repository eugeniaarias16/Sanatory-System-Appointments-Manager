package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.CalendarExceptionResponseDto;
import com.sanatoryApp.CalendarService.entity.CalendarException;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import com.sanatoryApp.CalendarService.entity.ExceptionScope;
import com.sanatoryApp.CalendarService.entity.ExceptionType;
import com.sanatoryApp.CalendarService.exception.BadRequest;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.repository.ICalendarExceptionRepository;
import com.sanatoryApp.CalendarService.repository.UserServiceApi;
import com.sanatoryApp.CalendarService.utils.TimeConstants;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.sanatoryApp.CalendarService.utils.TimeValidationUtils.*;
import static com.sanatoryApp.CalendarService.utils.ValidateDtoFields.*;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CalendarExceptionService implements ICalendarExceptionService {

    private final ICalendarExceptionRepository calendarExceptionRepository;
    private final IDoctorCalendarService doctorCalendarService;
    private final UserServiceApi userServiceApi;

    @Override
    @Transactional
    public CalendarExceptionResponseDto createCalendarException(CalendarExceptionCreateDto dto) {
        /* Validates that the provided fields are consistent with the scope */
        validateScopeConsistency(dto.scope(), dto.doctorId(), dto.doctorCalendarId());

        //Validate that endTime is after startTime
        validateTimeRange(dto.startTime(), dto.endTime());

        //validate Range Date endDate
        if (dto.endDate() != null) {
            validateDateRange(dto.startDate(), dto.endDate());
        }

        //validate ExceptionType and Reason
        validateExceptionTypeAndReason(dto.exceptionType(), dto.reason());


        //validate if Doctor exists by id
        if (dto.scope() == ExceptionScope.SEMI_GLOBAL) {
            validateDoctorId(dto.doctorId());
        }

        //verify if exists conflict creating calendar exception
        List<CalendarExceptionResponseDto> conflicts = findExistingCalendarExceptionCoincidence(
                dto.doctorId(),
                dto.doctorCalendarId(),
                dto.startDate(),
                dto.endDate(),
                0L
        );

        if (!conflicts.isEmpty()) {
            String errorMsg = buildConflictErrorMessage(conflicts);
            throw new BadRequest(errorMsg);
        }

        //create Calendar Exception Entity
        CalendarException calendarException = dto.toEntity();

        //Get DoctorCalendar if scope is SPECIFIC
        if (dto.scope() == ExceptionScope.SPECIFIC) {
            DoctorCalendar doctorCalendar = doctorCalendarService.getDoctorCalendarEntityById(dto.doctorCalendarId());
            calendarException.setDoctorCalendar(doctorCalendar);
            calendarException.setDoctorId(doctorCalendar.getDoctorId());

        }


        //verify if is FullDay
        if (isFullDayRange(dto.startTime(), dto.endTime())) {
            log.info("Creating full-day exception for {}", dto.startDate());
            calendarException.setFullDay(true);
            calendarException.setStartTime(TimeConstants.START_OF_DAY);
            calendarException.setEndTime(TimeConstants.END_OF_DAY);
        } else {
            log.info("Creating partial-day exception from day {} at {}hs to day{} at {}hs",
                    dto.startDate(), dto.startTime(), dto.endDate(), dto.endTime());
            calendarException.setFullDay(false);
        }


        CalendarException savedCE = calendarExceptionRepository.save(calendarException);

        log.info("Calendar Exception successfully created -SCOPE:{} ID:{}, Type:{}, Date:{} to {}.",
                savedCE.getScope(),
                savedCE.getId(),
                savedCE.getExceptionType(),
                savedCE.getStartDate(),
                savedCE.getEndDate() != null ? savedCE.getEndDate() : savedCE.getStartDate());
        return CalendarExceptionResponseDto.fromEntity(savedCE);
    }


    @Override
    @Transactional
    public CalendarExceptionResponseDto updateCalendarException(Long id, CalendarExceptionUpdateDto dto) {
        CalendarException existingCE = calendarExceptionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFound("Calendar exception with id " + id + " not found."));



        /* COMPUTE EFFECTIVE VALUES
         * if dto field is present (value or null) the effective value would be  field's data,
         * if the dto field is empty, the effective value is that of the entity existing in the BD.
         */
        LocalDate efStartDate = getEffectiveValue(dto.getStartDate(), existingCE.getStartDate());
        LocalDate efEndDate = getEffectiveValue(dto.getEndDate(), existingCE.getEndDate());
        LocalTime efStartTime = getEffectiveValue(dto.getStartTime(), existingCE.getStartTime());
        LocalTime efEndTime = getEffectiveValue(dto.getEndTime(), existingCE.getEndTime());
        ExceptionType efExceptionType = getEffectiveValue(dto.getExceptionType(), existingCE.getExceptionType());
        ExceptionScope efScope = getEffectiveValue(dto.getScope(), existingCE.getScope());
        String efReason = getEffectiveValue(dto.getReason(), existingCE.getReason());


        // validate non-nullable fields
        if (efStartDate == null) {
            throw new IllegalArgumentException("startDate cannot be set to null");
        }
        if (efExceptionType == null) {
            throw new IllegalArgumentException("exceptionType cannot be set to null");
        }
        if (efScope == null) {
            throw new IllegalArgumentException("scope cannot be set to null");
        }

        //validate time range, date range, and exceptionType + reason
        validateTimeRange(efStartTime, efEndTime);
        if (efEndDate != null) {
            validateDateRange(efStartDate, efEndDate);
        }
        validateExceptionTypeAndReason(efExceptionType, efReason);


        //resolve scope-dependent fields(doctorId, doctorCalendarId)
        Long efDoctorId;
        Long efDoctorCalendarId;
        DoctorCalendar efDoctorCalendar = null;

        switch (efScope) {
            case GLOBAL:
                efDoctorId = null;
                efDoctorCalendarId = null;
                break;
            case SEMI_GLOBAL:
                efDoctorId = getEffectiveValue(dto.getDoctorId(), existingCE.getDoctorId());
                validateDoctorId(efDoctorId);
                efDoctorCalendarId = null;
                break;
            case SPECIFIC:
                efDoctorCalendarId = getEffectiveValue(
                        dto.getDoctorCalendarId(),
                        existingCE.getDoctorCalendar() != null ? existingCE.getDoctorCalendar().getId() : null);
                efDoctorCalendar = doctorCalendarService.getDoctorCalendarEntityById(efDoctorCalendarId);


                efDoctorId = efDoctorCalendar.getDoctorId();
                if (dto.getDoctorId().isPresent() && dto.getDoctorId().get() != null) {
                    if (!efDoctorId.equals(dto.getDoctorId().get())) {
                        throw new IllegalArgumentException("The Doctor ID provided does not correspond to the Doctor Calendar ID.");
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown scope: " + efScope);
        }

        // Check conflicts using effective values (exclude current CE by id)
        List<CalendarExceptionResponseDto> conflicts = findExistingCalendarExceptionCoincidence(efDoctorId, efDoctorCalendarId, efStartDate, efEndDate, id);
        if (!conflicts.isEmpty()) {
            throw new BadRequest(buildConflictErrorMessage(conflicts));
        }


        //Apply effective values to entity
        existingCE.setStartDate(efStartDate);
        existingCE.setEndDate(efEndDate);
        existingCE.setStartTime(efStartTime);
        existingCE.setEndTime(efEndTime);
        existingCE.setScope(efScope);
        existingCE.setExceptionType(efExceptionType);
        existingCE.setDoctorId(efDoctorId);
        existingCE.setDoctorCalendar(efDoctorCalendar);

        if (efReason != null && !efReason.trim().isEmpty()) {
            existingCE.setReason(efReason.trim().toLowerCase());
        } else {
            existingCE.setReason(null);
        }


        //save and return
        CalendarException savedCE = calendarExceptionRepository.save(existingCE);
        log.info("Calendar Exception successfully updated - SCOPE:{} ID:{}, Type:{}, Date:{} to {}.",
                savedCE.getScope(), savedCE.getId(), savedCE.getExceptionType(),
                savedCE.getStartDate(),
                savedCE.getEffectiveEndDate());

        return CalendarExceptionResponseDto.fromEntity(savedCE);

    }

    @Override
    public CalendarExceptionResponseDto findByIdAndIsActive(Long id) {
        CalendarException calendarException = calendarExceptionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFound("Calendar exception with id " + id + " not found."));
        return CalendarExceptionResponseDto.fromEntity(calendarException);
    }

    @Override
    public List<CalendarExceptionResponseDto> findBydDoctorId(Long doctorId) {
        List<CalendarException> calendarExceptions = calendarExceptionRepository.findByDoctorId(doctorId);
        return calendarExceptions
                .stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CalendarExceptionResponseDto> findAllGlobalAndIsActive() {
        List<CalendarException> calendarExceptions = calendarExceptionRepository.findAllGlobalAndIsActive();
        return calendarExceptions
                .stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CalendarExceptionResponseDto> findSemiGlobalByDoctorIdAndIsActive(Long doctorId) {
        List<CalendarException> calendarExceptions = calendarExceptionRepository.findSemiGlobalByDoctorIdAndIsActive(doctorId);
        return calendarExceptions
                .stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CalendarExceptionResponseDto> findSpecificByDoctorCalendarIdAndIsActive(Long doctorCalendarId) {
        List<CalendarException> calendarExceptions = calendarExceptionRepository.findSpecificByDoctorCalendarIdAndIsActive(doctorCalendarId);
        return calendarExceptions
                .stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CalendarExceptionResponseDto> findByDoctorIdAndDateAndHour(Long doctorId, LocalDate date) {
        List<CalendarException> calendarExceptions = calendarExceptionRepository.findByDoctorIdAndDate(doctorId, date);
        return calendarExceptions
                .stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }


    @Override
    public List<CalendarExceptionResponseDto> findExistingCalendarExceptionCoincidence(Long doctorId, Long calendarId, LocalDate startDate, LocalDate endDate, Long excludeId) {

        List<CalendarException> calendarExceptionList = calendarExceptionRepository
                .findExistingCalendarExceptionCoincidence(doctorId, calendarId, startDate, endDate, excludeId);

        return calendarExceptionList.stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }


    @Override
    public void deleteCalendarExceptionById(Long id) {

        log.debug("Attempting to delete Calendar Exception with id: {}", id);
        CalendarException calendarException = calendarExceptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Calendar Exception not found with id:" + id));
        calendarExceptionRepository.delete(calendarException);
        log.info("Calendar Exception with id: {} successfully deleted.");

    }


    /*  HELPER METHODS */


    private String buildConflictErrorMessage(List<CalendarExceptionResponseDto> conflicts) {
        StringBuilder errorMsg = new StringBuilder("Schedule conflict detected. The following exception(s) already exist:\n");
        for (CalendarExceptionResponseDto ce : conflicts) {
            String dateRange = ce.endDate() == null ?
                    ce.startDate().toString() :
                    ce.startDate().toString() + " to " + ce.endDate().toString();

            errorMsg.append(String.format("Scope:[%s], ID:%d, Type:%s, Date:%s", ce.scope(), ce.id(), ce.exceptionType(), dateRange));

            if (ce.reason() != null) {
                errorMsg.append(String.format(",Reason:[%s]", ce.reason()));
            }
            errorMsg.append("\n");

        }
        return errorMsg.toString();
    }

    private void validateDoctorId(Long id) {
        try {
            userServiceApi.getDoctorById(id);
        } catch (FeignException feignException) {
            throw new ResourceNotFound("Doctor with id " + id + " not found.");
        }
    }

    private <T> T getEffectiveValue(JsonNullable<T> dtoField, T existingValue) {
        return dtoField.isPresent() ? dtoField.get() : existingValue;
    }






}

