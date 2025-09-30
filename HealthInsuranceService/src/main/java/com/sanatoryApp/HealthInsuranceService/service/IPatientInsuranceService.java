package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.PatientInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IPatientInsuranceService {

    /* BASIC CRUD */
    PatientInsuranceResponseDto findPatientInsuranceById(Long id);
    PatientInsuranceResponseDto createPatientInsurance(PatientInsuranceCreateDto dto);
    PatientInsuranceResponseDto updatePatientInsuranceById(Long id, Long coveragePlanId);
    void deletePatientInsuranceById(Long id);

    List<PatientInsuranceResponseDto> findPatientInsuranceByPatientId(Long id);
    PatientInsuranceResponseDto findPatientInsuranceByCredentialNumber(String id);
    List<PatientInsuranceResponseDto> findPatientInsuranceByHealthInsurance(Long id);
    List<PatientInsuranceResponseDto> findPatientInsuranceByCoveragePlanId(Long id);
    List<PatientInsuranceResponseDto> findPatientInsuranceByCreatedAt(LocalDate date);
    List<PatientInsuranceResponseDto> findPatientInsuranceByCreatedAfterDate(LocalDate date);


}
