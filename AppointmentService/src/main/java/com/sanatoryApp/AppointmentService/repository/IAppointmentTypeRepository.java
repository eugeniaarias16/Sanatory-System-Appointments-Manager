package com.sanatoryApp.AppointmentService.repository;

import com.sanatoryApp.AppointmentService.entity.AppointmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IAppointmentTypeRepository extends JpaRepository<AppointmentType,Long> {

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

    boolean existsByNameIgnoreCase(String name);

}
