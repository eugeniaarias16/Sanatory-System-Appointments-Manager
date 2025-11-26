package com.sanatoryApp.AppointmentService.controller;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentCreateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentCreateResponseDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentResponseDto;
import com.sanatoryApp.AppointmentService.service.IAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.ServiceUnavailableException;
import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
@Tag(name = "Appointment", description = "Appointment management endpoints")
public class AppointmentController {

    private final IAppointmentService appointmentService;

    @Operation(summary = "Get Appointment by id")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> findAppointmentById(@PathVariable Long id){
        return ResponseEntity.ok(appointmentService.findAppointmentById(id));
    }

    @Operation(summary = "Get Appointment by patient id")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientId(@PathVariable Long patientId){
        return ResponseEntity.ok(appointmentService.findByPatientId(patientId));
    }

    @Operation(summary = "Get Appointment by patient dni")
    @GetMapping("/patient/dni/{patientDni}")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientDni(@PathVariable String patientDni){
        return ResponseEntity.ok(appointmentService.findByPatientDni(patientDni));
    }

    @Operation(summary = "Get Appointment by health insurance id")
    @GetMapping("/insurance/{insuranceId}")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientInsuranceId(@PathVariable Long insuranceId){
        return ResponseEntity.ok(appointmentService.findByPatientInsuranceId(insuranceId));
    }

    @Operation(summary = "Get Appointment by patient id and date")
    @GetMapping("/patient/search-by-date")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientIdAndDate(
            @RequestParam Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return ResponseEntity.ok(appointmentService.findByPatientIdAndDate(patientId, date));
    }

    @Operation(summary = "Get Appointment by patient id and range date")
    @GetMapping("/patient/id/{patientId}/search-date-range")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientIdAndDateBetween(
            @PathVariable Long patientId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(appointmentService.findByPatientIdAndDateBetween(patientId, startDate, endDate));
    }

    @Operation(summary = "Get Appointment by patient dni and range date")
    @GetMapping("/patient/dni/{patientDni}/search-date-range")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientDniAndDateBetween(
            @PathVariable String patientDni,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        return ResponseEntity.ok(appointmentService.findByPatientDniAndDateBetween(patientDni, startDate, endDate));
    }

    @Operation(
            summary = "Get upcoming appointments by patient id",
            description = "Returns future appointments. If date is not provided, uses today's date."
    )
    @GetMapping("/patient/id/{patientId}/upcoming")
    public ResponseEntity<List<AppointmentResponseDto>> findUpcomingAppointmentsByPatientId(
            @PathVariable Long patientId,
            @Parameter(description = "Optional reference date. If not provided, uses today's date")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ){
        if(date == null){
            return ResponseEntity.ok(appointmentService.findUpcomingAppointmentsByPatientId(patientId));
        }
        return ResponseEntity.ok(appointmentService.findUpcomingAppointmentsByPatientId(patientId, date));
    }

    @Operation(
            summary = "Get upcoming appointments by patient dni",
            description = "Returns future appointments. If date is not provided, uses today's date."
    )
    @GetMapping("/patient/dni/{patientDni}/upcoming")
    public ResponseEntity<List<AppointmentResponseDto>> findUpcomingAppointmentsByPatientDni(
            @PathVariable String patientDni,
            @Parameter(description = "Optional reference date. If not provided, uses today's date")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date){

        if(date == null){
            return ResponseEntity.ok(appointmentService.findUpcomingAppointmentsByPatientDni(patientDni));
        }
        return ResponseEntity.ok(appointmentService.findUpcomingAppointmentsByPatientDni(patientDni, date));
    }

    @Operation(summary = "Get Appointment by doctor id")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponseDto>> findByDoctorId(@PathVariable Long doctorId){
        return ResponseEntity.ok(appointmentService.findByDoctorId(doctorId));
    }

    @Operation(summary = "Get Appointment by doctor id and range date")
    @GetMapping("/doctor/{doctorId}/search-date-range")
    public ResponseEntity<List<AppointmentResponseDto>> findByDoctorIdAndDateBetween(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        return ResponseEntity.ok(appointmentService.findByDoctorIdAndDateBetween(doctorId, startDate, endDate));
    }

    @Operation(summary = "Get Appointment by doctor calendar id and doctor id")
    @GetMapping("/doctor/{doctorId}/search-by-calendar")
    public ResponseEntity<List<AppointmentResponseDto>> findByDoctorIdAndDoctorCalendarId(
            @PathVariable Long doctorId,
            @RequestParam Long calendarId){
        return ResponseEntity.ok(appointmentService.findByDoctorIdAndDoctorCalendarId(doctorId, calendarId));
    }

    @Operation(
            summary = "Get today's appointments by doctor id",
            description = "Returns appointments for a specific date. If date is not provided, uses today's date."
    )
    @GetMapping("/doctor/{doctorId}/today")
    public ResponseEntity<List<AppointmentResponseDto>> findTodayAppointmentsByDoctorId(
            @PathVariable Long doctorId
    ){
        return ResponseEntity.ok(appointmentService.findTodayAppointmentsByDoctorId(doctorId));
    }

    @Operation(summary = "Get Appointment by patient id, doctor id and date")
    @GetMapping("/search-specific")
    public ResponseEntity<AppointmentResponseDto> findAppointmentByPatientIdAndDoctorIdAndDate(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return ResponseEntity.ok(appointmentService.findAppointmentByPatientIdAndDoctorIdAndDate(patientId, doctorId, date));
    }

    @Operation(summary = "Create Appointment")
    @PostMapping("/create")
    public ResponseEntity<AppointmentCreateResponseDto> createAppointment(
            @RequestBody AppointmentCreateDto dto) throws ServiceUnavailableException {
        return ResponseEntity.ok(appointmentService.createAppointment(dto));
    }

    @Operation(summary = "Cancel Appointment by id")
    @PatchMapping("/cancel/{id}")
    public ResponseEntity<String> cancelAppointmentById(@PathVariable Long id){
        appointmentService.cancelAppointmentById(id);
        return ResponseEntity.ok("Appointment with id " + id + " successfully cancelled.");
    }

    @Operation(summary = "Cancel Appointment by patient id, doctor id and date")
    @PatchMapping("/cancel")
    public ResponseEntity<String> cancelAppointmentByPatientIdAndDoctorIdAndDate(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        appointmentService.cancelAppointmentByPatientIdAndDoctorIdAndDate(patientId, doctorId, date);
        return ResponseEntity.ok("Appointment with patient id: " + patientId +
                ", doctor id: " + doctorId + " and date: " + date + " successfully cancelled.");
    }
}