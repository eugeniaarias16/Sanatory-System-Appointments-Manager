package com.sanatoryApp.AppointmentService.controller;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentCreateDto;
import com.sanatoryApp.AppointmentService.dto.Request.AppointmentUpdateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentResponseDto;
import com.sanatoryApp.AppointmentService.service.IAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final IAppointmentService appointmentService;

    /*  GET ENDPOINT  */

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDto> findAppointmentById(@PathVariable Long id){
        return ResponseEntity.ok(appointmentService.findAppointmentById(id));
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientId(@PathVariable Long patientId){
        return ResponseEntity.ok(appointmentService.findByPatientId(patientId));
    }

    @GetMapping("/insurance/{insuranceId}")
    public ResponseEntity<List<AppointmentResponseDto>>findByPatientInsuranceId(@PathVariable Long insuranceId){
        return ResponseEntity.ok(appointmentService.findByPatientInsuranceId(insuranceId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AppointmentResponseDto>> findByPatientIdAndDate(@RequestParam Long patientId,
                                                                               @RequestParam Date date){
        return ResponseEntity.ok(appointmentService.findByPatientIdAndDate(patientId,date));
    }

    /*  PUT ENDPOINT  */

    @PutMapping("/create")
    public ResponseEntity<AppointmentResponseDto> createAppointment(@RequestBody AppointmentCreateDto dto){
        return ResponseEntity.ok(appointmentService.createAppointment(dto));
    }


    /*  PATCH ENDPOINT  */
    @PatchMapping("/update/{id}")
    public ResponseEntity<AppointmentResponseDto> updateAppointment(@PathVariable Long id,
                                                                    @RequestBody AppointmentUpdateDto dto){
        return ResponseEntity.ok(appointmentService.updateAppointment(id,dto));
    }


    @PatchMapping("/cancelled/{id}")
    public ResponseEntity<String> cancelledAppointmentById(@PathVariable Long id){
        appointmentService.cancelledAppointmentById(id);
        return ResponseEntity.ok("Appointment with id "+id+" successfully cancelled.");
    }


    @PatchMapping("/cancelled")
    public ResponseEntity<String>  cancelledAppointmentByPatientIdAndDoctorIdAndDate(@RequestParam Long patientId,
                                                                                     @RequestParam Long doctorId,
                                                                                     @RequestParam Date date){
        appointmentService.cancelledAppointmentByPatientIdAndDoctorIdAndDate(patientId,doctorId,date);
        return ResponseEntity.ok("Appointment with patient id: "+patientId+" ,doctor id: "+doctorId+" and date: "+date+" successfully cancelled.");
    }

}
