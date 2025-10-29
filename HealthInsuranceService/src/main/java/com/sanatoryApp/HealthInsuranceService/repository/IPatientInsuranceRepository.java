package com.sanatoryApp.HealthInsuranceService.repository;

import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPatientInsuranceRepository extends JpaRepository<PatientInsurance, Long> {

    List<PatientInsurance> findByPatientDni(String dni);
    Optional<PatientInsurance> findByCredentialNumber(String id);
    List<PatientInsurance> findByHealthInsuranceId(Long id);
    List<PatientInsurance> findByCoveragePlanId(Long id);
    List<PatientInsurance> findByCreatedAt(LocalDate date);
    List<PatientInsurance> findByCreatedAtAfter(LocalDate date);

    Boolean existsByCredentialNumber(String credentialNumber);

    @Query("SELECT COUNT(p) FROM PatientInsurance p " +
            "WHERE p.healthInsuranceId = :insuranceId " +
            "AND p.isActive = true")
    Integer countActivePatientsByInsuranceId(@Param("insuranceId") Long insuranceId);
}