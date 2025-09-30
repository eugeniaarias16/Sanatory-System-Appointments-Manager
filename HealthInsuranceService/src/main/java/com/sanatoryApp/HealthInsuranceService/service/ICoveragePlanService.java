package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;

import java.util.Map;

public interface ICoveragePlanService {

    /* BASIC CRUD */
    CoveragePlanResponseDto findCoveragePlanById(Long id);
    CoveragePlanResponseDto createCoveragePlan(CoveragePlanCreateDto dto);
    CoveragePlanResponseDto updateCoveragePlanById(Long id, Map<String,Object>updates);
    void deleteCoveragePlanById(Long id);

    CoveragePlanResponseDto findCoveragePlanByName(String name);
    Boolean existsByName(String name);

}
