package com.sanatoryApp.AppointmentService.controller;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentCreateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentCreateResponseDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentResponseDto;
import com.sanatoryApp.AppointmentService.service.IAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final IAppointmentService appointmentService;

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> findAppointmentById(@PathVariable Long id){
        return ResponseEntity.ok(appointmentService.findAppointmentById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientId(@PathVariable Long patientId){
        return ResponseEntity.ok(appointmentService.findByPatientId(patientId));
    }

    @GetMapping("/insurance/{insuranceId}")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientInsuranceId(@PathVariable Long insuranceId){
        return ResponseEntity.ok(appointmentService.findByPatientInsuranceId(insuranceId));
    }

    @GetMapping("/patient/search-by-date")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientIdAndDate(
            @RequestParam Long patientId,
            @RequestParam LocalDateTime date){
        return ResponseEntity.ok(appointmentService.findByPatientIdAndDate(patientId, date));
    }

    @GetMapping("/patient/search-date-range")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientIdAndDateBetween(
            @RequestParam Long patientId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
    ){
        return ResponseEntity.ok(appointmentService.findByPatientIdAndDateBetween(patientId, startDate, endDate));
    }

    @GetMapping("/patient/upcoming")
    public ResponseEntity<List<AppointmentResponseDto>> findUpcomingAppointmentsByPatientId(
            @RequestParam Long patientId,
            @RequestParam LocalDateTime now
    ){
        return ResponseEntity.ok(appointmentService.findUpcomingAppointmentsByPatientId(patientId, now));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDto>> findByDoctorId(@PathVariable Long doctorId){
        return ResponseEntity.ok(appointmentService.findByDoctorId(doctorId));
    }

    @GetMapping("/doctor/search-date-range")
    public ResponseEntity<List<AppointmentResponseDto>> findByDoctorIdAndDateBetween(
            @RequestParam Long doctorId,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
    ){
        return ResponseEntity.ok(appointmentService.findByDoctorIdAndDateBetween(doctorId, startDate, endDate));
    }

    @GetMapping("/doctor/search-by-calendar")
    public ResponseEntity<List<AppointmentResponseDto>> findByDoctorIdAndDoctorCalendarId(
            @RequestParam Long doctorId,
            @RequestParam Long calendarId){
        return ResponseEntity.ok(appointmentService.findByDoctorIdAndDoctorCalendarId(doctorId, calendarId));
    }

    @GetMapping("/doctor/today")
    public ResponseEntity<List<AppointmentResponseDto>> findTodayAppointmentsByDoctorId(
            @RequestParam Long doctorId,
            @RequestParam LocalDate today
    ){
        return ResponseEntity.ok(appointmentService.findTodayAppointmentsByDoctorId(doctorId, today));
    }

    @GetMapping("/search-specific")
    public ResponseEntity<AppointmentResponseDto> findAppointmentByPatientIdAndDoctorIdAndDate(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam LocalDate date){
        return ResponseEntity.ok(appointmentService.findAppointmentByPatientIdAndDoctorIdAndDate(patientId, doctorId, date));
    }

    @PostMapping("/create")
    public ResponseEntity<AppointmentCreateResponseDto> createAppointment(@RequestBody AppointmentCreateDto dto) throws ServiceUnavailableException {
        return ResponseEntity.ok(appointmentService.createAppointment(dto));
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<String> cancelAppointmentById(@PathVariable Long id){
        appointmentService.cancelAppointmentById(id);
        return ResponseEntity.ok("Appointment with id " + id + " successfully cancelled.");
    }

    @PatchMapping("/cancel")
    public ResponseEntity<String> cancelAppointmentByPatientIdAndDoctorIdAndDate(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam LocalDate date){
        appointmentService.cancelAppointmentByPatientIdAndDoctorIdAndDate(patientId, doctorId, date);
        return ResponseEntity.ok("Appointment with patient id: " + patientId + ", doctor id: " + doctorId + " and date: " + date + " successfully cancelled.");
    }

}
