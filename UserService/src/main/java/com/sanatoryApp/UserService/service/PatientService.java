package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.PatientCreateDto;
import com.sanatoryApp.UserService.dto.Response.PatientResponseDto;
import com.sanatoryApp.UserService.entity.Patient;
import com.sanatoryApp.UserService.exception.DuplicateResourceException;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.repository.IPatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PatientService implements IPatientService{
   private final IPatientRepository patientRepository;
    @Override
    public PatientResponseDto findPatientById(Long id) {
        Patient patient=patientRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Patient not found found with id: "+id));
        return PatientResponseDto.fromEntity(patient);
    }

    @Transactional
    @Override
    public PatientResponseDto updatePatientById(Long id, Map<String, Object> updates) {
        log.debug("Verifying if patient with id {} exists...",id);
        Patient existingPatient=patientRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Patient not found found with id: "+id));
        log.debug("Updating patient with id {}...",id);
        updates.forEach((key,value)->{
            switch (key){
                case"dni"->{
                    String dni=(String) value;
                    if(existByDni(dni)){
                        throw new DuplicateResourceException("Patient already exit with dni: "+dni);
                    }
                    existingPatient.setDni(dni);
                }
                case "firstName"->existingPatient.setFirstName((String) value);
                case "lastName"->existingPatient.setLastName((String)value);
                case "email"->{
                    String email=(String) value;
                    if(existsByEmail(email)){
                        throw new DuplicateResourceException("Patient already exists with email: "+email);
                    }
                existingPatient.setEmail(email);
                }
                case "phoneNumber"->{
                    String phoneNumber=(String) value;
                    if (existsByPhoneNumber(phoneNumber)){
                        throw new DuplicateResourceException("Patient already exists with phone  number: "+phoneNumber);
                    }
                    existingPatient.setPhoneNumber(phoneNumber);
                }
            }
        });
        Patient saved=patientRepository.save(existingPatient);
        log.info("Patient with id {} successfully updated.",id);
        return PatientResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public PatientResponseDto createPatient(PatientCreateDto dto) {
        String email=dto.getEmail();
        if(existsByEmail(email)){
            throw new DuplicateResourceException("Patient already exists with email: "+email);
        }
        String phoneNumber= dto.getPhoneNumber();
        if (existsByPhoneNumber(phoneNumber)){
            throw new DuplicateResourceException("Patient already exists with phone  number: "+phoneNumber);
        }
        String dni=dto.getDni();
        if(existByDni(dni)){
            throw new DuplicateResourceException("Patient already exit with dni: "+dni);
        }
        log.debug("Creating new patient...");
        Patient patient=dto.toEntity();
        Patient saved=patientRepository.save(patient);
        log.info("New Patient with id {} successfully created",saved.getId());
        return PatientResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public void deletePatientById(Long id) {
        Patient existingPatient=patientRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Patient not found found with id: "+id));
        log.info("Deleting patient with id {}...",id);
        patientRepository.delete(existingPatient);
    }

    @Override
    public PatientResponseDto findPatientByDni(String dni) {
        log.debug("Searching patient by dni {}...",dni);
        Patient patient=patientRepository.findPatientByDni(dni)
                .orElseThrow(()->new ResourceNotFound("Patient not found with dni: "+dni));
        return PatientResponseDto.fromEntity(patient);
    }

    @Override
    public PatientResponseDto findPatientByEmail(String email) {
        log.debug("Searching patient by email{}...",email);
        Patient patient=patientRepository.findPatientByEmail(email)
                .orElseThrow(()->new ResourceNotFound("Patient not found with email: "+email));

        return PatientResponseDto.fromEntity(patient);
    }

    @Override
    public boolean existByDni(String dni) {
        log.debug("Verifying if Patient with dni {} exists...",dni);
        return patientRepository.existByDni(dni);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Verifying if Patient with email {} exists...",email);
        return patientRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        log.debug("Verifying if Patient with phoneNumber {} exists.",phoneNumber);
        return patientRepository.existsByPhoneNumber(phoneNumber);
    }
}
