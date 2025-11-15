package com.sanatoryApp.CalendarService.controller;

import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarCreateResponseDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarResponseDto;
import com.sanatoryApp.CalendarService.service.IDoctorCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctorCalendar")
@RequiredArgsConstructor
@Tag(name = "Doctor Calendar", description = "Doctor Calendar management endpoints")
public class DoctorCalendarController {

    private final IDoctorCalendarService doctorCalendarService;

    @Operation(summary = "Get Doctor Calendar by id")
    @GetMapping("/{id}")
    public ResponseEntity<DoctorCalendarResponseDto> findDoctorCalendarById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorCalendarService.findDoctorCalendarById(id));
    }

    @Operation(summary = "Get active Doctor Calendar by doctor id")
    @GetMapping("/active/doctor/{doctorId}")
    public ResponseEntity<List<DoctorCalendarResponseDto>> findByDoctorIdAndIsActiveTrue(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorCalendarService.findByDoctorIdAndIsActiveTrue(doctorId));
    }

    @Operation(summary = "Get Doctor Calendar by doctor id")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<DoctorCalendarResponseDto>> findByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(doctorCalendarService.findByDoctorId(doctorId));
    }

    @Operation(summary = "Get Doctor Calendar by doctor id and name")
    @GetMapping("/active/search")
    public ResponseEntity<DoctorCalendarResponseDto> findByDoctorIdAndName(
            @RequestParam Long doctorId,
            @RequestParam String name) {
        return ResponseEntity.ok(doctorCalendarService.findByDoctorIdAndName(doctorId, name));
    }

    @Operation(summary = "Create new Doctor Calendar ")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<DoctorCalendarCreateResponseDto> createDoctorCalendar(
            @Valid @RequestBody DoctorCalendarCreateDto dto) {
        DoctorCalendarCreateResponseDto response = doctorCalendarService.createDoctorCalendar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update Doctor Calendar by id")
    @PatchMapping("/{id}")
    public ResponseEntity<DoctorCalendarResponseDto> updateDoctorCalendar(
            @PathVariable Long id,
            @Valid @RequestBody DoctorCalendarUpdateDto dto) {
        return ResponseEntity.ok(doctorCalendarService.updateDoctorCalendar(id, dto));
    }

    @Operation(summary = "Delete Doctor Calendar by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteDoctorCalendar(@PathVariable Long id) {
        doctorCalendarService.deleteDoctorCalendar(id);
        return ResponseEntity.noContent().build();
    }
}