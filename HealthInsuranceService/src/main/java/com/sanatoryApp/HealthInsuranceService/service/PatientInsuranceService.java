package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.PatientInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.repository.IPatientInsuranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class PatientInsuranceService implements IPatientInsuranceService{

    private final IPatientInsuranceRepository patientInsuranceRepository;

    @Override
    public PatientInsuranceResponseDto findPatientInsuranceById(Long id) {
        log.debug("Attempting to find Patient Insurance by Id: {}",id);
        PatientInsurance patientInsurance=patientInsuranceRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Patient Insurance not found with id: "+id));
        return PatientInsuranceResponseDto.fromEntity(patientInsurance);
    }

    @Override
    public PatientInsuranceResponseDto createPatientInsurance(PatientInsuranceCreateDto dto) {
        return null;
    }

    //The only field that could be updated is Coverage Plan
    @Override
    public PatientInsuranceResponseDto updatePatientInsuranceById(Long id, Long coveragePlanId) {
        log.debug("Attempting to update the plan for the patient with the ID: {}",id);
        PatientInsurance existingPatientInsurance =patientInsuranceRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Patient Insurance not found with id: "+id));

        //verificar si el coveragePlan id existe - comunicacion entre microservicios

        log.info("Updating the patient's coverage plan to the new plan with the ID: {}",coveragePlanId);
        existingPatientInsurance.setCoveragePlanId(coveragePlanId);

        PatientInsurance saved=patientInsuranceRepository.save(existingPatientInsurance);
        return PatientInsuranceResponseDto.fromEntity(saved);
    }

    @Override
    public void deletePatientInsuranceById(Long id) {
        log.debug("Attempting to delete Patient Insurance with id {}",id);
        PatientInsurance patientInsurance=patientInsuranceRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Patient Insurance not found with id: "+id));
        patientInsuranceRepository.delete(patientInsurance);
        log.info("Patient Insurance with id {} successfully deleted",id);

    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientInsuranceByPatientId(Long id) {
        log.debug("Attempting to find Patient Insurance wit patient id: {}",id);
        List<PatientInsurance> patientInsuranceList=patientInsuranceRepository.findByPatientId(id);
                if(patientInsuranceList.isEmpty())
                {
                    throw new ResourceNotFound("Patients Insurance not found with patient Id "+id);}
        return patientInsuranceList.stream()
                .map(PatientInsuranceResponseDto::fromEntity)
                .toList();
    }

    @Override
    public PatientInsuranceResponseDto findPatientInsuranceByCredentialNumber(String id) {
        log.debug("Attempting to find Patient Insurance by credential number {}",id);
        PatientInsurance patientInsurance=patientInsuranceRepository.findByCredentialNumber(id)
                .orElseThrow(()->new ResourceNotFound("Patient Insurance not found with credential number: "+id));
        return PatientInsuranceResponseDto.fromEntity(patientInsurance);
    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientInsuranceByHealthInsurance(Long id) {
        List<PatientInsurance>patientInsuranceList=patientInsuranceRepository.findByHealthInsuranceId(id);
                if(patientInsuranceList.isEmpty()){
                     throw new ResourceNotFound("No Patients Insurance found with health insurance id "+id);
                }
        return patientInsuranceList.stream()
                .map(PatientInsuranceResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientInsuranceByCoveragePlanId(Long id) {
        List<PatientInsurance>patientInsuranceList=patientInsuranceRepository.findByCoveragePlanId(id);
        if(patientInsuranceList.isEmpty()){
            throw new ResourceNotFound("No Patients Insurance found with coverage plan id "+id);
        }
        return patientInsuranceList.stream()
                .map(PatientInsuranceResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientInsuranceByCreatedAt(LocalDate date) {
        List<PatientInsurance>patientInsuranceList=patientInsuranceRepository.findByCreatedAt(date);
        if(patientInsuranceList.isEmpty()){
            throw new ResourceNotFound("No Patients Insurance found created at "+date);
        }
        return patientInsuranceList.stream()
                .map(PatientInsuranceResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientInsuranceByCreatedAfterDate(LocalDate date) {
        List<PatientInsurance>patientInsuranceList=patientInsuranceRepository.findByCreatedAtAfterDate(date);
                if(patientInsuranceList.isEmpty()){
                    throw new ResourceNotFound("No Patients Insurance found created  after  "+date);
                }
        return patientInsuranceList.stream()
                .map(PatientInsuranceResponseDto::fromEntity)
                .toList();
    }
}
