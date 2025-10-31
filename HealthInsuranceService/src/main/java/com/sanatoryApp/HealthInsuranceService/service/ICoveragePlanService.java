package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;

import java.util.List;

public interface ICoveragePlanService {

    CoveragePlan getCoveragePlanById(Long id);
    CoveragePlanResponseDto findCoveragePlanById(Long id);

    CoveragePlanResponseDto createCoveragePlan(CoveragePlanCreateDto dto);

    CoveragePlanResponseDto updateCoveragePlanById(Long id, CoveragePlanUpdateDto dto);

    void softDeleteCoveragePlanById(Long id);

    void deleteCoveragePlanById(Long id);

    List<CoveragePlanResponseDto> findByHealthInsuranceId(Long healthInsuranceId);

    List<CoveragePlanResponseDto> findByHealthInsuranceIdAndIsActiveTrue(Long healthInsuranceId);

    CoveragePlanResponseDto findCoveragePlanByName(String name);

    Boolean existsByName(String name);

    boolean existsById(Long id);

    boolean existsByIdAndHealthInsuranceId(Long id, Long healthInsuranceId);

    Boolean existsByNameAndInsurance(String name, Long insuranceId, Long excludeId);

    Integer countActivePlans();

    Integer countActivePlanByHealthInsurance(Long healthInsuranceId);
}