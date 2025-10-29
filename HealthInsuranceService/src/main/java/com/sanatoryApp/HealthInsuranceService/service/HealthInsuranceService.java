package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.HealthInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.exception.DuplicateResourceException;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.repository.IHealthInsuranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class HealthInsuranceService implements IHealthInsuranceService {

    private final IHealthInsuranceRepository healthInsuranceRepository;
    private final ICoveragePlanService coveragePlanService;
    private final IPatientInsuranceService patientInsuranceService;

    @Override
    public HealthInsuranceResponseDto findHealthInsuranceById(Long id) {
        log.debug("Attempting to find Health Insurance by id: {}", id);
        HealthInsurance healthInsurance = healthInsuranceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Health Insurance not found with id: " + id));

        return HealthInsuranceResponseDto.fromEntity(healthInsurance);
    }

    @Transactional
    @Override
    public HealthInsuranceResponseDto createHealthInsurance(HealthInsuranceCreateDto dto) {
        log.debug("Attempting to create Health Insurance with values: {}", dto);

        validateUniqueConstraints(dto.getCompanyName(), dto.getEmail(),
                dto.getCompanyCode(), dto.getPhoneNumber());

        HealthInsurance healthInsurance = dto.toEntity();
        HealthInsurance saved = healthInsuranceRepository.save(healthInsurance);
        log.info("Health Insurance with id {} successfully created", saved.getId());
        return HealthInsuranceResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public HealthInsuranceResponseDto updateHealthInsuranceById(Long id, HealthInsuranceUpdateDto dto) {
        log.debug("Attempting to update Health Insurance with id {} and values: {}", id, dto);
        HealthInsurance existingHealthInsurance = healthInsuranceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Health Insurance not found with id: " + id));

        updateCompanyName(existingHealthInsurance, dto.companyName());
        updateCompanyCode(existingHealthInsurance, dto.companyCode());
        updatePhoneNumber(existingHealthInsurance, dto.phoneNumber());
        updateEmail(existingHealthInsurance, dto.email());

        HealthInsurance saved = healthInsuranceRepository.save(existingHealthInsurance);
        log.info("Health Insurance with id {} successfully updated", id);
        return HealthInsuranceResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public void deleteHealthInsuranceById(Long id) {
        log.debug("Attempting to delete Health Insurance with id {}", id);
        HealthInsurance healthInsurance = healthInsuranceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Health Insurance not found with id: " + id));
        healthInsuranceRepository.delete(healthInsurance);
        log.info("Health Insurance with id {} successfully deleted", id);
    }

    @Transactional
    @Override
    public void softDeleteHealthInsuranceById(Long id) {
        log.debug("Attempting to soft delete Health Insurance with id {}", id);
        HealthInsurance healthInsurance = healthInsuranceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Health Insurance not found with id: " + id));
        healthInsurance.setActive(false);
        healthInsuranceRepository.save(healthInsurance);
        log.info("Health Insurance with id {} successfully deactivated", id);
    }

    @Transactional
    @Override
    public void activateHealthInsuranceById(Long id) {
        log.debug("Attempting to activate Health Insurance with id {}", id);
        HealthInsurance healthInsurance = healthInsuranceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Health Insurance not found with id: " + id));
        healthInsurance.setActive(true);
        healthInsuranceRepository.save(healthInsurance);
        log.info("Health Insurance with id {} successfully activated", id);
    }

    @Override
    public HealthInsuranceResponseDto findHealthInsuranceByCompanyName(String companyName) {
        log.debug("Attempting to find Health Insurance by Company Name: {}", companyName);
        HealthInsurance healthInsurance = healthInsuranceRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new ResourceNotFound("Health Insurance not found with company name: " + companyName));
        return HealthInsuranceResponseDto.fromEntity(healthInsurance);
    }

    @Override
    public HealthInsuranceResponseDto findHealthInsuranceByCompanyCode(Long companyCode) {
        log.debug("Attempting to find Health Insurance by Company Code: {}", companyCode);
        HealthInsurance healthInsurance = healthInsuranceRepository.findByCompanyCode(companyCode)
                .orElseThrow(() -> new ResourceNotFound("Health Insurance not found with company code: " + companyCode));
        return HealthInsuranceResponseDto.fromEntity(healthInsurance);
    }

    @Override
    public HealthInsuranceResponseDto findHealthInsuranceByPhoneNumber(String phoneNumber) {
        log.debug("Attempting to find Health Insurance by Phone Number: {}", phoneNumber);
        HealthInsurance healthInsurance = healthInsuranceRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFound("Health Insurance not found with phone number: " + phoneNumber));
        return HealthInsuranceResponseDto.fromEntity(healthInsurance);
    }

    @Override
    public HealthInsuranceResponseDto findHealthInsuranceByEmail(String email) {
        log.debug("Attempting to find Health Insurance by Email: {}", email);
        HealthInsurance healthInsurance = healthInsuranceRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFound("Health Insurance not found with email: " + email));
        return HealthInsuranceResponseDto.fromEntity(healthInsurance);
    }

    @Override
    public List<HealthInsuranceResponseDto> searchByName(String name) {
        log.debug("Searching Health Insurance by name containing: {}", name);
        List<HealthInsurance> healthInsuranceList = healthInsuranceRepository.findByCompanyNameContaining(name);

        if (healthInsuranceList.isEmpty()) {
            log.info("No Health Insurance found with name containing: {}", name);
        }

        return healthInsuranceList.stream()
                .map(HealthInsuranceResponseDto::fromEntity)
                .toList();
    }

    @Override
    public List<CoveragePlanResponseDto> findCoveragePlans(Long insuranceId) {
        log.debug("Attempting to find all active coverage plans for insurance id: {}", insuranceId);
        return coveragePlanService.findByHealthInsuranceIdAndIsActiveTrue(insuranceId);
    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientsByInsuranceId(Long insuranceId) {
        log.debug("Attempting to find all patients for insurance id: {}", insuranceId);
        return patientInsuranceService.findPatientInsuranceByHealthInsurance(insuranceId);
    }

    @Override
    public boolean existsByCompanyName(String companyName) {
        log.debug("Verifying if Health Insurance with Company Name: {} already exists", companyName);
        return healthInsuranceRepository.existsByCompanyName(companyName);
    }

    @Override
    public boolean existsByCompanyCode(Long companyCode) {
        log.debug("Verifying if Health Insurance with Company Code: {} already exists", companyCode);
        return healthInsuranceRepository.existsByCompanyCode(companyCode);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        log.debug("Verifying if Health Insurance with Phone Number: {} already exists", phoneNumber);
        return healthInsuranceRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("Verifying if Health Insurance with Email: {} already exists", email);
        return healthInsuranceRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(Long id) {
        return healthInsuranceRepository.existsById(id);
    }

    @Override
    public Integer countActivePatients(Long insuranceId) {
        return patientInsuranceService.countActivePatientsByInsuranceId(insuranceId);
    }

    @Override
    public Integer countActivePlans(Long insuranceId) {
        return coveragePlanService.countActivePlanByHealthInsurance(insuranceId);
    }

    // Private helper methods to reduce code duplication
    private void validateUniqueConstraints(String companyName, String email,
                                           Long companyCode, String phoneNumber) {
        if (existsByCompanyName(companyName)) {
            throw new DuplicateResourceException("Health Insurance with Company Name '"
                    + companyName + "' already exists");
        }
        if (existsByEmail(email)) {
            throw new DuplicateResourceException("Health Insurance with Email '"
                    + email + "' already exists");
        }
        if (existsByCompanyCode(companyCode)) {
            throw new DuplicateResourceException("Health Insurance with Company Code '"
                    + companyCode + "' already exists");
        }
        if (existsByPhoneNumber(phoneNumber)) {
            throw new DuplicateResourceException("Health Insurance with Phone Number '"
                    + phoneNumber + "' already exists");
        }
    }

    private void updateCompanyName(HealthInsurance insurance, String newName) {
        if (newName != null && !newName.isBlank()
                && !insurance.getCompanyName().equalsIgnoreCase(newName)) {
            if (existsByCompanyName(newName)) {
                throw new DuplicateResourceException("Health Insurance with Company Name '"
                        + newName + "' already exists");
            }
            insurance.setCompanyName(newName);
        }
    }

    private void updateCompanyCode(HealthInsurance insurance, Long newCode) {
        if (newCode != null && !insurance.getCompanyCode().equals(newCode)) {
            if (existsByCompanyCode(newCode)) {
                throw new DuplicateResourceException("Health Insurance with Company Code '"
                        + newCode + "' already exists");
            }
            insurance.setCompanyCode(newCode);
        }
    }

    private void updatePhoneNumber(HealthInsurance insurance, String newPhone) {
        if (newPhone != null && !newPhone.isBlank()
                && !insurance.getPhoneNumber().equals(newPhone)) {
            if (existsByPhoneNumber(newPhone)) {
                throw new DuplicateResourceException("Health Insurance with Phone Number '"
                        + newPhone + "' already exists");
            }
            insurance.setPhoneNumber(newPhone);
        }
    }

    private void updateEmail(HealthInsurance insurance, String newEmail) {
        if (newEmail != null && !newEmail.isBlank()
                && !insurance.getEmail().equalsIgnoreCase(newEmail)) {
            if (existsByEmail(newEmail)) {
                throw new DuplicateResourceException("Health Insurance with Email '"
                        + newEmail + "' already exists");
            }
            insurance.setEmail(newEmail);
        }
    }
}