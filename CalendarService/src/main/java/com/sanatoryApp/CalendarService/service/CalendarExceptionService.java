package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.CalendarExceptionResponseDto;
import com.sanatoryApp.CalendarService.entity.CalendarException;
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
public class CalendarExceptionService implements ICalendarExceptionService{
    private final ICalendarExceptionRepository calendarExceptionRepository;
    private final DoctorCalendarService doctorCalendarService;

    @Override
    public CalendarExceptionResponseDto findById(Long id) {
        CalendarException calendarException= calendarExceptionRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("No calendar exception found with id "+id));
        return CalendarExceptionResponseDto.fromEntity(calendarException);
    }
    @Transactional
    @Override
    public CalendarExceptionResponseDto createCalendarException(CalendarExceptionCreateDto dto) {

        //validate time range
        validateTimeRange(dto.getStartTime(),dto.getEndTime());

        //verify if doctor calendar exists
        doctorCalendarService.findDoctorCalendarById(dto.getDoctorCalendarId());

        //validate future date
        validateFutureDate(dto.getDate());

        //verify if exist conflict exception
        validateNoConflictException(dto.getDoctorCalendarId(),dto.getDate(),0L);

        CalendarException calendarException=dto.toEntity();

        //verify if it's a full day exception or not
        if (isFullDayRange(dto.getStartTime(), dto.getEndTime())) {
            log.info("Creating full-day exception for {}", dto.getDate());
            calendarException.setStartTime(TimeConstants.START_OF_DAY); //set startTime= 0:0:0
            calendarException.setEndTime(TimeConstants.END_OF_DAY); //set endTime = 23:59:59
            calendarException.setFullDay(true);
        } else {
            log.info("Creating partial-day exception for {} from {} to {}",
                    dto.getDate(), dto.getStartTime(), dto.getEndTime());
            calendarException.setFullDay(false);
        }

        calendarException.setActive(true);

        CalendarException saved=calendarExceptionRepository.save(calendarException);
        log.info("Calendar exception created with id: {}", saved.getId());

        return CalendarExceptionResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public CalendarExceptionResponseDto updateCalendarException(Long id, CalendarExceptionUpdateDto dto) {
        //verify if calendar exception exists
        CalendarException existingCalendarException= calendarExceptionRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("No calendar exception found with id "+id));

        //validate time range
        validateTimeRange(dto.startTime(),dto.endTime());

        //verify if doctor calendar exists
        doctorCalendarService.findDoctorCalendarById(dto.doctorCalendarId());

        //validate future date
        validateFutureDate(dto.date());

        //verify if exist conflict exception
        validateNoConflictException(dto.doctorCalendarId(),dto.date(),id);

        //Update calendar exception
        existingCalendarException.setDoctorCalendarId(dto.doctorCalendarId());
        existingCalendarException.setDate(dto.date());
        existingCalendarException.setGlobal(dto.isGlobal());

        //verify if it's a full day exception or not
        if (isFullDayRange(dto.startTime(), dto.endTime())) {
            log.info("Updating full-day exception for {}", dto.date());
            existingCalendarException.setStartTime(TimeConstants.START_OF_DAY); //set startTime= 0:0:0
            existingCalendarException.setEndTime(TimeConstants.END_OF_DAY); //set endTime = 23:59:59
            existingCalendarException.setFullDay(true);
        } else {
            log.info("Updating partial-day exception for {} from {} to {}",
                    dto.date(), dto.startTime(), dto.endTime());
            existingCalendarException.setStartTime(dto.startTime());
            existingCalendarException.setEndTime(dto.endTime());
            existingCalendarException.setFullDay(false);
            if(!dto.reason().isEmpty()){
                existingCalendarException.setReason(dto.reason().toLowerCase());
            }
        }


        CalendarException saved=calendarExceptionRepository.save(existingCalendarException);
        log.info("Calendar Exception successfully updated {}",saved);
        return CalendarExceptionResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public void deleteCalendarException(Long id) {
        log.debug("Attempting to delete Calendar Exception with id {}",id);
        CalendarException existingCalendarException= calendarExceptionRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("No calendar exception found with id "+id));
        existingCalendarException.setActive(false);
        CalendarException saved=calendarExceptionRepository.save(existingCalendarException);
        log.info("Calendar Exception with id {} successfully deactivated (soft delete)", id);
    }

    @Override
    public List<CalendarExceptionResponseDto> findByDoctorCalendarId(Long doctorCalendarId) {
        List<CalendarException> calendarExceptionList=calendarExceptionRepository.findByDoctorCalendarId(doctorCalendarId);
        if(calendarExceptionList.isEmpty()){
            log.info("No Calendar Exception found with Doctor Calendar id {}",doctorCalendarId);
        }
        return  calendarExceptionList.stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CalendarExceptionResponseDto> findApplicableExceptionsInTimeRange(Long calendarId, Long doctorId, LocalDate startTime, LocalDate endTime) {
        List<CalendarException> calendarExceptionList=calendarExceptionRepository.findApplicableExceptionsInTimeRange(calendarId,doctorId,startTime,endTime);
        if(calendarExceptionList.isEmpty()){
            log.info("No Calendar Exception found with calendar id {} or doctor id{} between dates {}-{}",calendarId,doctorId,startTime,endTime);
        }
        return  calendarExceptionList.stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CalendarExceptionResponseDto> findApplicableExceptionsForCalendar(Long calendarId, Long doctorId, LocalDate date) {
        List<CalendarException>calendarExceptionList=calendarExceptionRepository.findApplicableExceptionsForCalendar(calendarId,doctorId,date);
        if(calendarExceptionList.isEmpty()){
            log.info("No Calendar Exception found for calendar id {} ond date {}",calendarId,date);
        }
        return  calendarExceptionList.stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }


    @Override
    public List<CalendarExceptionResponseDto> findGlobalExceptionsByDoctorId(Long doctorId) {
        List<CalendarException>calendarExceptionList=calendarExceptionRepository.findGlobalExceptionsByDoctorId(doctorId);
        if(calendarExceptionList.isEmpty()){
            log.info("No Global Exception found for Doctor with id {}",doctorId);
        }
        return  calendarExceptionList.stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CalendarExceptionResponseDto> findFutureExceptions(Long calendarId, LocalDate currentDate) {
        List<CalendarException>calendarExceptionList=calendarExceptionRepository.findFutureExceptions(calendarId,currentDate);
        if(calendarExceptionList.isEmpty()){
            log.info("No future Calendar Exception found for calendar id {} with current date {}",calendarId,currentDate);
        }
        return  calendarExceptionList.stream()
                .map(CalendarExceptionResponseDto::fromEntity)
                .toList();
    }

    private void validateNoConflictException(Long calendarId,LocalDate date,Long excludedId){
       log.debug("Validating no conflicting exception: calendar={}, date={},excluded id={}",calendarId,date,excludedId);

       boolean hasConflict=calendarExceptionRepository.existsConflictingException(calendarId,date,excludedId);
       if(hasConflict) {
           throw new IllegalArgumentException(
                   "A conflicting exception already exists for this calendar on " + date + "." +
                           " Please update the existing exception instead of creating a new one."
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


}
