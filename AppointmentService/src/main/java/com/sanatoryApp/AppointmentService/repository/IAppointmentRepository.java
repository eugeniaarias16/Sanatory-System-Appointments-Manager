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

    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByPatientInsuranceId(Long insuranceId);
    List<Appointment> findByPatientIdAndDate(Long patientId, LocalDateTime date);
    List<Appointment> findByPatientIdAndDateBetween(
            Long patientId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patientId = :patientId " +
            "AND a.date >= :now " +
            "ORDER BY a.date ASC")
    List<Appointment> findUpcomingAppointmentsByPatientId(
            @Param("patientId") Long patientId,
            @Param("now") LocalDateTime now
    );

    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdAndDateBetween(
            Long doctorId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
    List<Appointment> findByDoctorIdAndDoctorCalendarId(Long doctorId, Long calendarId);

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctorId = :doctorId " +
            "AND DATE(a.date) = :today")
    List<Appointment> findTodayAppointmentsByDoctorId(
            @Param("doctorId") Long doctorId,
            @Param("today") LocalDate today
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patientId = :patientId " +
            "AND a.doctorId = :doctorId " +
            "AND DATE(a.date) = :date")
    Optional<Appointment> findAppointmentByPatientIdAndDoctorIdAndDate(
            @Param("patientId") Long patientId,
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDate date
    );



}
