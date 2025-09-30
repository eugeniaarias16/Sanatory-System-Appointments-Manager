package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.HealthInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.exception.DuplicateResourceException;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.repository.IHealthInsuranceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class HealthInsuranceService implements IHealthInsuranceService{

    private final IHealthInsuranceRepository healthInsuranceRepository;

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
    public HealthInsuranceResponseDto updateHealthInsuranceById(Long id, Map<String, Object> updates) {
        log.debug("Attempting to update Health Insurance with id {} and values: {}",id,updates.values());
        HealthInsurance existingHealthInsurance=healthInsuranceRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Health Insurance not found with id: "+id));

        updates.forEach((key,value)->{
            switch (key){
                case "companyName"->{
                    String companyName=(String) value;
                    if(existsByCompanyName(companyName)){
                        throw new DuplicateResourceException("Health Insurance with Company Name "+companyName+" already exists.");
                    }
                    existingHealthInsurance.setCompanyName(companyName);
                }

                case "companyCode"->{
                    Long companyCode=(Long) value;
                    if(existsByCompanyCode(companyCode)){
                        throw new DuplicateResourceException("Health Insurance with Company Code "+companyCode+" already exists.");
                    }
                    existingHealthInsurance.setCompanyCode(companyCode);
                }

                case "phoneNumber"->{
                    String phoneNumber=(String) value;
                    if (existsByPhoneNumber(phoneNumber)){
                        throw new DuplicateResourceException("Health Insurance with PhoneNumber "+phoneNumber+" already exists.");
                    }
                    existingHealthInsurance.setPhoneNumber(phoneNumber);
                }

                case "email"->{
                    String email=(String) value;
                    if(existsByEmail(email)){
                        throw new DuplicateResourceException("Health Insurance with Email "+email+" already exists.");
                    }
                    existingHealthInsurance.setEmail(email);
                }
                case "isActive"->existingHealthInsurance.setActive((Boolean)value);

                default -> log.warn("Field not recognized {}",key);
            }
        });

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
}
