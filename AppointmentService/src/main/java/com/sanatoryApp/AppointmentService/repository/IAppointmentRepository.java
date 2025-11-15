package com.sanatoryApp.AppointmentService.repository;

import com.sanatoryApp.AppointmentService.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface IAppointmentRepository extends JpaRepository<Appointment,Long> {

  /* All these methods are for finding medical appointments with SCHEDULED status */


    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patientId = :patientId " +
            "AND a.status = 'SCHEDULED'")
    List<Appointment> findByPatientId(Long patientId);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patientInsuranceId = :insuranceId " +
            "AND a.status = 'SCHEDULED'")
    List<Appointment> findByPatientInsuranceId(Long insuranceId);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patientId = :patientId " +
            "AND a.date = :date " +
            "AND a.status = 'SCHEDULED'")
    List<Appointment> findByPatientIdAndDate(Long patientId, LocalDateTime date);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patientId = :patientId " +
            "AND a.date >= :startDate " +
            "AND a.date < :endDate " +
            "AND a.status = 'SCHEDULED' " +
            "ORDER BY a.date ASC")
    List<Appointment> findByPatientIdAndDateBetween(
            Long patientId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );


    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patientId = :patientId " +
            "AND a.date >= :now " +
            "AND a.status = 'SCHEDULED' " +
            "ORDER BY a.date ASC")
    List<Appointment> findUpcomingAppointmentsByPatientId(
            @Param("patientId") Long patientId,
            @Param("now") LocalDateTime now
    );

    @Query("SELECT a FROM Appointment a "+
            "WHERE a.doctorId=:doctorId "+
            "AND a.status='SCHEDULED'")
    List<Appointment> findByDoctorId(Long doctorId);


    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctorId = :doctorId " +
            "AND a.date >= :startDate " +
            "AND a.date < :endDate " +
            "AND a.status = 'SCHEDULED' " +
            "ORDER BY a.date ASC")
    List<Appointment> findByDoctorIdAndDateBetween(
            Long doctorId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctorId = :doctorId " +
            "AND a.doctorCalendarId = :calendarId " +
            "AND a.status = 'SCHEDULED' " +
            "ORDER BY a.date ASC")
    List<Appointment> findByDoctorIdAndDoctorCalendarId(Long doctorId, Long calendarId);


    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctorId = :doctorId " +
            "AND DATE(a.date) = :today " +    // ← Espacio después de =
            "AND a.status = 'SCHEDULED'")
    List<Appointment> findTodayAppointmentsByDoctorId(
            @Param("doctorId") Long doctorId,
            @Param("today") LocalDate today
    );


    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patientId = :patientId " +
            "AND a.doctorId = :doctorId " +
            "AND DATE(a.date) = :date " +     // ← Espacio después de =
            "AND a.status = 'SCHEDULED'")
    Optional<Appointment> findAppointmentByPatientIdAndDoctorIdAndDate(
            @Param("patientId") Long patientId,
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date
    );

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
            "WHERE a.patientId = :patientId " +
            "AND a.doctorId = :doctorId " +
            "AND a.date = :date " +
            "AND a.status = 'SCHEDULED'")
    boolean existsByPatientIdAndDoctorIdAndDate(
            Long patientId,
            Long doctorId,
            LocalDateTime date
    );
}
