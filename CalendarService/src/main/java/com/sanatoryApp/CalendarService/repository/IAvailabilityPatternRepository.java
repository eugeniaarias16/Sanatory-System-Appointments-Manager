package com.sanatoryApp.CalendarService.repository;

import com.sanatoryApp.CalendarService.entity.AvailabilityPattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface IAvailabilityPatternRepository extends JpaRepository<AvailabilityPattern, Long> {

    List<AvailabilityPattern> findByDoctorCalendarId(Long id);

    List<AvailabilityPattern> findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(
            Long calendarId,
            DayOfWeek dayOfWeek
    );

    @Query("SELECT ap FROM AvailabilityPattern ap " +
            "WHERE ap.doctorCalendar.id IN (" +
            "SELECT dc.id FROM DoctorCalendar dc " +
            "WHERE dc.doctorId = :doctorId AND dc.isActive = true) " +
            "AND ap.isActive = true " +
            "ORDER BY ap.dayOfWeek, ap.startTime")
    List<AvailabilityPattern> findByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT ap FROM AvailabilityPattern ap " +
            "WHERE ap.doctorCalendar.id IN (" +
            "SELECT dc.id FROM DoctorCalendar dc " +
            "WHERE dc.doctorId = :doctorId AND dc.isActive = true) " +
            "AND ap.dayOfWeek = :dayOfWeek " +
            "AND ap.isActive = true " +
            "ORDER BY ap.startTime")
    List<AvailabilityPattern> findByDoctorIdAndDay(
            @Param("doctorId") Long doctorId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek
    );

    @Query("SELECT CASE WHEN EXISTS (" +
            "SELECT 1 FROM AvailabilityPattern ap " +
            "WHERE ap.doctorCalendar.id IN " +
            "  (SELECT dc2.id FROM DoctorCalendar dc2 " +
            "   WHERE dc2.doctorId = " +
            "     (SELECT dc.doctorId FROM DoctorCalendar dc WHERE dc.id = :calendarId) " +
            "   AND dc2.isActive = true) " +
            "AND ap.dayOfWeek = :dayOfWeek " +
            "AND ap.isActive = true " +
            "AND ap.id != :excludeId " +
            "AND (ap.startTime < :endTime AND ap.endTime > :startTime)" +
            ") THEN true ELSE false END")
    boolean hasOverlappingPattern(
            @Param("calendarId") Long calendarId,
            @Param("dayOfWeek") DayOfWeek dayOfWeek,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId
    );
}