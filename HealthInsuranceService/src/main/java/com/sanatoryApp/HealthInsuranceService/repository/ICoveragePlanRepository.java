package com.sanatoryApp.HealthInsuranceService.repository;

import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICoveragePlanRepository extends JpaRepository<CoveragePlan,Long> {

    Optional<CoveragePlan>findByName(String name);
    Boolean existsByName(String name);
}
