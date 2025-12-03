package com.sanatoryApp.CalendarService.controller;

import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.AvailabilityPatternResponseDto;
import com.sanatoryApp.CalendarService.service.IAvailabilityPatternService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/availabilityPattern")
@RequiredArgsConstructor
@Tag(name = "Availability Pattern", description = "Availability Pattern management endpoints")
@PreAuthorize("hasRole('SECRETARY')")
public class AvailabilityPatternController {

    private final IAvailabilityPatternService availabilityPatternService;

    @Operation(summary = "Get Availability Pattern by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<AvailabilityPatternResponseDto> findAvailabilityPatternById(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityPatternService.findAvailabilityPatternById(id));
    }

    @Operation(summary = "Get Availability Pattern by Doctor Calendar id")
    @GetMapping("/doctorCalendar/{id}")
    public ResponseEntity<List<AvailabilityPatternResponseDto>> findAvailabilityPatternByDoctorCalendarId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(availabilityPatternService.findAvailabilityPatternByDoctorCalendarId(id));
    }

    @Operation(summary = "Get active Availability Pattern by Doctor Calendar and Day of Week")
    @GetMapping("/search/calendarAndDay")
    public ResponseEntity<List<AvailabilityPatternResponseDto>> findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(
            @RequestParam Long calendarId,
            @RequestParam DayOfWeek dayOfWeek
    ) {
        return ResponseEntity.ok(
                availabilityPatternService.findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(calendarId, dayOfWeek)
        );
    }

    @Operation(summary = "Get Availability Pattern by doctor id")
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AvailabilityPatternResponseDto>> findByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(availabilityPatternService.findByDoctorId(doctorId));
    }

    @Operation(summary = "Get Availability Pattern doctor id and day of week")
    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}")
    public ResponseEntity<List<AvailabilityPatternResponseDto>> findByDoctorIdAndDay(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek
    ) {
        return ResponseEntity.ok(availabilityPatternService.findByDoctorIdAndDay(doctorId, dayOfWeek));
    }

    @Operation(summary = "Create Availability Pattern")
    @PostMapping("/create")
    public ResponseEntity<AvailabilityPatternResponseDto> createAvailabilityPattern(
            @RequestBody @Valid AvailabilityPatternCreateDto dto
    ) {
        return ResponseEntity.ok(availabilityPatternService.createAvailabilityPattern(dto));
    }

    @Operation(summary = "Update Availability Pattern by id")
    @PatchMapping("/update/{id}")
    public ResponseEntity<AvailabilityPatternResponseDto> updateAvailabilityPatternById(
            @PathVariable Long id,
            @RequestBody @Valid AvailabilityPatternUpdateDto dto
    ) {
        return ResponseEntity.ok(availabilityPatternService.updateAvailabilityPatternById(id, dto));
    }

    @Operation(summary = "Deactivate Availability Pattern by id")
    @PatchMapping("/softDelete/{id}")
    public ResponseEntity<String> softDeleteAvailabilityPatternById(@PathVariable Long id) {
        availabilityPatternService.softDeleteAvailabilityPatternById(id);
        return ResponseEntity.ok("Availability Pattern with id " + id + " successfully deactivated.");
    }

    @Operation(summary = "Delete Availability Pattern by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAvailabilityPatternById(@PathVariable Long id) {
        availabilityPatternService.deleteAvailabilityPatternById(id);
        return ResponseEntity.ok("Availability Pattern with id " + id + " successfully deleted.");
    }
}