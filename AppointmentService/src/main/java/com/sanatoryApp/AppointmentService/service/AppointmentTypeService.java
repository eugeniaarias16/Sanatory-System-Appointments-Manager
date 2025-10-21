package com.sanatoryApp.AppointmentService.service;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeCreateDto;
import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeUpdateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentTypeResponseDto;
import com.sanatoryApp.AppointmentService.entity.AppointmentType;
import com.sanatoryApp.AppointmentService.exception.ResourceNotFound;
import com.sanatoryApp.AppointmentService.repository.IAppointmentTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AppointmentTypeService implements IAppointmentTypeService{

    private final IAppointmentTypeRepository appointmentTypeRepository;

    @Override
    public AppointmentTypeResponseDto findAppointmentTypeById(Long id) {
        AppointmentType appointmentType= appointmentTypeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Appointment Type not found with id "+id));
        return AppointmentTypeResponseDto.fromEntity(appointmentType);
    }

    @Override
    public AppointmentTypeResponseDto createAppointmentType(AppointmentTypeCreateDto dto) {
        log.debug("Attempting to create new Appointment Type with name {}",dto.getName());

        //verify if already exist Appointment Type with that name
        if(existsAppointmentTypeByName(dto.getName())){
            throw new IllegalArgumentException("Appointment Type already exist with name "+dto.getName());
        }
        //verify duration time
        validateDurationTime(dto.getDurationMin());

        //validate buffer time
        validateBufferTime(dto.getBufferTimeMin());

        //validate base price
        validateBasePrice(dto.getBasePrice());


        AppointmentType appointmentType=dto.toEntity();
        AppointmentType saved=appointmentTypeRepository.save(appointmentType);
        log.info("Appointment Type with successfully created with id {}",saved.getId());
        return AppointmentTypeResponseDto.fromEntity(saved);
    }

    @Override
    public AppointmentTypeResponseDto updateAppointmentType(Long id, AppointmentTypeUpdateDto dto) {
        log.debug("Attempting to update Appointment Type with id {}",id);
        AppointmentType existingAppointmentType= appointmentTypeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Appointment Type not found with id "+id));

        if (dto.getName() != null) {
            if (!existingAppointmentType.getName().equals(dto.getName())) {
                if (existsAppointmentTypeByName(dto.getName())) {
                    throw new IllegalArgumentException(
                            "Appointment Type already exists with name: " + dto.getName()
                    );
                }
            }
            existingAppointmentType.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            existingAppointmentType.setDescription(dto.getDescription());
        }

        if (dto.getDurationMin() != null) {
            validateDurationTime(dto.getDurationMin());
            existingAppointmentType.setDurationMin(dto.getDurationMin());
        }

        if (dto.getBufferTimeMin() != null) {
            validateBufferTime(dto.getBufferTimeMin());
            existingAppointmentType.setBufferTimeMin(dto.getBufferTimeMin());
        }

        if (dto.getBasePrice() != null) {
            validateBasePrice(dto.getBasePrice());
            existingAppointmentType.setBasePrice(dto.getBasePrice());
        }

        AppointmentType saved=appointmentTypeRepository.save(existingAppointmentType);
        log.info("Appointment Type with id {} successfully updated",id);
        return AppointmentTypeResponseDto.fromEntity(saved);
    }

    @Override
    public void deleteAppointmentType(Long id) {
        log.debug("Attempting to delete Appointment Type with id {}",id);
        AppointmentType existingAppointmentType= appointmentTypeRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Appointment Type not found with id "+id));
        existingAppointmentType.setActive(false);
        log.info("Appointment Type with id {} successfully deactivated (soft deleted)",id);
        appointmentTypeRepository.save(existingAppointmentType);
    }

    @Override
    public AppointmentTypeResponseDto findAppointmentTypeByName(String name) {
        log.debug("Attempting to find Appointment Type by Name: {}",name);
        AppointmentType appointmentType=appointmentTypeRepository.findByName(name)
                .orElseThrow(()-> new ResourceNotFound("Appointment Type not found with name "+name));
        log.info("Appointment Type successfully found with name {} ",name);
        return AppointmentTypeResponseDto.fromEntity(appointmentType);
    }

    @Override
    public List<AppointmentTypeResponseDto> findAppointmentTypeByLikeName(String name) {
        log.debug("Attempting to find Appointment Type by Name containing the word : {}",name);
        if(name==null || name.isEmpty()){
            log.warn("Empty search term provided");
            return Collections.emptyList();
        }
        List<AppointmentType>appointmentTypeList=appointmentTypeRepository.findByLikeName(name);
        return appointmentTypeList.stream()
                .map(AppointmentTypeResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<AppointmentTypeResponseDto> findAppointmentTypeByRangeBasePrice(BigDecimal minPrice, BigDecimal maxPrice) {
        log.debug("Attempting to find Appointment Type By Range Base Price between values min:{} - max:{}",minPrice,maxPrice);

        if(minPrice==null || maxPrice==null){
            throw new IllegalArgumentException("Price range cannot contain null values");
        }
        if(minPrice.compareTo(new BigDecimal("1"))<0){
            throw new IllegalArgumentException("Min Price must be at least 1");
        }
        if(minPrice.compareTo(maxPrice)>0){
            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price. " +
                            "Min: " + minPrice + ", Max: " + maxPrice
            );
        }

        List<AppointmentType>appointmentTypeList=appointmentTypeRepository.findByRangeBasePrice(minPrice,maxPrice);
        return appointmentTypeList.stream()
                .map(AppointmentTypeResponseDto::fromEntity)
                .toList();
    }

    @Override
    public boolean existsAppointmentTypeByName(String name) {
        return appointmentTypeRepository.existsByNameIgnoreCase(name);
    }

    private void validateDurationTime(int durationMin){
        //verify that appointment's duration is at least 15 minutes
        if(durationMin<15){
            throw new IllegalArgumentException("Appointments cannot last less than 15 minutes.");
        }

    }
    private void validateBufferTime(int durationMin){
        //verify that buffer time is positive
        if(durationMin<0){
            throw new IllegalArgumentException("Buffer Time must be positive.");
        }

    }
    private void validateBasePrice(BigDecimal price){
        //verify that price is positive
        if (price.compareTo(new BigDecimal("1")) < 0) {
            throw new IllegalArgumentException("Price must be at least 1");
        }

    }

}
