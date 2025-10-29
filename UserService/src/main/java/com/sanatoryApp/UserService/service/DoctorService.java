package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.DoctorCreateDto;
import com.sanatoryApp.UserService.dto.Request.DoctorUpdateDto;
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
    public List<DoctorResponseDto> findAll() {
        List<Doctor>list=doctorRepository.findAll();
        return list.stream()
                .map(DoctorResponseDto::fromEntity)
                .toList();
    }

    @Override
    public DoctorResponseDto findDoctorById(Long id) {
        log.debug("Attempting to find doctor by Id: {}",id);
        Doctor doctor=doctorRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Doctor not found with id: "+id));
        return DoctorResponseDto.fromEntity(doctor);

    }

    @Transactional
    @Override
    public DoctorResponseDto updateDoctorById(Long id, DoctorUpdateDto dto) {
        log.debug("Updating doctor with id: {}", id);

        //Search for existing doctor
        Doctor existingDoctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Doctor not found with id: " + id));

        // Update firstName
        if (dto.firstName() != null && !dto.firstName().trim().isEmpty()) {
            existingDoctor.setFirstName(dto.firstName().trim());
        }

        // Update lastName
        if (dto.lastName() != null && !dto.lastName().trim().isEmpty()) {
            existingDoctor.setLastName(dto.lastName().trim());
        }

        // Update email (validate duplicates only if changed)
        if (dto.email() != null && !dto.email().trim().isEmpty()) {
            String newEmail = dto.email().trim().toLowerCase();

            // Only validate if the email is different from the current one
            if (!existingDoctor.getEmail().equalsIgnoreCase(newEmail)) {
                if (existsByEmail(newEmail)) {
                    log.error("Duplicate email attempt: {}", newEmail);
                    throw new DuplicateResourceException(
                            "Doctor already exists with email: " + newEmail
                    );
                }
                existingDoctor.setEmail(newEmail);
            }
        }

        // Update phoneNumber (validate duplicates only if changed)
        if (dto.phoneNumber() != null && !dto.phoneNumber().trim().isEmpty()) {
            String newPhone = dto.phoneNumber().trim();

            // Only validate if the phone number is different from the current one.
            if (!existingDoctor.getPhoneNumber().equals(newPhone)) {
                if (existsByPhoneNumber(newPhone)) {
                    log.error("Duplicate phone number attempt: {}", newPhone);
                    throw new DuplicateResourceException(
                            "Doctor already exists with phone number: " + newPhone
                    );
                }
                existingDoctor.setPhoneNumber(newPhone);
            }
        }

        // Save changes
        Doctor saved = doctorRepository.save(existingDoctor);
        log.info("Doctor with id {} successfully updated", id);

        return DoctorResponseDto.fromEntity(saved);
    }



    @Transactional
    @Override
    public DoctorResponseDto createDoctor(DoctorCreateDto dto) {
        log.debug("Attempting to create new Doctor with values: {}",dto);
        if(existsByEmail(dto.getEmail())){
            throw new DuplicateResourceException("Doctor already exists with email: "+dto.getEmail());
        }
        if(existsByPhoneNumber(dto.getPhoneNumber())){
            throw new DuplicateResourceException("Doctor already exists with phone number: "+dto.getPhoneNumber());
        }

        Doctor doctor=dto.toEntity();
        Doctor saved=doctorRepository.save(doctor);
        log.info("Doctor with id {} successfully created.",saved.getId());
        return DoctorResponseDto.fromEntity(saved);
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
        List<Doctor> doctorList=doctorRepository.findDoctorByFirstNameIgnoreCase(firstName);
        if(doctorList.isEmpty()){
            log.info("No Doctor was found with first name: {} ",firstName);
        }
        return doctorList.stream()
                .map(DoctorResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<DoctorResponseDto> findDoctorByLastName(String lastName) {
        log.debug("Attempting to find doctor by Last Name: {}",lastName);
        List<Doctor>doctorList=doctorRepository.findDoctorByLastNameIgnoreCase(lastName);
        if(doctorList.isEmpty()){
            log.info("No Doctor was found with last name: {} ",lastName);
        }
        return doctorList.stream()
                .map(DoctorResponseDto::fromEntity)
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Verifying if Doctor with email {} exists.",email);
        return doctorRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        log.debug("Verifying id Doctor with phoneNumber {} exists.",phoneNumber);
        return doctorRepository.existsByPhoneNumber(phoneNumber);
    }
}