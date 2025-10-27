package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.HealthInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;

import java.util.List;

public interface IHealthInsuranceService {
    /* BASIC CRUD */
    HealthInsuranceResponseDto findHealthInsuranceById(Long id);
    HealthInsuranceResponseDto createHealthInsurance(HealthInsuranceCreateDto dto);
    HealthInsuranceResponseDto updateHealthInsuranceById(Long id, HealthInsuranceUpdateDto dto);
    void deleteHealthInsuranceById(Long id);
    void softDeleteHealthInsuranceById(Long id);

    void activatedHealthInsuranceById(Long id);

    /*  SEARCH  */
    HealthInsuranceResponseDto findHealthInsuranceByCompanyName(String companyName);
    HealthInsuranceResponseDto findHealthInsuranceByCompanyCode(Long companyCode);
    HealthInsuranceResponseDto findHealthInsuranceByPhoneNumber(String phoneNumber);
    HealthInsuranceResponseDto findHealthInsuranceByEmail(String email);
    List<HealthInsuranceResponseDto> searchByName(String name);

    /*  METHODS FOR ACCESSING RELATIONSHIPS  */
    List<CoveragePlanResponseDto> findCoveragePlans(Long insuranceId);
    List<PatientInsuranceResponseDto> findPatientsByInsuranceId(Long insuranceId);


    /* METHODS FOR VERIFYING EXISTING */
    Boolean existsByCompanyName(String companyName);
    Boolean existsByCompanyCode(Long companyCode);
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByEmail(String email);
    boolean existsById(Long id);

    /* STATISTICS */
    Integer countActivePatients(Long insuranceId);
    Integer countActivePlans(Long insuranceId);
}
