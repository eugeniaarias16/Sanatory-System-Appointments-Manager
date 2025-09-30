package com.sanatoryApp.HealthInsuranceService.repository;

import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IHealthInsuranceRepository extends JpaRepository<HealthInsurance,Long> {

    Optional<HealthInsurance>findByCompanyName(String companyName);
    Optional<HealthInsurance> findByCompanyCode(Long companyCode);
    Optional<HealthInsurance>findByEmail(String email);
    Optional<HealthInsurance>findByPhoneNumber(String phoneNumber);

    Boolean existsByCompanyName(String companyName);
    Boolean existsByCompanyCode(Long companyCode);
    Boolean existsByPhoneNumber(String phoneNumber);
    Boolean existsByEmail(String email);



}
