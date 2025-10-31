package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.exception.DuplicateResourceException;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.repository.ICoveragePlanRepository;
import com.sanatoryApp.HealthInsuranceService.repository.IHealthInsuranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CoveragePlanService implements ICoveragePlanService {

    private final ICoveragePlanRepository coveragePlanRepository;
    private final IHealthInsuranceService healthInsuranceService;


    @Override
    public CoveragePlan getCoveragePlanById(Long id) {
         return coveragePlanRepository.findById(id)
                 .orElseThrow(() -> new ResourceNotFound("Coverage Plan not found with id: " + id));
    }

    @Override
    public CoveragePlanResponseDto findCoveragePlanById(Long id) {
        log.debug("Searching Coverage Plan by id {}", id);
        CoveragePlan coveragePlan = getCoveragePlanById(id);
        return CoveragePlanResponseDto.fromEntity(coveragePlan);
    }

    @Transactional
    @Override
    public CoveragePlanResponseDto createCoveragePlan(CoveragePlanCreateDto dto) {
        HealthInsurance healthInsurance = healthInsuranceService.getHealthInsuranceById(dto.getHealthInsuranceId());

        if (existsByNameAndInsurance(dto.getName(), dto.getHealthInsuranceId(), null)) {
            throw new DuplicateResourceException(
                    "Already exists a Coverage Plan with name " + dto.getName() +
                            " and insurance id " + dto.getHealthInsuranceId()
            );
        }

        log.debug("Creating new Coverage Plan with values: {}", dto);
        CoveragePlan coveragePlan = dto.toEntity(healthInsurance);
        CoveragePlan saved = coveragePlanRepository.save(coveragePlan);
        log.info("Coverage Plan with id {} successfully created.", saved.getId());
        return CoveragePlanResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public CoveragePlanResponseDto updateCoveragePlanById(Long id, CoveragePlanUpdateDto dto) {
        log.debug("Attempting to update Coverage Plan with id: {} with fields: {}", id, dto);
        CoveragePlan existingCoveragePlan = getCoveragePlanById(id);

        if (dto.name() != null && !dto.name().isEmpty()) {
            String newName = dto.name();
            if (!existingCoveragePlan.getName().equalsIgnoreCase(newName)) {
                if (existsByNameAndInsurance(newName, existingCoveragePlan.getHealthInsurance().getId(), id)) {
                    throw new DuplicateResourceException(
                            "Already exists a Coverage Plan with name " + newName +
                                    " and insurance id " + existingCoveragePlan.getHealthInsurance().getId()
                    );
                }
                existingCoveragePlan.setName(newName);
            }
        }

        if (dto.description() != null && !dto.description().isEmpty()) {
            existingCoveragePlan.setDescription(dto.description());
        }

        if (dto.coverageValuePercentage() != null &&
                !existingCoveragePlan.getCoverageValuePercentage().equals(dto.coverageValuePercentage())) {
            existingCoveragePlan.setCoverageValuePercentage(dto.coverageValuePercentage());
        }

        CoveragePlan coveragePlan = coveragePlanRepository.save(existingCoveragePlan);
        log.info("Coverage Plan with id {} successfully updated with values: {}", id, dto);
        return CoveragePlanResponseDto.fromEntity(coveragePlan);
    }

    @Transactional
    @Override
    public void softDeleteCoveragePlanById(Long id) {
        log.debug("Attempting to soft delete Coverage Plan with id: {}", id);
        CoveragePlan coveragePlan = getCoveragePlanById(id);
        coveragePlan.setActive(false);
        coveragePlanRepository.save(coveragePlan);
        log.info("Coverage Plan with id {} successfully deactivated (soft deleted).", id);
    }

    @Transactional
    @Override
    public void deleteCoveragePlanById(Long id) {
        log.debug("Attempting to delete Coverage Plan with id: {}", id);
        CoveragePlan coveragePlan = getCoveragePlanById(id);
        coveragePlanRepository.delete(coveragePlan);
        log.info("Coverage Plan with id {} successfully deleted.", id);
    }

    @Override
    public List<CoveragePlanResponseDto> findByHealthInsuranceId(Long healthInsuranceId) {
        List<CoveragePlan> coveragePlanList = coveragePlanRepository.findByHealthInsurance_Id(healthInsuranceId);
        if (coveragePlanList.isEmpty()) {
            log.info("No coverage plans found with Insurance id {}", healthInsuranceId);
        }
        return coveragePlanList.stream()
                .map(CoveragePlanResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CoveragePlanResponseDto> findByHealthInsuranceIdAndIsActiveTrue(Long healthInsuranceId) {
        List<CoveragePlan> coveragePlanList =
                coveragePlanRepository.findByHealthInsurance_IdAndIsActiveTrue(healthInsuranceId);
        if (coveragePlanList.isEmpty()) {
            log.info("No active coverage plans found with Insurance id {}", healthInsuranceId);
        }
        return coveragePlanList.stream()
                .map(CoveragePlanResponseDto::fromEntity)
                .toList();
    }

    @Override
    public CoveragePlanResponseDto findCoveragePlanByName(String name) {
        log.debug("Attempting to find Coverage Plan by name: {}", name);
        CoveragePlan coveragePlan = coveragePlanRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFound("Coverage Plan not found with name: " + name));
        return CoveragePlanResponseDto.fromEntity(coveragePlan);
    }

    @Override
    public Boolean existsByName(String name) {
        log.debug("Verifying if Coverage Plan already exists with name {} ", name);
        return coveragePlanRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public boolean existsById(Long id) {
        return coveragePlanRepository.existsById(id);
    }

    @Override
    public boolean existsByIdAndHealthInsuranceId(Long id, Long healthInsuranceId) {
        return coveragePlanRepository.existsByIdAndHealthInsuranceId(id, healthInsuranceId);
    }

    @Override
    public Boolean existsByNameAndInsurance(String name, Long insuranceId, Long excludeId) {
        return coveragePlanRepository.existsByNameAndInsurance(name, insuranceId, excludeId);
    }

    @Override
    public Integer countActivePlans() {
        return coveragePlanRepository.countActivePlans();
    }

    @Override
    public Integer countActivePlanByHealthInsurance(Long healthInsuranceId) {
        return coveragePlanRepository.countActivePlanByHealthInsurance(healthInsuranceId);
    }
}