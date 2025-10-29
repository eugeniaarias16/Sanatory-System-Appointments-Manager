package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarUpdateDto;
import com.sanatoryApp.CalendarService.dto.Request.externalService.DoctorDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarCreateResponseDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarResponseDto;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.repository.IDoctorCalendarRepository;
import com.sanatoryApp.CalendarService.repository.UserServiceApi;
import com.sanatoryApp.CalendarService.utils.TimeZoneValidator;
import feign.FeignException;
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
public class DoctorCalendarService implements IDoctorCalendarService {

    private final IDoctorCalendarRepository doctorCalendarRepository;
    private final UserServiceApi userServiceApi;

    @Override
    @Transactional(readOnly = true)
    public DoctorCalendarResponseDto findDoctorCalendarById(Long id) {
        DoctorCalendar doctorCalendar = doctorCalendarRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFound("Doctor Calendar not found with id " + id));
        return DoctorCalendarResponseDto.fromEntity(doctorCalendar);
    }

    @Override
    public DoctorCalendarCreateResponseDto createDoctorCalendar(DoctorCalendarCreateDto dto) {
        log.debug("Attempting to create a new Doctor Calendar");
        log.debug("Verifying if doctor with id {} exists.", dto.getDoctorId());

        DoctorDto doctorDto = findDoctorById(dto.getDoctorId());

        String calendarName = dto.getName().trim().toLowerCase();

        if (existsByDoctorIdAndNameAndIsActiveTrue(dto.getDoctorId(), calendarName)) {
            throw new IllegalArgumentException(
                    "Doctor with id " + dto.getDoctorId() +
                            " already has an active calendar with name: " + calendarName
            );
        }

        TimeZoneValidator.validateTimeZone(dto.getTimeZone());

        log.debug("Creating new Doctor Calendar for the doctor with the ID {}", dto.getDoctorId());

        DoctorCalendar doctorCalendar = dto.toEntity();
        doctorCalendar.setName(calendarName);

        DoctorCalendar saved = doctorCalendarRepository.save(doctorCalendar);

        log.info("The new Doctor Calendar {} was successfully created for the doctor with id {} and timezone {}",
                dto.getName(), dto.getDoctorId(), dto.getTimeZone());

        return DoctorCalendarCreateResponseDto.fromEntities(saved, doctorDto);
    }

    @Override
    public DoctorCalendarResponseDto updateDoctorCalendar(Long id, DoctorCalendarUpdateDto dto) {
        DoctorCalendar existingDoctorCalendar = doctorCalendarRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFound("Doctor Calendar not found with id " + id));

        if (dto.doctorId() != null) {
            DoctorDto doctorDto = findDoctorById(dto.doctorId());
            existingDoctorCalendar.setDoctorId(doctorDto.id());
        }

        if (dto.name() != null && !dto.name().trim().isEmpty()) {
            String newName = dto.name().trim().toLowerCase();

            if (!existingDoctorCalendar.getName().equals(newName)) {
                if (existsByDoctorIdAndNameExcludingId(
                        existingDoctorCalendar.getDoctorId(),
                        newName,
                        id)) {
                    throw new IllegalArgumentException(
                            "Doctor already has another active calendar with name: " + newName
                    );
                }
                existingDoctorCalendar.setName(newName);
            }
        }

        if (dto.timeZone() != null) {
            TimeZoneValidator.validateTimeZone(dto.timeZone());
            existingDoctorCalendar.setTimeZone(ZoneId.of(dto.timeZone()));
        }

        DoctorCalendar saved = doctorCalendarRepository.save(existingDoctorCalendar);

        log.info("Doctor Calendar with id {} successfully updated - doctorId: {}, name: {}, timeZone: {}",
                saved.getId(), saved.getDoctorId(), saved.getName(), saved.getTimeZone());

        return DoctorCalendarResponseDto.fromEntity(saved);
    }

    @Override
    public void deleteDoctorCalendar(Long id) {
        log.debug("Attempting to soft delete Doctor Calendar with id {}", id);

        DoctorCalendar existingDoctorCalendar = doctorCalendarRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFound("Doctor Calendar not found with id " + id));

        existingDoctorCalendar.setActive(false);
        doctorCalendarRepository.save(existingDoctorCalendar);

        log.info("Doctor Calendar with id {} successfully deactivated (soft delete)", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorCalendarResponseDto> findByDoctorIdAndIsActiveTrue(Long doctorId) {
        List<DoctorCalendar> doctorCalendarList =
                doctorCalendarRepository.findByDoctorIdAndIsActiveTrue(doctorId);

        return doctorCalendarList.stream()
                .map(DoctorCalendarResponseDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorCalendarResponseDto> findByDoctorId(Long doctorId) {
        List<DoctorCalendar> doctorCalendarList = doctorCalendarRepository.findByDoctorId(doctorId);

        return doctorCalendarList.stream()
                .map(DoctorCalendarResponseDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorCalendarResponseDto findByDoctorIdAndName(Long doctorId, String name) {
        String normalizedName = name.trim().toLowerCase();

        DoctorCalendar doctorCalendar = doctorCalendarRepository
                .findByDoctorIdAndName(doctorId, normalizedName)
                .orElseThrow(() -> new ResourceNotFound(
                        "Doctor Calendar not found with doctor id: " + doctorId +
                                " and name: " + normalizedName));

        return DoctorCalendarResponseDto.fromEntity(doctorCalendar);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDoctorIdAndNameAndIsActiveTrue(Long doctorId, String name) {
        return doctorCalendarRepository.existsByDoctorIdAndNameAndIsActiveTrue(
                doctorId,
                name.trim().toLowerCase()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDoctorIdAndNameExcludingId(Long doctorId, String name, Long excludeId) {
        return doctorCalendarRepository.existsByDoctorIdAndNameExcludingId(
                doctorId,
                name.trim().toLowerCase(),
                excludeId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long calendarId) {
        return doctorCalendarRepository.existsById(calendarId);
    }

    private DoctorDto findDoctorById(Long doctorId) {
        try {
            return userServiceApi.getDoctorById(doctorId);
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFound("Doctor with id: " + doctorId + " not found");
        } catch (FeignException e) {
            throw new RuntimeException("Error calling User Service: " + e.getMessage(), e);
        }
    }
}