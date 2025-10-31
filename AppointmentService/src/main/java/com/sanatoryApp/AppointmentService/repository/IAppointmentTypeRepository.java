package com.sanatoryApp.AppointmentService.repository;

import com.sanatoryApp.AppointmentService.entity.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface IAppointmentTypeRepository extends JpaRepository<AppointmentType,Long> {

    @Query("SELECT ap FROM AppointmentType ap WHERE ap.name = :name AND ap.isActive = true")
    Optional<AppointmentType> findByName(String name);

    @Query( "SELECT ap FROM AppointmentType ap "+
            "WHERE LOWER(ap.name) LIKE LOWER(CONCAT('%',:name,'%')) "+
            "AND ap.isActive=true "+
            "ORDER BY ap.name ")
    List<AppointmentType>findByLikeName(@Param("name") String name);

    @Query( "SELECT ap FROM AppointmentType ap "+
            "WHERE ap.basePrice BETWEEN :minPrice AND :maxPrice "+
            "AND ap.isActive=true "+
            "ORDER BY ap.basePrice ")

    List<AppointmentType> findByRangeBasePrice(@Param("minPrice") BigDecimal minPrice,
                                         @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT ap FROM AppointmentType ap WHERE ap.id = :id AND ap.isActive = true")
    Optional<AppointmentType> findByIdAndActive(@Param("id") Long id);

    boolean existsByNameIgnoreCase(String name);

}
