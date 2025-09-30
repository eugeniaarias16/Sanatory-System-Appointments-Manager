package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.HealthInsuranceResponseDto;

import java.util.Map;

public interface IHealthInsuranceService {
    /* BASIC CRUD */
    HealthInsuranceResponseDto findHealthInsuranceById(Long id);
    HealthInsuranceResponseDto createHealthInsurance(HealthInsuranceCreateDto dto);
    HealthInsuranceResponseDto updateHealthInsuranceById(Long id, Map<String,Object>updates);
    void deleteHealthInsuranceById(Long id);


    HealthInsuranceResponseDto findHealthInsuranceByCompanyName(String companyName);
    HealthInsuranceResponseDto findHealthInsuranceByCompanyCode(Long companyCode);
    HealthInsuranceResponseDto findHealthInsuranceByPhoneNumber(String phoneNumber);
    HealthInsuranceResponseDto findHealthInsuranceByEmail(String email);

    Boolean existsByCompanyName(String companyName);
    Boolean existsByCompanyCode(Long companyCode);
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByEmail(String email);


}
