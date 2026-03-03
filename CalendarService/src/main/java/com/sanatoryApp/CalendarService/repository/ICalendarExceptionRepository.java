package com.sanatoryApp.CalendarService.repository;

import com.sanatoryApp.CalendarService.entity.CalendarException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ICalendarExceptionRepository extends JpaRepository<CalendarException, Long> {

    /** Active Calendar Exceptions **/

    // Get CE for a specific calendar
    @Query("SELECT ce FROM CalendarException ce "+
            "WHERE ce.doctorCalendar.id=:doctorCalendarId "+
            "AND ce.isActive= true "+
            "AND ce.scope='SPECIFIC'")
    List<CalendarException>findSpecificByDoctorCalendarIdAndIsActive(@Param("doctorCalendarId") Long doctorCalendarId);



    // Get CE Semi-Global by doctorId
    @Query( "SELECT ce FROM CalendarException ce "+
            "WHERE ce.doctorId=:doctorId "+
            "AND ce.isActive= true "+
            "AND ce.scope='SEMI_GLOBAL'")
    List<CalendarException> findSemiGlobalByDoctorIdAndIsActive(@Param("doctorId") Long doctorId);






    // Get CE Global (se aplican a todos los doctores)
    @Query( "SELECT ce FROM CalendarException ce "+
            "WHERE ce.isActive= true "+
            "AND ce.scope='GLOBAL'")
    List<CalendarException> findAllGlobalAndIsActive();





    /*
     * Get all exceptions for a doctor on a specific date (includes GLOBAL, SEMI_GLOBAL, and SPECIFIC)
     * Considers both single-day and multi-day exceptions
     */
    @Query("SELECT ce FROM CalendarException ce " +
            "WHERE ce.isActive = true " +

            // Date range check: :date falls within [startDate, endDate]
            "AND ce.startDate <= :date " +
            "AND COALESCE(ce.endDate, ce.startDate) >= :date " +

            // Include GLOBAL, SEMI_GLOBAL (same doctor), and SPECIFIC (same doctor)
            "AND (ce.scope = 'GLOBAL' OR ce.doctorId = :doctorId) " +

            "ORDER BY ce.startTime ASC")
    List<CalendarException> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") LocalDate date);




    /*
     * Get all exceptions applicable to a doctor
     * Includes: GLOBAL + SEMI_GLOBAL (same doctor) + SPECIFIC (all calendars of doctor)
     */
    @Query("SELECT ce FROM CalendarException ce " +
            "WHERE ce.isActive = true " +
            "AND (ce.scope = 'GLOBAL' OR ce.doctorId = :doctorId) " +
            "ORDER BY ce.startDate ASC")
    List<CalendarException> findByDoctorId(@Param("doctorId") Long doctorId);




    /*
     * Check if there's a conflicting exception considering:
     * - Date range overlapping (supports single-day and multi-day exceptions)
     * - GLOBAL: affects all doctors/calendars
     * - SEMI_GLOBAL: affects all calendars of the same doctor
     * - SPECIFIC: affects only the specific calendar
     *
     * @param scope The scope of the exception being created/updated
     * @param doctorId The doctor ID (required for SEMI_GLOBAL and SPECIFIC)
     * @param calendarId The calendar ID (required for SPECIFIC, null for others)
     * @param startDate Start date of the exception
     * @param endDate End date of the exception (null for single-day)
     * @param excludeId ID to exclude (for updates), use 0L for creates
     * @return true if conflict exists, false otherwise
     */

    @Query("SELECT CASE WHEN COUNT(ce) > 0 THEN true ELSE false END " +
            "FROM CalendarException ce " +
            "WHERE ce.isActive = true " +
            "AND ce.id != :excludeId " +
            // (existing.start < new.end) && (existing.end > new.start)
            "AND ce.startDate <= COALESCE(CAST(:endDate AS LocalDate), :startDate) " +
            "AND COALESCE(ce.endDate, ce.startDate) >= :startDate " +
            "AND (ce.scope = 'GLOBAL' " +
                "OR (ce.scope = 'SEMI_GLOBAL' AND ce.doctorId = :doctorId) " +
                "OR (ce.scope = 'SPECIFIC' AND ce.doctorCalendar.id = :calendarId)" +
            ")")
    boolean existConflict(
            @Param("doctorId") Long doctorId,
            @Param("calendarId") Long calendarId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId
    );


    /**
     * Find ALL conflicting exceptions for detailed error reporting
     * Can return multiple conflicts: GLOBAL + SEMI_GLOBAL + SPECIFIC simultaneously
     * Ordered by scope (GLOBAL first) then by ID
     */
    @Query("SELECT ce FROM CalendarException ce " +
            "WHERE ce.isActive = true " +
            "AND ce.id != :excludeId " +
            "AND ce.startDate <= COALESCE(CAST(:endDate AS LocalDate), :startDate) " +
            "AND COALESCE(ce.endDate, ce.startDate) >= :startDate " +
            "AND (ce.scope = 'GLOBAL' " +
                "OR (ce.scope = 'SEMI_GLOBAL' AND ce.doctorId = :doctorId) " +
                "OR (ce.scope = 'SPECIFIC' AND ce.doctorCalendar.id = :calendarId)" +
            ") " +
            "ORDER BY ce.scope, ce.id ASC")
    List<CalendarException> findExistingCalendarExceptionCoincidence(
            @Param("doctorId") Long doctorId,
            @Param("calendarId") Long calendarId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId
    );

    Optional<CalendarException> findByIdAndIsActiveTrue(Long id);
}