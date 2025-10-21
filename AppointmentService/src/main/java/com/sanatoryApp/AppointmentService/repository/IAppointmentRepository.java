package com.sanatoryApp.AppointmentService.repository;

import com.sanatoryApp.AppointmentService.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentRepository extends JpaRepository<Appointment,Long> {

    //SEARCH BY PATIENT
    List<Appointment>findByPatientId(Long patientId);
    List<Appointment>findByPatientInsuranceId(Long insuranceId);
    List<Appointment> findByPatientIdAndDate(Long patientId, LocalDateTime date);
    List<Appointment> findByPatientIdAndAppointmentDateBetween(
            Long patientId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Appointment> findUpcomingAppointmentsByPatientId(
            @Param("patientId") Long patientId,
            @Param("now") LocalDateTime now
    );


    //SEARCH BY DOCTOR
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdAndAppointmentDateBetween(
            Long doctorId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );
    List<Appointment> findByDoctorIdAndDoctorCalendarId(Long doctorId, Long calendarId);

    List<Appointment> findTodayAppointmentsByDoctorId(
            @Param("doctorId") Long doctorId,
            @Param("today") LocalDate today
    );


    @Query( "UPDATE Appointment a "+
            "SET a.status=AppointmentStatus.CANCELLED "+
            "WHERE a.id=:id ")
    void cancelAppointmentById(@Param("id") Long id);

    @Query( "UPDATE Appointment"+
            "SET status=AppointmentStatus.CANCELLED"+
            "WHERE patientId=:patientId AND doctorId=:doctorCalendarId AND date=:date")
    void cancelAppointmentByPatientIdAndDoctorIdAndDate(@Param("patientId") Long patientId, @Param("doctorId") Long doctorCalendarId, @Param("date") LocalDateTime date);


}
