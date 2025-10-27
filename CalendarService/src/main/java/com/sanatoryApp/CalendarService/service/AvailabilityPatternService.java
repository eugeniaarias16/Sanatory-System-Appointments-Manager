package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.AvailabilityPatternResponseDto;
import com.sanatoryApp.CalendarService.entity.AvailabilityPattern;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.repository.IAvailabilityPatternRepository;
import com.sanatoryApp.CalendarService.repository.IDoctorCalendarRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;

import static com.sanatoryApp.CalendarService.utils.TimeValidationUtils.validateTimeRange;


@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class AvailabilityPatternService implements IAvailabilityPatternService {

    private final IAvailabilityPatternRepository availabilityPatternRepository;
    private final IDoctorCalendarRepository doctorCalendarRepository;

    @Override
    public AvailabilityPatternResponseDto findAvailabilityPatternById(Long id) {
        log.debug("Attempting to find Availability Pattern by id: {}",id);
        AvailabilityPattern availabilityPattern=availabilityPatternRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Availability Pattern not found with id: "+id));
        return AvailabilityPatternResponseDto.fromEntity(availabilityPattern);
    }

    @Override
    public List<AvailabilityPatternResponseDto> findAvailabilityPatternByDoctorCalendarId(Long id) {
        log.debug("Attempting to find Availability Patterns by doctor calendar id: {}",id);
        List<AvailabilityPattern> availabilityPatternList=availabilityPatternRepository.findByDoctorCalendarId(id) ;
        if(availabilityPatternList.isEmpty()) {
           throw  new ResourceNotFound("Availability Pattern not found with doctor calendar id: " + id);
        }
        return availabilityPatternList.stream()
                .map(AvailabilityPatternResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AvailabilityPatternResponseDto> findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(Long calendarId, DayOfWeek dayOfWeek) {
        List<AvailabilityPattern> availabilityPatternList=availabilityPatternRepository.findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(calendarId,dayOfWeek);
        if(availabilityPatternList.isEmpty()){
            log.info("No Availability Pattern found with calendar id: {} on Day: {}",calendarId,dayOfWeek);
        }
        return  availabilityPatternList.stream()
                .map(AvailabilityPatternResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AvailabilityPatternResponseDto> findByDoctorId(Long doctorId) {
        List<AvailabilityPattern> availabilityPatternList=availabilityPatternRepository.findByDoctorId(doctorId);
        if(availabilityPatternList.isEmpty()){
            log.info("No Availability Pattern found with doctor id {}",doctorId);
        }
        return  availabilityPatternList.stream()
                .map(AvailabilityPatternResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AvailabilityPatternResponseDto> findByDoctorIdAndDay(Long doctorId, DayOfWeek dayOfWeek) {
        List<AvailabilityPattern> availabilityPatternList=availabilityPatternRepository.findByDoctorIdAndDay(doctorId,dayOfWeek);
        if(availabilityPatternList.isEmpty()){
            log.info("No Availability Pattern found with doctor id:{} on Day: {}",doctorId,dayOfWeek);
        }
        return  availabilityPatternList.stream()
                .map(AvailabilityPatternResponseDto::fromEntity)
                .toList();
    }

    @Transactional
    @Override
    public AvailabilityPatternResponseDto createAvailabilityPattern(AvailabilityPatternCreateDto dto) {
       log.debug("Attempting to create Availability Pattern with values: {} ",dto);

       //validate range of time
        validateTimeRange(dto.getStartTime(),dto.getEndTime());

       //verify that the doctor calendar ID actually exists
        if(!doctorCalendarRepository.existsById(dto.getDoctorCalendarId())){
            throw new ResourceNotFound("The Doctor Calendar's id is wrong.");
        }

        //verify if exists overlapping pattern
        if (availabilityPatternRepository.hasOverlappingPattern(dto.getDoctorCalendarId(),dto.getDayOfWeek(),dto.getStartTime(),dto.getEndTime(),0L)){
            throw new IllegalArgumentException("Schedule conflict: The new time slot overlaps with an existing schedule.\n The doctor cannot have overlapping schedules across any of their calendars. ");
        }




        AvailabilityPattern availabilityPattern=dto.toEntity();
        AvailabilityPattern saved=availabilityPatternRepository.save(availabilityPattern);
        log.info("Availability Pattern successfully created with id {}",saved.getId());
        return AvailabilityPatternResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public AvailabilityPatternResponseDto updateAvailabilityPatternById(Long id, AvailabilityPatternUpdateDto dto) {
        log.debug("Attempting to update Availability Pattern with id {}",id);
        AvailabilityPattern existingAvailabilityPattern=availabilityPatternRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Availability Pattern not found with id: "+id));

        validateTimeRange(dto.startTime(),dto.endTime());
        if(availabilityPatternRepository.hasOverlappingPattern(
                existingAvailabilityPattern.getDoctorCalendarId(),
                existingAvailabilityPattern.getDayOfWeek(),
                dto.startTime(),
                dto.endTime(),
                id)){
            throw new IllegalArgumentException("Schedule conflict: The updated time slot overlaps with an existing schedule."
            );
        }

        existingAvailabilityPattern.setStartTime(dto.startTime());
        existingAvailabilityPattern.setEndTime(dto.endTime());
        AvailabilityPattern saved=availabilityPatternRepository.save(existingAvailabilityPattern);
        log.info("Availability Pattern successfully updated with start-time: {} and end-time: {}",saved.getStartTime(),saved.getEndTime());
        return AvailabilityPatternResponseDto.fromEntity(saved);


    }

    @Transactional
    @Override
    public void deleteAvailabilityPatternById(Long id) {
        log.debug("Attempting to delete Availability Pattern with id {}",id);
        AvailabilityPattern availabilityPattern=availabilityPatternRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Availability Pattern not found with id: "+id));
       availabilityPatternRepository.delete(availabilityPattern);

        log.info("Availability Pattern with id {} successfully deleted", id);

    }

    @Override
    public void sofDeleteAvailabilityPatternById(Long id) {
        log.debug("Attempting to deactivate Availability Pattern with id {}",id);
        AvailabilityPattern availabilityPattern=availabilityPatternRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Availability Pattern not found with id: "+id));
        availabilityPattern.setIsActive(false);
        availabilityPatternRepository.save(availabilityPattern);
        log.info("Availability Pattern with id {} successfully deactivated (soft delete)", id);

    }


}
