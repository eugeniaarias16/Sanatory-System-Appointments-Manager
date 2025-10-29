package com.sanatoryApp.HealthInsuranceService.repository;

import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICoveragePlanRepository extends JpaRepository<CoveragePlan, Long> {

    Optional<CoveragePlan> findByName(String name);

    Boolean existsByNameIgnoreCase(String name);

    List<CoveragePlan> findByHealthInsuranceId(Long healthInsuranceId);

    List<CoveragePlan> findByHealthInsuranceIdAndIsActiveTrue(Long healthInsuranceId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM CoveragePlan c " +
            "WHERE c.id = :id AND c.healthInsuranceId = :healthInsuranceId AND c.isActive = true")
    boolean existsByIdAndHealthInsuranceId(
            @Param("id") Long id,
            @Param("healthInsuranceId") Long healthInsuranceId
    );

    boolean existsById(Long id);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM CoveragePlan c " +
            "WHERE c.healthInsuranceId = :insuranceId " +
            "AND LOWER(c.name) = LOWER(:name) " +
            "AND (:excludeId IS NULL OR c.id != :excludeId)")
    boolean existsByNameAndInsurance(
            @Param("name") String name,
            @Param("insuranceId") Long insuranceId,
            @Param("excludeId") Long excludeId
    );

    @Query("SELECT COUNT(c) FROM CoveragePlan c " +
            "WHERE c.isActive = true")
    Integer countActivePlans();

    @Query("SELECT COUNT(c) FROM CoveragePlan c " +
            "WHERE c.isActive = true AND c.healthInsuranceId = :healthInsuranceId")
    Integer countActivePlanByHealthInsurance(@Param("healthInsuranceId") Long healthInsuranceId);
}