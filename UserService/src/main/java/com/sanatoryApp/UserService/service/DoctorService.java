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
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private  final PasswordEncoder passwordEncoder;

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
        if(existsByEmail(dto.email())){
            throw new DuplicateResourceException("Doctor already exists with email: "+dto.email());
        }
        if(existsByPhoneNumber(dto.phoneNumber())){
            throw new DuplicateResourceException("Doctor already exists with phone number: "+dto.phoneNumber());
        }

        if(existsByDni(dto.dni())){
            throw new DuplicateResourceException("Doctor already exists with dni: "+dto.dni());
        }

        String defaultPassword= passwordEncoder.encode(dto.dni());


        Doctor doctor=dto.toEntity();
        doctor.setPassword(defaultPassword);
        Doctor saved=doctorRepository.save(doctor);
        log.info("Doctor with id {} successfully created.",saved.getId());
        return DoctorResponseDto.fromEntity(saved);
    }

    @Override
    public DoctorResponseDto findDoctorByDni(String dni) {
        Doctor doctor=doctorRepository.findDoctorByDni(dni)
                .orElseThrow(()->new ResourceNotFound("Doctor not found with dni: "+dni));
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
    public DoctorResponseDto findByEmail(String email) {
        Doctor doctor=doctorRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFound("Doctor not found with email: "+email));
        return DoctorResponseDto.fromEntity(doctor);
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

    @Override
    public boolean existsByDni(String dni) {
        return doctorRepository.existsByDni(dni);
    }

    @Transactional
    public void disableDoctorByDni(String dni){
        log.debug("Attempting to disabled Doctor with dni {}",dni);
        if (!doctorRepository.existsByDni(dni)) {
            throw new RuntimeException("Doctor with DNI " + dni + " not found");
        }
        doctorRepository.disableDoctorByDni(dni);
        log.info("Doctor {} {} with dni {} successfully disabled.",dni);
    }

    @Transactional
    public void enableDoctorByDni(String dni){
        log.debug("Attempting to enable Doctor with dni {}",dni);
        if (!doctorRepository.existsByDni(dni)) {
            throw new RuntimeException("Doctor with DNI " + dni + " not found");
        }
        doctorRepository.enableDoctorByDni(dni);
        log.info("Doctor {} {} with dni {} successfully enable.",dni);
    }
}