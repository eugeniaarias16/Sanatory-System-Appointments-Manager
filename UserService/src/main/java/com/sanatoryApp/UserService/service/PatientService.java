package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.PatientCreateDto;
import com.sanatoryApp.UserService.dto.Request.PatientUpdateDto;
import com.sanatoryApp.UserService.dto.Response.PatientResponseDto;
import com.sanatoryApp.UserService.entity.Patient;
import com.sanatoryApp.UserService.exception.DuplicateResourceException;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.repository.IPatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PatientService implements IPatientService{
    private final IPatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<PatientResponseDto> findAll() {
        List<Patient>list=patientRepository.findAll();
        return list.stream()
                .map(PatientResponseDto::fromEntity)
                .toList();
    }

    @Override
    public PatientResponseDto findPatientById(Long id) {
        Patient patient=patientRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Patient not found found with id: "+id));
        return PatientResponseDto.fromEntity(patient);
    }

    @Transactional
    @Override
    public PatientResponseDto updatePatientById(Long id, PatientUpdateDto dto) {
        log.debug("Verifying if patient with id {} exists...",id);
        Patient existingPatient=patientRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Patient not found found with id: "+id));
        log.debug("Updating patient with id {}...",id);

        //updating dni
        if(dto.dni()!=null && !dto.dni().isEmpty()){
            String newDni=dto.dni().trim();

            //verify if new dni and old dni are equals
            if(!existingPatient.getDni().equalsIgnoreCase(newDni)){

                //verifying if dni already exists
                if(existsByDni(newDni)){
                    throw new DuplicateResourceException("Already exists Patient with dni: "+newDni);
                }
                existingPatient.setDni(newDni);
            }
        }

        //updating firstname
        if(dto.firstName()!=null && !dto.firstName().isEmpty()){
            existingPatient.setFirstName(dto.firstName().trim());
        }

        //updating lastname
        if(dto.lastName()!=null && !dto.lastName().isEmpty()){
            existingPatient.setLastName(dto.lastName());
        }


        //updating email
        if(dto.email()!=null && !dto.email().isEmpty()){
            String newEmail= dto.email().trim().toLowerCase();

            //verify if new email and old email are equals
            if(!existingPatient.getEmail().equalsIgnoreCase(newEmail)){

                //verifying if new email already exists
                if(existsByEmail(newEmail)){
                    throw new DuplicateResourceException("Already exists Patient with email: "+newEmail);
                }
                //updating email
                existingPatient.setEmail(newEmail);
            }
        }

        //updating phone number
        if(dto.phoneNumber()!=null && !dto.phoneNumber().isEmpty()){
            String newPhoneNumber= dto.phoneNumber().trim();

            //verifying if old phone number and new phone number are equals
            if(!existingPatient.getPhoneNumber().equals(newPhoneNumber)){
                //verifying if new phone number already exists
                if(existsByPhoneNumber(newPhoneNumber)){
                    throw new DuplicateResourceException("Already exists Patient with phone number: "+newPhoneNumber);
                }
                //updating phone number
                existingPatient.setPhoneNumber(newPhoneNumber);
            }
        }

        Patient saved=patientRepository.save(existingPatient);
        log.info("Patient with id {} successfully updated.",id);
        return PatientResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public PatientResponseDto updatePatientByDni(String dni, PatientUpdateDto dto) {
        log.debug("Verifying if patient with dni {} exists...",dni);
        Patient existingPatient=patientRepository.findPatientByDni(dni)
                .orElseThrow(()->new ResourceNotFound("Patient not found found with dni: "+dni));
        log.debug("Updating patient with dni {}...",dni);

        //updating dni
        if(dto.dni()!=null && !dto.dni().isEmpty()){
            String newDni=dto.dni().trim();

            //verify if new dni and old dni are equals
            if(!existingPatient.getDni().equalsIgnoreCase(newDni)){

                //verifying if dni already exists
                if(existsByDni(newDni)){
                    throw new DuplicateResourceException("Already exists Patient with dni: "+newDni);
                }
                existingPatient.setDni(newDni);
            }
        }

        //updating firstname
        if(dto.firstName()!=null && !dto.firstName().isEmpty()){
            existingPatient.setFirstName(dto.firstName().trim());
        }

        //updating lastname
        if(dto.lastName()!=null && !dto.lastName().isEmpty()){
            existingPatient.setLastName(dto.lastName());
        }


        //updating email
        if(dto.email()!=null && !dto.email().isEmpty()){
            String newEmail= dto.email().trim().toLowerCase();

            //verify if new email and old email are equals
            if(!existingPatient.getEmail().equalsIgnoreCase(newEmail)){

                //verifying if new email already exists
                if(existsByEmail(newEmail)){
                    throw new DuplicateResourceException("Already exists Patient with email: "+newEmail);
                }
                //updating email
                existingPatient.setEmail(newEmail);
            }
        }

        //updating phone number
        if(dto.phoneNumber()!=null && !dto.phoneNumber().isEmpty()){
            String newPhoneNumber= dto.phoneNumber().trim();

            //verifying if old phone number and new phone number are equals
            if(!existingPatient.getPhoneNumber().equals(newPhoneNumber)){
                //verifying if new phone number already exists
                if(existsByPhoneNumber(newPhoneNumber)){
                    throw new DuplicateResourceException("Already exists Patient with phone number: "+newPhoneNumber);
                }
                //updating phone number
                existingPatient.setPhoneNumber(newPhoneNumber);
            }
        }

        Patient saved=patientRepository.save(existingPatient);
        log.info("Patient with dni {} successfully updated.",dni);
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
        if(existsByDni(dni)){
            throw new DuplicateResourceException("Patient already exit with dni: "+dni);
        }

        String defaultPassword=passwordEncoder.encode(dni);

        log.debug("Creating new patient...");
        Patient patient=dto.toEntity();
        patient.setPassword(defaultPassword);
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

    @Transactional
    @Override
    public void deletePatientByDni(String dni) {
        Patient existingPatient=patientRepository.findPatientByDni(dni)
                .orElseThrow(()->new ResourceNotFound("Patient not found found with dni: "+dni));
        log.info("Deleting patient with dni {}...",dni);
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
    public boolean existsByDni(String dni) {
        log.debug("Verifying if Patient with dni {} exists...",dni);
        return patientRepository.existsByDni(dni);
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

    @Transactional
    @Override
    public void disablePatientByDni(String dni) {
        log.debug("Attempting to disable patient with DNI {}",dni);
        patientRepository.disablePatientByDni(dni);
        log.debug("Patient successfully disabled patient with DNI {}",dni);

    }

    @Transactional
    @Override
    public void enablePatientByDni(String dni) {
        log.debug("Attempting to enable patient with DNI {}",dni);
        patientRepository.enablePatientByDni(dni);
        log.debug("Patient successfully enabled patient with DNI {}",dni);
    }
}