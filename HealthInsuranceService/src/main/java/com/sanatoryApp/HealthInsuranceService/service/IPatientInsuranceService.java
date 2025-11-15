package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.PatientInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceCreateResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IPatientInsuranceService {

    PatientInsurance getPatientInsuranceById(Long id);
    PatientInsuranceResponseDto findPatientInsuranceById(Long id);
    PatientInsuranceCreateResponseDto createPatientInsurance(PatientInsuranceCreateDto dto);
    PatientInsuranceResponseDto updatePatientInsuranceCoveragePlanById(Long id, Long coveragePlanId);
    void deletePatientInsuranceById(Long id);
    void softDeletePatientInsuranceById(Long id);
    void activatePatientInsuranceById(Long id);

    Integer countActivePatientsByInsuranceId(Long insuranceId);

    List<PatientInsuranceResponseDto> findPatientInsuranceByPatientDni(String dni);
    PatientInsuranceResponseDto findPatientInsuranceByCredentialNumber(String id);
    List<PatientInsuranceResponseDto> findPatientInsuranceByHealthInsurance(Long id);
    List<PatientInsuranceResponseDto> findPatientInsuranceByCoveragePlanId(Long id);
    List<PatientInsuranceResponseDto> findPatientInsuranceByCreatedAt(LocalDate date);
    List<PatientInsuranceResponseDto> findPatientInsuranceByCreatedAfterDate(LocalDate date);

    Boolean existsByCredentialNumber(String credentialNumber);


}