package com.sanatoryApp.CalendarService.repository;

import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IDoctorCalendarRepository extends JpaRepository<DoctorCalendar,Long> {

    @Query("SELECT dc.doctorId FROM DoctorCalendar dc WHERE dc.id = :doctorCalendarId")
    Long getDoctorIdByDoctorCalendarId(@Param("doctorCalendarId") Long doctorCalendarId);

    //Find all active calendars for a doctor
    List<DoctorCalendar> findByDoctorIdAndIsActiveTrue(Long doctorId);

    List<DoctorCalendar>findByDoctorId(Long doctorId);

    //Check if a doctor already has a calendar with that name.
    boolean existsByDoctorIdAndNameAndIsActiveTrue(Long doctorId, String name);

    @Query("SELECT CASE WHEN COUNT(dc)>0 THEN true ELSE false END"+
            "FROM DoctorCalendar dc"+
            "WHERE dc.doctorId=:doctorId"+
            "AND dc.name=:name"+
            "AND dc.isActive=true"+
            "AND dc.id!=:excludeId")
    boolean existsByDoctorIdAndNameExcludingId(
            @Param("doctorId") Long doctorId,
            @Param("name") String name,
            @Param("excludeId") Long excludeId
    );

    boolean existsById(Long calendarId);




}
