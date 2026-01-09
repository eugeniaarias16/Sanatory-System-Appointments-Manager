package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.PatientInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.externalService.PatientDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceCreateResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.repository.IPatientInsuranceRepository;
import com.sanatoryApp.HealthInsuranceService.repository.UserServiceApi;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor

public class PatientInsuranceService implements IPatientInsuranceService {

    private final IPatientInsuranceRepository patientInsuranceRepository;
    private final IHealthInsuranceService healthInsuranceService;
    private final ICoveragePlanService coveragePlanService;
    private final UserServiceApi userServiceApi;


    @Override
    public PatientInsurance getPatientInsuranceById(Long id) {
        return patientInsuranceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Patient Insurance not found with id: " + id));
    }

    @Override
    public PatientInsuranceResponseDto findPatientInsuranceById(Long id) {
        log.debug("Attempting to find Patient Insurance by Id: {}", id);
        PatientInsurance patientInsurance = getPatientInsuranceById(id);
        return PatientInsuranceResponseDto.fromEntity(patientInsurance);
    }

    @Transactional
    @Override
    public PatientInsuranceCreateResponseDto createPatientInsurance(PatientInsuranceCreateDto dto) {
        log.debug("Attempting to create Patient Insurance with values: {}", dto);

        PatientDto patientDto;
        try {
            patientDto = userServiceApi.getPatientByDni(dto.patientDni());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFound("Patient with DNI: " + dto.patientDni() + " not found.");
        } catch (FeignException e) {
            throw new RuntimeException("Error communicating with User Service: " + e.getMessage(), e);
        }

        HealthInsurance healthInsurance=healthInsuranceService.getHealthInsuranceById(dto.healthInsuranceId());

        CoveragePlan coveragePlan=coveragePlanService.getCoveragePlanById(dto.coveragePlanId());

        if (!coveragePlanService.existsByIdAndHealthInsuranceId(dto.coveragePlanId(), dto.healthInsuranceId())) {
            throw new IllegalArgumentException("No coverage Plan found with id: " + dto.coveragePlanId() +
                    " and Health Insurance id: " + dto.healthInsuranceId());
        }

        PatientInsurance patientInsurance = dto.toEntity(healthInsurance,coveragePlan);
        PatientInsurance saved = patientInsuranceRepository.save(patientInsurance);
        log.info("Patient Insurance with id {} successfully created", saved.getId());

        return PatientInsuranceCreateResponseDto.fromEntities(saved, patientDto);
    }

    @Transactional
    @Override
    public PatientInsuranceResponseDto updatePatientInsuranceCoveragePlanById(Long id, Long coveragePlanId) {
        log.debug("Attempting to update the plan for the patient with the ID: {}", id);
        PatientInsurance existingPatientInsurance = getPatientInsuranceById(id);

       CoveragePlan coveragePlan=coveragePlanService.getCoveragePlanById(coveragePlanId);

        log.info("Updating the patient's coverage plan to the new plan with the ID: {}", coveragePlanId);
        existingPatientInsurance.setCoveragePlan(coveragePlan);

        PatientInsurance saved = patientInsuranceRepository.save(existingPatientInsurance);
        return PatientInsuranceResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public void deletePatientInsuranceById(Long id) {
        log.debug("Attempting to delete Patient Insurance with id {}", id);
        PatientInsurance patientInsurance = getPatientInsuranceById(id);
        patientInsuranceRepository.delete(patientInsurance);
        log.info("Patient Insurance with id {} successfully deleted", id);
    }

    @Transactional
    @Override
    public void softDeletePatientInsuranceById(Long id) {
        log.debug("Attempting to soft delete Patient Insurance with id {}", id);
        PatientInsurance patientInsurance = getPatientInsuranceById(id);
        patientInsurance.setIsActive(false);
        patientInsuranceRepository.save(patientInsurance);
        log.info("Patient Insurance with id {} successfully deactivated (soft deleted)", id);
    }

    @Transactional
    @Override
    public void activatePatientInsuranceById(Long id) {
        log.debug("Attempting to activate Patient Insurance with id {}", id);
        PatientInsurance patientInsurance = getPatientInsuranceById(id);
        patientInsurance.setIsActive(true);
        patientInsuranceRepository.save(patientInsurance);
        log.info("Patient Insurance with id {} successfully activated.", id);
    }

    @Override
    public Integer countActivePatientsByInsuranceId(Long insuranceId) {
        return patientInsuranceRepository.countActivePatientsByInsuranceId(insuranceId);
    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientInsuranceByPatientDni(String dni) {
        log.debug("Attempting to find Patient Insurance with patient dni: {}", dni);
        List<PatientInsurance> patientInsuranceList = patientInsuranceRepository.findByPatientDni(dni);
        if (patientInsuranceList.isEmpty()) {
            throw new ResourceNotFound("Patients Insurance not found with patient dni " + dni);
        }
        return patientInsuranceList.stream()
                .map(PatientInsuranceResponseDto::fromEntity)
                .toList();
    }

    @Override
    public PatientInsuranceResponseDto findPatientInsuranceByCredentialNumber(String id) {
        log.debug("Attempting to find Patient Insurance by credential number {}", id);
        PatientInsurance patientInsurance = patientInsuranceRepository.findByCredentialNumber(id)
                .orElseThrow(() -> new ResourceNotFound("Patient Insurance not found with credential number: " + id));
        return PatientInsuranceResponseDto.fromEntity(patientInsurance);
    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientInsuranceByHealthInsurance(Long id) {
        List<PatientInsurance> patientInsuranceList = patientInsuranceRepository.findByHealthInsurance_Id(id);
        if (patientInsuranceList.isEmpty()) {
            throw new ResourceNotFound("No Patients Insurance found with health insurance id " + id);
        }
        return patientInsuranceList.stream()
                .map(PatientInsuranceResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientInsuranceByCoveragePlanId(Long id) {
        List<PatientInsurance> patientInsuranceList = patientInsuranceRepository.findByCoveragePlan_Id(id);
        if (patientInsuranceList.isEmpty()) {
            throw new ResourceNotFound("No Patients Insurance found with coverage plan id " + id);
        }
        return patientInsuranceList.stream()
                .map(PatientInsuranceResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientInsuranceByCreatedAt(LocalDate date) {
        List<PatientInsurance> patientInsuranceList = patientInsuranceRepository.findByCreatedAt(date);
        if (patientInsuranceList.isEmpty()) {
            throw new ResourceNotFound("No Patients Insurance found created at " + date);
        }
        return patientInsuranceList.stream()
                .map(PatientInsuranceResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientInsuranceByCreatedAfterDate(LocalDate date) {
        List<PatientInsurance> patientInsuranceList = patientInsuranceRepository.findByCreatedAtAfter(date);
        if (patientInsuranceList.isEmpty()) {
            throw new ResourceNotFound("No Patients Insurance found created after " + date);
        }
        return patientInsuranceList.stream()
                .map(PatientInsuranceResponseDto::fromEntity)
                .toList();
    }

    @Override
    public Boolean existsByCredentialNumber(String credentialNumber) {
        return patientInsuranceRepository.existsByCredentialNumber(credentialNumber);
    }


}