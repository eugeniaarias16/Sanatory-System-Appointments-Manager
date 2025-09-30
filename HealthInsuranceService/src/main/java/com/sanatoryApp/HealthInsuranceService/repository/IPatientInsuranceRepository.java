package com.sanatoryApp.HealthInsuranceService.repository;

import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPatientInsuranceRepository extends JpaRepository<PatientInsurance,Long> {
    List<PatientInsurance>findByPatientId(Long id);
    Optional<PatientInsurance>findByCredentialNumber(String id);
    List<PatientInsurance>findByHealthInsuranceId(Long id);
    List<PatientInsurance> findByCoveragePlanId(Long id);
    List<PatientInsurance>findByCreatedAt(LocalDate date);
    List<PatientInsurance> findByCreatedAtAfterDate(LocalDate date);

}
