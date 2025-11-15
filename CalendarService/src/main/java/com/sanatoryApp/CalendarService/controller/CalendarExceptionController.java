package com.sanatoryApp.CalendarService.controller;

import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.CalendarExceptionResponseDto;
import com.sanatoryApp.CalendarService.service.ICalendarExceptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/calendarException")
@RequiredArgsConstructor
@Tag(name = "Calendar Exception", description = "Calendar Exception management endpoints")
public class CalendarExceptionController {

    private final ICalendarExceptionService calendarExceptionService;

    @Operation(summary = "Get Calendar Exception by id")
    @GetMapping("/{id}")
    public ResponseEntity<CalendarExceptionResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(calendarExceptionService.findById(id));
    }

    @Operation(summary = "Get Calendar Exception by doctor calendar id")
    @GetMapping("/doctorCalendar/{doctorCalendarId}")
    public ResponseEntity<List<CalendarExceptionResponseDto>> findByDoctorCalendarId(
            @PathVariable Long doctorCalendarId) {
        return ResponseEntity.ok(calendarExceptionService.findByDoctorCalendarId(doctorCalendarId));
    }

    @Operation(summary = "Get Calendar Exception by range time")
    @GetMapping("/search/time-range")
    public ResponseEntity<List<CalendarExceptionResponseDto>> findApplicableExceptionsInTimeRange(
            @RequestParam Long calendarId,
            @RequestParam Long doctorId,
            @RequestParam LocalDate startTime,
            @RequestParam LocalDate endTime) {
        return ResponseEntity.ok(calendarExceptionService.findApplicableExceptionsInTimeRange(
                calendarId, doctorId, startTime, endTime));
    }

    @Operation(summary = "Find applicable calendar exceptions for a specific date")
    @GetMapping("/search/calendar-date")
    public ResponseEntity<List<CalendarExceptionResponseDto>> findApplicableExceptionsForCalendar(
            @RequestParam Long calendarId,
            @RequestParam Long doctorId,
            @RequestParam LocalDate date) {
        return ResponseEntity.ok(calendarExceptionService.findApplicableExceptionsForCalendar(
                calendarId, doctorId, date));
    }


    @Operation(summary = "Get Global Calendar Exception by doctor id ")
    @GetMapping("/global/doctor/{doctorId}")
    public ResponseEntity<List<CalendarExceptionResponseDto>> findGlobalExceptionsByDoctorId(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(calendarExceptionService.findGlobalExceptionsByDoctorId(doctorId));
    }

    @Operation(summary = "Get future Calendar Exception by calendar id")
    @GetMapping("/search/future")
    public ResponseEntity<List<CalendarExceptionResponseDto>> findFutureExceptions(
            @RequestParam Long calendarId,
            @RequestParam LocalDate currentDate) {
        return ResponseEntity.ok(calendarExceptionService.findFutureExceptions(calendarId, currentDate));
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCalendarException(@PathVariable Long id) {
        calendarExceptionService.deleteCalendarException(id);
        return ResponseEntity.noContent().build();
    }
}