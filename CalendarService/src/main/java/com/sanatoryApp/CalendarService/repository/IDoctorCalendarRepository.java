package com.sanatoryApp.CalendarService.repository;

import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDoctorCalendarRepository extends JpaRepository<DoctorCalendar, Long> {

    @Query("SELECT dc.doctorId FROM DoctorCalendar dc WHERE dc.id = :doctorCalendarId")
    Long getDoctorIdByDoctorCalendarId(@Param("doctorCalendarId") Long doctorCalendarId);

    List<DoctorCalendar> findByDoctorIdAndIsActiveTrue(Long doctorId);

    List<DoctorCalendar> findByDoctorId(Long doctorId);

    @Query("SELECT dc FROM DoctorCalendar dc " +
            "WHERE dc.doctorId = :doctorId " +
            "AND dc.name = :name " +
            "AND dc.isActive = true")
    Optional<DoctorCalendar> findByDoctorIdAndName(@Param("doctorId") Long doctorId,
                                                   @Param("name") String name);

    boolean existsByDoctorIdAndNameAndIsActiveTrue(Long doctorId, String name);

    @Query("SELECT CASE WHEN COUNT(dc) > 0 THEN true ELSE false END " +
            "FROM DoctorCalendar dc " +
            "WHERE dc.doctorId = :doctorId " +
            "AND dc.name = :name " +
            "AND dc.isActive = true " +
            "AND dc.id != :excludeId")
    boolean existsByDoctorIdAndNameExcludingId(@Param("doctorId") Long doctorId,
                                               @Param("name") String name,
                                               @Param("excludeId") Long excludeId);

    @Query("SELECT dc FROM DoctorCalendar dc WHERE dc.id = :id AND dc.isActive = true")
    Optional<DoctorCalendar> findByIdAndActiveTrue(@Param("id") Long id);

    boolean existsById(Long calendarId);
}