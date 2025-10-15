package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarResponseDto;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.repository.IDoctorCalendarRepository;
import com.sanatoryApp.CalendarService.utils.TimeZoneValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DoctorCalendarService implements IDoctorCalendarService{

    private final IDoctorCalendarRepository doctorCalendarRepository;


    @Override
    public DoctorCalendarResponseDto findDoctorCalendarById(Long id) {
        DoctorCalendar doctorCalendar=doctorCalendarRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Doctor Calendar not found with id "+id));
        return DoctorCalendarResponseDto.fromEntity(doctorCalendar);
    }

    @Override
    public DoctorCalendarResponseDto createDoctorCalendar(DoctorCalendarCreateDto dto) {
       log.debug("Attempting to create a new Doctor Calendar");

       log.debug("Verifying if doctor with id {} exists.",dto.getDoctorId());
        //verify if doctor exists by id
        doctorExistsById(dto.getDoctorId());

        //validate time zone
        TimeZoneValidator.validateTimeZone(dto.getTimeZone());

        log.debug("Creating new Doctor Calendar for the doctor with the ID {}",dto.getDoctorId());
        DoctorCalendar doctorCalendar=dto.toEntity();
        DoctorCalendar saved=doctorCalendarRepository.save(doctorCalendar);
        log.info("The new Doctor Calendar {} was successfully created for the doctor with id {} and timezone {}",dto.getName(),dto.getDoctorId(),dto.getTimeZone());
        return DoctorCalendarResponseDto.fromEntity(saved);

    }

    @Override
    public DoctorCalendarResponseDto updateDoctorCalendar(Long id, DoctorCalendarUpdateDto dto) {
        //verify if doctor calendar exists
        DoctorCalendar existingDoctorCalendar=doctorCalendarRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Doctor Calendar not found with id "+id));

        //validate doctor id
        if(dto.doctorId()!=null){
            doctorExistsById(dto.doctorId());
            existingDoctorCalendar.setDoctorId(dto.doctorId());
        }

        //validate time zone
        if(dto.timeZone()!=null){
            TimeZoneValidator.validateTimeZone(dto.timeZone());
            existingDoctorCalendar.setTimeZone(ZoneId.of(dto.timeZone()));
        }

        if(dto.name()!=null && !dto.name().isEmpty()){
            existingDoctorCalendar.setName(dto.name().toLowerCase());
        }

        DoctorCalendar saved=doctorCalendarRepository.save(existingDoctorCalendar);
        log.info("Doctor Calendar with id {} successfully updated - doctorId: {}, name: {},timeZone: {}",saved.getId(),saved.getDoctorId(),saved.getName(),saved.getTimeZone());
        return DoctorCalendarResponseDto.fromEntity(saved);
    }

    @Override
    public void deleteDoctorCalendar(Long id) {
        log.debug("Attempting to sof delete Doctor Calendar with id {}",id);
        DoctorCalendar existingDoctorCalendar=doctorCalendarRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Doctor Calendar not found with id "+id));
        existingDoctorCalendar.setActive(false);
        log.info("Doctor Calendar with id {} successfully deactivated(soft delete)",id);
    }

    @Override
    public List<DoctorCalendarResponseDto> findByDoctorIdAndIsActiveTrue(Long doctorId) {
        List<DoctorCalendar>doctorCalendarList=doctorCalendarRepository.findByDoctorIdAndIsActiveTrue(doctorId);

        return doctorCalendarList.stream()
                .map(DoctorCalendarResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<DoctorCalendarResponseDto> findByDoctorId(Long doctorId) {
        List<DoctorCalendar>doctorCalendarList=doctorCalendarRepository.findByDoctorId(doctorId);

        return doctorCalendarList.stream()
                .map(DoctorCalendarResponseDto::fromEntity)
                .toList();
    }

    @Override
    public boolean existsByDoctorIdAndNameAndIsActiveTrue(Long doctorId, String name) {
        return doctorCalendarRepository.existsByDoctorIdAndNameAndIsActiveTrue(doctorId,name);
    }

    @Override
    public boolean existsByDoctorIdAndNameExcludingId(Long doctorId, String name, Long excludeId) {
        return doctorCalendarRepository.existsByDoctorIdAndNameExcludingId(doctorId,name,excludeId);
    }

    @Override
    public boolean existsById(Long calendarId) {
        return doctorCalendarRepository.existsById(calendarId);
    }

    private boolean doctorExistsById(Long doctorId){
        //verify if doctor id exists- comunicacion con otro microservicio
        /*if(){
            throw new IllegalArgumentException("Doctor with id "+dto.getDoctorId()+" does not exist.");
        }

         */
        return true;
    }
}
