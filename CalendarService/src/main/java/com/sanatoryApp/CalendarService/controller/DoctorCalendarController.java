package com.sanatoryApp.CalendarService.controller;

import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarResponseDto;
import com.sanatoryApp.CalendarService.service.IDoctorCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctorCalendar")
@RequiredArgsConstructor
public class DoctorCalendarController {


    private final IDoctorCalendarService doctorCalendarService;

    /*  GET ENDPOINT  */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorCalendarResponseDto> findDoctorCalendarById(Long id){
        return ResponseEntity.ok(doctorCalendarService.findDoctorCalendarById(id));
    }

    @GetMapping("/active/doctor/{doctorId}")
    public ResponseEntity<List<DoctorCalendarResponseDto>> findByDoctorIdAndIsActiveTrue(Long doctorId){
        return ResponseEntity.ok(doctorCalendarService.findByDoctorIdAndIsActiveTrue(doctorId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorCalendarResponseDto>>findByDoctorId(Long doctorId){
        return ResponseEntity.ok(doctorCalendarService.findByDoctorId(doctorId));
    }

   /*  PUT ENDPOINT  */

    @PutMapping("/create")
    public ResponseEntity<DoctorCalendarResponseDto> createDoctorCalendar(DoctorCalendarCreateDto dto){
        return ResponseEntity.ok(doctorCalendarService.createDoctorCalendar(dto));
    }

   /*  PATCH ENDPOINT */
    @PatchMapping("/update/{id}")
    public ResponseEntity<DoctorCalendarResponseDto> updateDoctorCalendar(Long id, DoctorCalendarUpdateDto dto){
        return ResponseEntity.ok(doctorCalendarService.updateDoctorCalendar(id,dto));
    }

    /*  DELETE ENDPOINT  */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDoctorCalendar(Long id){
        doctorCalendarService.deleteDoctorCalendar(id);
        return ResponseEntity.ok("Doctor Calendar with id "+id+" successfully deleted.");
    }



}
