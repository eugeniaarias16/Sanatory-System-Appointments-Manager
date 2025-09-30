package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.DoctorCreateDto;
import com.sanatoryApp.UserService.dto.Response.DoctorResponseDto;
import com.sanatoryApp.UserService.entity.Doctor;
import com.sanatoryApp.UserService.exception.DuplicateResourceException;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.repository.IDoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DoctorService implements IDoctorService{

    private final IDoctorRepository doctorRepository;

    @Override
    public DoctorResponseDto findDoctorById(Long id) {
        log.debug("Attempting to find doctor by Id: {}",id);
        Doctor doctor=doctorRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Doctor not found with id: "+id));
        return DoctorResponseDto.fromEntity(doctor);

    }
    @Transactional
    @Override
    public DoctorResponseDto updateDoctorById(Long id, Map<String, Object> updates) {
        log.debug("Attempting to update doctor by Id: {}",id);
        Doctor existingDoctor=doctorRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Doctor not found with id: "+id));

        updates.forEach((key,value)->{
            switch (key){
                case "firstName"->existingDoctor.setFirstName((String) value);
                case "lastName"->existingDoctor.setLastName((String) value);
                case "email"->{
                    String email=(String) value;
                    if(existByEmail(email)){
                        throw new DuplicateResourceException("Doctor already exists with email: "+email);
                    }
                    existingDoctor.setEmail(email);
                }
                case "phoneNumber"->{
                    String phoneNumber=(String) value;
                    if(existByPhoneNumber(phoneNumber)){
                        throw new DuplicateResourceException("Doctor already exists with phone number: "+phoneNumber);
                    }
                    existingDoctor.setPhoneNumber(phoneNumber);
                }
            }
        });
        Doctor saved=doctorRepository.save(existingDoctor);
        log.info("Doctor with id {} successfully updated with values: {}",id,updates.values());
        return DoctorResponseDto.fromEntity(saved);
    }
    @Transactional
    @Override
    public DoctorResponseDto createDoctor(DoctorCreateDto dto) {
        log.debug("Attempting to create new Doctor with values: {}",dto);
        if(existByEmail(dto.getEmail())){
            throw new DuplicateResourceException("Doctor already exists with email: "+dto.getEmail());
        }
        if(existByPhoneNumber(dto.getPhoneNumber())){
            throw new DuplicateResourceException("Doctor already exists with phone number: "+dto.getPhoneNumber());
        }

        Doctor doctor=dto.toEntity();
        Doctor saved=doctorRepository.save(doctor);
        log.info("Doctor with id {} successfully created.",saved.getId());
        return DoctorResponseDto.fromEntity(doctor);
    }
    @Transactional
    @Override
    public void deleteDoctorById(Long id) {
        log.debug("Attempting to delete Doctor with id {}.",id);
        Doctor existingDoctor=doctorRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Doctor not found with id: "+id));
        doctorRepository.delete(existingDoctor);
        log.info("Doctor with id {} successfully deleted.",id);
    }

    @Override
    public List<DoctorResponseDto> findDoctorByFirstName(String firstName) {
        log.debug("Attempting to find doctor by First Name: {}",firstName);
        List<Doctor> doctorList=doctorRepository.findDoctorByFirstNameIgnoreCase(firstName)
                .orElseThrow(()-> new ResourceNotFound("No Doctor was found with first name: "+firstName));
        return doctorList.stream()
                .map(DoctorResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<DoctorResponseDto> findDoctorByLastName(String lastName) {
        log.debug("Attempting to find doctor by Last Name: {}",lastName);
        List<Doctor>doctorList=doctorRepository.findDoctorByLastNameIgnoreCase(lastName)
                .orElseThrow(()->new ResourceNotFound("No doctor was found with last name: "+lastName));
        return doctorList.stream()
                .map(DoctorResponseDto::fromEntity)
                .toList();
    }

    @Override
    public boolean existByEmail(String email) {
        log.debug("Verifying if Doctor with email {} exists.",email);
        return doctorRepository.existByEmail(email);
    }

    @Override
    public boolean existByPhoneNumber(String phoneNumber) {
        log.debug("Verifying id Doctor with phoneNumber {} exists.",phoneNumber);
        return doctorRepository.existByPhoneNumber(phoneNumber);
    }
}
