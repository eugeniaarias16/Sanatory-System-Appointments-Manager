package com.sanatoryApp.HealthInsuranceService.repository;

import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IPatientInsuranceRepository extends JpaRepository<PatientInsurance, Long> {

    List<PatientInsurance> findByPatientDni(String dni);

    Optional<PatientInsurance> findByCredentialNumber(String id);


    List<PatientInsurance> findByHealthInsurance_Id(Long id);


    List<PatientInsurance> findByCoveragePlan_Id(Long id);

    List<PatientInsurance> findByCreatedAt(LocalDate date);

    List<PatientInsurance> findByCreatedAtAfter(LocalDate date);

    Boolean existsByCredentialNumber(String credentialNumber);


    @Query("SELECT COUNT(p) FROM PatientInsurance p " +
            "WHERE p.healthInsurance.id = :insuranceId " +
            "AND p.isActive = true")
    Integer countActivePatientsByInsuranceId(@Param("insuranceId") Long insuranceId);
}