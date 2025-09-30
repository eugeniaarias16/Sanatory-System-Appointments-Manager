package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.exception.DuplicateResourceException;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.repository.ICoveragePlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoveragePlanService implements ICoveragePlanService{
   private final ICoveragePlanRepository coveragePlanRepository;

   @Override
    public CoveragePlanResponseDto findCoveragePlanById(Long id) {
       log.debug("Searching Coverage Plan by id {}",id);
       CoveragePlan coveragePlan=coveragePlanRepository.findById(id)
               .orElseThrow(()->new ResourceNotFound("Coverage Plan not found with id: "+id));
        return CoveragePlanResponseDto.fromEntity(coveragePlan);
   }
    @Transactional
    @Override
    public CoveragePlanResponseDto createCoveragePlan(CoveragePlanCreateDto dto) {

       if(existsByName(dto.getName())){
        throw new DuplicateResourceException("Coverage Plan already exists with name: "+dto.getName());
       }
       log.debug("Creating new Coverage Plan with values: {}",dto);
       CoveragePlan coveragePlan=dto.toEntity();
       CoveragePlan saved=coveragePlanRepository.save(coveragePlan);
       log.info("Coverage Plan with id {} successfully created.",saved.getId());
       return CoveragePlanResponseDto.fromEntity(saved);
    }
    @Transactional
    @Override
    public CoveragePlanResponseDto updateCoveragePlanById(Long id, Map<String, Object> updates) {
        log.debug("Attempting to update Coverage Plan with id: {} with fields: {}", id, updates.keySet());
        CoveragePlan existingCoveragePlan=coveragePlanRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Coverage Plan not found with id: "+id));

        updates.forEach((key,value)->{
            switch (key){
                case"name"->{
                    String name=(String) value;
                    if(existsByName(name)){
                        throw new DuplicateResourceException("Coverage Plan already exists with name: "+name);
                    }
                    existingCoveragePlan.setName(name);
                }
                case "description"->existingCoveragePlan.setDescription((String) value);
                case "coverageValuePercentage"->existingCoveragePlan.setCoverageValuePercentage((Double)value);
                case "isActive"->existingCoveragePlan.setActive((Boolean)value);
                default -> log.warn("Field not recognized: {}",key);
            }
        });
        CoveragePlan coveragePlan=coveragePlanRepository.save(existingCoveragePlan);
        log.info("Coverage Plan with id {} successfully updated with values: {}",id,updates.values());
        return CoveragePlanResponseDto.fromEntity(coveragePlan);
    }
    @Transactional
    @Override
    public void deleteCoveragePlanById(Long id) {
        log.debug("Attempting to delete Coverage Plan with id: {}", id);
        CoveragePlan coveragePlan=coveragePlanRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Coverage Plan not found with id: "+id));
        coveragePlanRepository.delete(coveragePlan);
        log.info("Coverage plan successfully deleted.");
    }

    @Override
    public CoveragePlanResponseDto findCoveragePlanByName(String name) {
       log.debug("Attempting to find Coverage Plan by name: {}",name);
       CoveragePlan coveragePlan=coveragePlanRepository.findByName(name)
               .orElseThrow(()-> new ResourceNotFound("Coverage Plan not found with name: "+name));
       return CoveragePlanResponseDto.fromEntity(coveragePlan);
    }

    @Override
    public Boolean existsByName(String name) {
        log.debug("Verifying if Coverage Plan already exists with name {} ",name);
        return coveragePlanRepository.existsByName(name);
    }
}
