package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.HealthInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;

import java.util.List;

public interface IHealthInsuranceService {

    // CRUD Operations

    HealthInsurance getHealthInsuranceById(Long id);
    HealthInsuranceResponseDto findHealthInsuranceById(Long id);

    HealthInsuranceResponseDto createHealthInsurance(HealthInsuranceCreateDto dto);

    HealthInsuranceResponseDto updateHealthInsuranceById(Long id, HealthInsuranceUpdateDto dto);

    void deleteHealthInsuranceById(Long id);

    void softDeleteHealthInsuranceById(Long id);

    void activateHealthInsuranceById(Long id);

    // Search Operations
    HealthInsuranceResponseDto findHealthInsuranceByCompanyName(String companyName);

    HealthInsuranceResponseDto findHealthInsuranceByCompanyCode(Long companyCode);

    HealthInsuranceResponseDto findHealthInsuranceByPhoneNumber(String phoneNumber);

    HealthInsuranceResponseDto findHealthInsuranceByEmail(String email);

    List<HealthInsuranceResponseDto> searchByName(String name);


    // Existence Verification Methods - use primitive boolean
    boolean existsByCompanyName(String companyName);

    boolean existsByCompanyCode(Long companyCode);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);




}