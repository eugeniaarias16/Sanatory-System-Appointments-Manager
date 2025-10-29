package com.sanatoryApp.CalendarService.controller;

import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarCreateResponseDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarResponseDto;
import com.sanatoryApp.CalendarService.service.IDoctorCalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctorCalendar")
@RequiredArgsConstructor
public class DoctorCalendarController {

    private final IDoctorCalendarService doctorCalendarService;

    @GetMapping("/{id}")
    public ResponseEntity<DoctorCalendarResponseDto> findDoctorCalendarById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorCalendarService.findDoctorCalendarById(id));
    }

    @GetMapping("/active/doctor/{doctorId}")
    public ResponseEntity<List<DoctorCalendarResponseDto>> findByDoctorIdAndIsActiveTrue(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorCalendarService.findByDoctorIdAndIsActiveTrue(doctorId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorCalendarResponseDto>> findByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorCalendarService.findByDoctorId(doctorId));
    }

    @GetMapping("/active/search")
    public ResponseEntity<DoctorCalendarResponseDto> findByDoctorIdAndName(
            @RequestParam Long doctorId,
            @RequestParam String name) {
        return ResponseEntity.ok(doctorCalendarService.findByDoctorIdAndName(doctorId, name));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DoctorCalendarCreateResponseDto> createDoctorCalendar(
            @Valid @RequestBody DoctorCalendarCreateDto dto) {
        DoctorCalendarCreateResponseDto response = doctorCalendarService.createDoctorCalendar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DoctorCalendarResponseDto> updateDoctorCalendar(
            @PathVariable Long id,
            @Valid @RequestBody DoctorCalendarUpdateDto dto) {
        return ResponseEntity.ok(doctorCalendarService.updateDoctorCalendar(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteDoctorCalendar(@PathVariable Long id) {
        doctorCalendarService.deleteDoctorCalendar(id);
        return ResponseEntity.noContent().build();
    }
}