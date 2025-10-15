package com.sanatoryApp.CalendarService.repository;


import com.sanatoryApp.CalendarService.entity.CalendarException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ICalendarExceptionRepository extends JpaRepository<CalendarException,Long> {

    List<CalendarException> findByDoctorCalendarId(Long doctorCalendarId);


    //Find applicable exceptions within a RANGE of dates
    @Query("SELECT ce FROM CalendarException ce"+
            "WHERE ce.doctorCalendarId=:calendarId"+"" +
            "OR (ce.isGlobal=true AND ce.doctorCalendarId IN"+
            "(SELECT dc.id FROM DoctorCalendar dc WHERE dc.doctorId=:doctorId)))" +
            "AND ce.date BETWEEN :startDate AND :endDate"+
            "ce.isActive=true"+
            "ORDER BY ce.date ASC")
    List<CalendarException> findApplicableExceptionsInTimeRange(@Param("calendarId")Long calendarId,
                                                                @Param("doctorId") Long doctorId,
                                                                @Param("startDate")LocalDate startDate,
                                                                @Param("endDate")LocalDate endDate);



    // Method for searching exceptions (specific or global) that apply to a specific calendar
    @Query("SELECT ce FROM CalendarException ce " +
            "WHERE (ce.doctorCalendarId = :calendarId " +
            "OR (ce.isGlobal = true AND ce.doctorCalendarId IN " +
            "(SELECT dc.id FROM DoctorCalendar dc WHERE dc.doctorId = :doctorId))) " +
            "AND ce.date = :date " +
            "AND ce.isActive = true")
    List<CalendarException>findApplicableExceptionsForCalendar(@Param("calendarId") Long calendarId,
                                                    @Param("doctorId") Long doctorId,
                                                    @Param("date") LocalDate date);



    //Find all global exceptions for a doctor
    @Query("SELECT ce FROM CalendarException ce"+
            "WHERE ce.isGlobal=true"+
            "AND ce.doctorCalendarId IN"+
            "(SELECT dc.id FROM DoctorCalendar dc WHERE dc.doctorId=:doctorId)"+
            "AND ce.isActive"+
            "ORDER BY ce.date")
    List<CalendarException>findGlobalExceptionsByDoctorId(@Param("doctorId")Long doctorId);




    //Check if there is a conflicting exception
    @Query("SELECT CASE WHEN COUNT(ce) > 0 THEN true ELSE false END " +
            "FROM CalendarException ce " +
            "WHERE ce.doctorCalendarId = :calendarId " +
            "AND ce.date = :date " +
            "AND ce.isActive = true " +
            "AND ce.id != :excludeId")
    boolean existsConflictingException(@Param("calendarId")Long calendarId,
                                       @Param("date")LocalDate date,
                                       @Param("excludeId") Long excludeId); // To exclude the same exception when updating



    //Find future exceptions in a calendar
    @Query("SELECT ce FROM CalendarException ce"+
            "WHERE ce.date>=:currentDate"+
            "AND ce.isActive=true" +
            "ORDER BY date ASC")
    List<CalendarException> findFutureExceptions(
            @Param("calendarId") Long calendarId,
            @Param("currentDate") LocalDate currentDate
    );
}
