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
public class HealthInsuranceService implements IHealthInsuranceService{

    private final IHealthInsuranceRepository healthInsuranceRepository;
    private final ICoveragePlanService coveragePlanService;
    private final IPatientInsuranceService patientInsuranceService;

    @Override
    public HealthInsuranceResponseDto findHealthInsuranceById(Long id) {
        log.debug("Attempting to find Health Insurance By id: {}",id);
        HealthInsurance healthInsurance=healthInsuranceRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Health Insurance not found with id: "+id));

        return HealthInsuranceResponseDto.fromEntity(healthInsurance);
    }

    @Transactional
    @Override
    public HealthInsuranceResponseDto createHealthInsurance(HealthInsuranceCreateDto dto) {
        log.debug("Attempting to create Health Insurance with values: {}",dto);
        if(existsByCompanyName(dto.getCompanyName())){
            throw new DuplicateResourceException("Health Insurance with Company Name "+dto.getCompanyName()+" already exists.");
        }
        if(existsByEmail(dto.getEmail())){
            throw new DuplicateResourceException("Health Insurance with Email "+dto.getEmail()+" already exists.");
        }
        if(existsByCompanyCode(dto.getCompanyCode())){
            throw new DuplicateResourceException("Health Insurance with Company Code "+dto.getCompanyCode()+" already exists.");
        }
        if (existsByPhoneNumber(dto.getPhoneNumber())){
            throw new DuplicateResourceException("Health Insurance with PhoneNumber "+dto.getPhoneNumber()+" already exists.");
        }

        HealthInsurance healthInsurance=dto.toEntity();
        HealthInsurance saved=healthInsuranceRepository.save(healthInsurance);
        log.info("Health Insurance with id {} successfully created",saved.getId());
        return HealthInsuranceResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public HealthInsuranceResponseDto updateHealthInsuranceById(Long id, HealthInsuranceUpdateDto dto) {
        log.debug("Attempting to update Health Insurance with id {} and values: {}",id,dto);
        HealthInsurance existingHealthInsurance=healthInsuranceRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Health Insurance not found with id: "+id));

        //updating company name
        if(dto.companyName()!=null && !dto.companyName().isEmpty()){
            String newName= dto.companyName();
            if(!existingHealthInsurance.getCompanyName().equalsIgnoreCase(newName)){
                if(existsByCompanyName(newName)){
                    throw new DuplicateResourceException("Health Insurance with Company Name "+newName+" already exists.");
                }
                existingHealthInsurance.setCompanyName(newName);
            }
        }

        //updating company code
        if(dto.companyCode()!=null){
            Long newCode= dto.companyCode();
            if(!existingHealthInsurance.getCompanyCode().equals(newCode)){
                if (existsByCompanyCode(newCode)){
                    throw new DuplicateResourceException("Health Insurance with Company Code "+newCode+" already exists.");
                }
                existingHealthInsurance.setCompanyCode(newCode);
            }
        }

        //updating phone number
        if(dto.phoneNumber()!=null && !dto.phoneNumber().isEmpty()){
            String phoneNumber= dto.phoneNumber();
            if(!existingHealthInsurance.getPhoneNumber().equals(phoneNumber)){
                if (existsByPhoneNumber(phoneNumber)){
                    throw new DuplicateResourceException("Health Insurance with PhoneNumber "+phoneNumber+" already exists.");
                }
                existingHealthInsurance.setPhoneNumber(phoneNumber);
            }
        }

        //updating email
        if(dto.email()!=null && !dto.email().isEmpty()){
            String newEmail=dto.email();
            if(!existingHealthInsurance.getEmail().equals(newEmail)){
                if(existsByEmail(newEmail)){
                        throw new DuplicateResourceException("Health Insurance with Email "+newEmail+" already exists.");
                    }
                existingHealthInsurance.setEmail(newEmail);
            }
        }

        HealthInsurance saved=healthInsuranceRepository.save(existingHealthInsurance);
        log.info("Health Insurance with id {} successfully updated.",id);
        return HealthInsuranceResponseDto.fromEntity(saved);

    }

    @Transactional
    @Override
    public void deleteHealthInsuranceById(Long id) {
        log.debug("Attempting to delete Health Insurance with id {}",id);
        HealthInsurance healthInsurance=healthInsuranceRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Health Insurance not found with id: "+id));
        healthInsuranceRepository.delete(healthInsurance);
        log.info("Health Insurance with id {} successfully deleted.",id);
    }

    @Override
    public void softDeleteHealthInsuranceById(Long id) {
        log.debug("Attempting to soft delete Health Insurance with id {}",id);
        HealthInsurance healthInsurance=healthInsuranceRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Health Insurance not found with id: "+id));
        healthInsurance.setActive(false);
        log.info("Health Insurance with id {} successfully deactivated (soft deleted).",id);
    }

    @Override
    public void activatedHealthInsuranceById(Long id) {
        log.debug("Attempting to activate Health Insurance with id {}",id);
        HealthInsurance healthInsurance=healthInsuranceRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Health Insurance not found with id: "+id));
        healthInsurance.setActive(true);
        log.info("Health Insurance with id {} successfully activated.",id);
    }


    @Override
    public HealthInsuranceResponseDto findHealthInsuranceByCompanyName(String companyName) {
        log.debug("Attempting to find Health Insurance by Company Name: {}",companyName);
        HealthInsurance healthInsurance=healthInsuranceRepository.findByCompanyName(companyName)
                .orElseThrow(()->new ResourceNotFound("Health Insurance not found with company name "+companyName));
        return HealthInsuranceResponseDto.fromEntity(healthInsurance);
    }

    @Override
    public HealthInsuranceResponseDto findHealthInsuranceByCompanyCode(Long companyCode) {
        log.debug("Attempting to find Health Insurance by Company Code: {}",companyCode);
        HealthInsurance healthInsurance=healthInsuranceRepository.findByCompanyCode(companyCode)
                .orElseThrow(()->new ResourceNotFound("Health Insurance not found with company code "+companyCode));
        return HealthInsuranceResponseDto.fromEntity(healthInsurance);
    }

    @Override
    public HealthInsuranceResponseDto findHealthInsuranceByPhoneNumber(String phoneNumber) {
        log.debug("Attempting to find Health Insurance by Phone Number: {}",phoneNumber);
        HealthInsurance healthInsurance=healthInsuranceRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(()->new ResourceNotFound("Health Insurance not found with phone number "+phoneNumber));
        return HealthInsuranceResponseDto.fromEntity(healthInsurance);
    }

    @Override
    public HealthInsuranceResponseDto findHealthInsuranceByEmail(String email) {
        log.debug("Attempting to find Health Insurance by Email: {}",email);
        HealthInsurance healthInsurance=healthInsuranceRepository.findByEmail(email)
                .orElseThrow(()->new ResourceNotFound("Health Insurance not found with email "+email));
        return HealthInsuranceResponseDto.fromEntity(healthInsurance);
    }

    @Override
    public List<HealthInsuranceResponseDto> searchByName(String name) {
       List<HealthInsurance> healthInsuranceList=healthInsuranceRepository.findByCompanyNameContaining(name);
       if(healthInsuranceList.isEmpty()){
           log.info("No Health Insurance found with name: {} ",name);
       }
       return healthInsuranceList.stream()
               .map(HealthInsuranceResponseDto::fromEntity)
               .toList();
    }

    @Override
    public List<CoveragePlanResponseDto> findCoveragePlans(Long insuranceId) {
        log.debug("Attempting to find all coverage plans active with insurance id");
        List<CoveragePlanResponseDto> coveragePlanList=coveragePlanService.findByHealthInsuranceIdAndIsActiveTrue(insuranceId);
        return  coveragePlanList;
    }

    @Override
    public List<PatientInsuranceResponseDto> findPatientsByInsuranceId(Long insuranceId) {
        return patientInsuranceService.findPatientInsuranceByHealthInsurance(insuranceId);
    }

    @Override
    public Boolean existsByCompanyName(String companyName) {
        log.debug("Verifying if Health Insurance with Company Name: {} already exists",companyName);
        return healthInsuranceRepository.existsByCompanyName(companyName);
    }

    @Override
    public Boolean existsByCompanyCode(Long companyCode) {
        log.debug("Verifying if Health Insurance with Company Code: {} already exists",companyCode);
        return healthInsuranceRepository.existsByCompanyCode(companyCode);
    }

    @Override
    public Boolean existsByPhoneNumber(String phoneNumber) {
        log.debug("Verifying if Health Insurance with Phone Number: {} already exists",phoneNumber);
        return healthInsuranceRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public Boolean existsByEmail(String email) {
        log.debug("Verifying if Health Insurance with Email: {} already exists",email);
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
}
