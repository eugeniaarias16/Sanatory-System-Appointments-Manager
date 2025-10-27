package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.PatientInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;


public interface IPatientInsuranceService {

    /* BASIC CRUD */
    PatientInsuranceResponseDto findPatientInsuranceById(Long id);
    PatientInsuranceResponseDto createPatientInsurance(PatientInsuranceCreateDto dto);
    PatientInsuranceResponseDto updatePatientInsuranceCoveragePlanById(Long id, Long coveragePlanId);

    void deletePatientInsuranceById(Long id);
    void softDeletePatientInsuranceById(Long id);
    void activatePatientInsuranceById(Long id);

    Integer countActivePatientsByInsuranceId(Long insuranceId);

    List<PatientInsuranceResponseDto> findPatientInsuranceByPatientDni(Long dni);
    PatientInsuranceResponseDto findPatientInsuranceByCredentialNumber(String id);
    List<PatientInsuranceResponseDto> findPatientInsuranceByHealthInsurance(Long id);
    List<PatientInsuranceResponseDto> findPatientInsuranceByCoveragePlanId(Long id);
    List<PatientInsuranceResponseDto> findPatientInsuranceByCreatedAt(LocalDate date);
    List<PatientInsuranceResponseDto> findPatientInsuranceByCreatedAfterDate(LocalDate date);

    Boolean existByCredentialNumber(String credentialNumber);

}
