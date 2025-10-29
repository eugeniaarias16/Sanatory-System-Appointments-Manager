package com.sanatoryApp.HealthInsuranceService.repository;

import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IHealthInsuranceRepository extends JpaRepository<HealthInsurance, Long> {

    Optional<HealthInsurance> findByCompanyName(String companyName);

    Optional<HealthInsurance> findByCompanyCode(Long companyCode);

    Optional<HealthInsurance> findByEmail(String email);

    Optional<HealthInsurance> findByPhoneNumber(String phoneNumber);

    List<HealthInsurance> findByIsActiveTrue();

    @Query("SELECT h FROM HealthInsurance h " +
            "WHERE LOWER(h.companyName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "ORDER BY h.companyName")
    List<HealthInsurance> findByCompanyNameContaining(@Param("name") String name);


    boolean existsByCompanyName(String companyName);

    boolean existsByCompanyCode(Long companyCode);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    boolean existsById(Long id);
}