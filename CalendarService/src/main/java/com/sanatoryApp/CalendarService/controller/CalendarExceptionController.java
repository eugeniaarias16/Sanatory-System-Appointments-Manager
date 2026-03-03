package com.sanatoryApp.CalendarService.controller;

import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.CalendarExceptionResponseDto;
import com.sanatoryApp.CalendarService.service.ICalendarExceptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/calendarException")
@RequiredArgsConstructor
@Tag(name = "Calendar Exception", description = "Calendar Exception management endpoints")
@PreAuthorize("hasRole('SECRETARY')")
public class CalendarExceptionController {

    private final ICalendarExceptionService calendarExceptionService;

    @Operation(summary = "Get Calendar Exception by id")
    @GetMapping("/{id}")
    public ResponseEntity<CalendarExceptionResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(calendarExceptionService.findByIdAndIsActive(id));
    }

    @Operation(summary = "Get all Calendar Exceptions by doctor id (includes GLOBAL, SEMI_GLOBAL and SPECIFIC)")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<CalendarExceptionResponseDto>> findByDoctorId(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(calendarExceptionService.findBydDoctorId(doctorId));
    }

    @Operation(summary = "Get all active GLOBAL Calendar Exceptions")
    @GetMapping("/global")
    public ResponseEntity<List<CalendarExceptionResponseDto>> findAllGlobalAndIsActive() {
        return ResponseEntity.ok(calendarExceptionService.findAllGlobalAndIsActive());
    }

    @Operation(summary = "Get all active SEMI_GLOBAL Calendar Exceptions by doctor id")
    @GetMapping("/semi-global/doctor/{doctorId}")
    public ResponseEntity<List<CalendarExceptionResponseDto>> findSemiGlobalByDoctorIdAndIsActive(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(calendarExceptionService.findSemiGlobalByDoctorIdAndIsActive(doctorId));
    }

    @Operation(summary = "Get all active SPECIFIC Calendar Exceptions by doctor calendar id")
    @GetMapping("/specific/doctorCalendar/{doctorCalendarId}")
    public ResponseEntity<List<CalendarExceptionResponseDto>> findSpecificByDoctorCalendarIdAndIsActive(
            @PathVariable Long doctorCalendarId) {
        return ResponseEntity.ok(calendarExceptionService.findSpecificByDoctorCalendarIdAndIsActive(doctorCalendarId));
    }

    @Operation(summary = "Get all Calendar Exceptions by doctor id and date")
    @GetMapping("/doctor/{doctorId}/date/{date}")
    public ResponseEntity<List<CalendarExceptionResponseDto>> findByDoctorIdAndDate(
            @PathVariable Long doctorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(calendarExceptionService.findByDoctorIdAndDateAndHour(doctorId, date));
    }

    @Operation(summary = "Create a new Calendar Exception")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CalendarExceptionResponseDto> createCalendarException(
            @Valid @RequestBody CalendarExceptionCreateDto dto) {
        CalendarExceptionResponseDto response = calendarExceptionService.createCalendarException(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update Calendar Exception by id")
    @PatchMapping("/{id}")
    public ResponseEntity<CalendarExceptionResponseDto> updateCalendarException(
            @PathVariable Long id,
            @Valid @RequestBody CalendarExceptionUpdateDto dto) {
        return ResponseEntity.ok(calendarExceptionService.updateCalendarException(id, dto));
    }

    @Operation(summary = "Delete Calendar Exception by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteCalendarException(@PathVariable Long id) {
        calendarExceptionService.deleteCalendarExceptionById(id);
        return ResponseEntity.ok("Calendar Exception with id: " + id + " successfully deleted.");
    }
}
