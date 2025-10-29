package com.sanatoryApp.CalendarService.controller;

import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.AvailabilityPatternResponseDto;
import com.sanatoryApp.CalendarService.service.IAvailabilityPatternService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.util.List;

@RestController
@RequestMapping("/availabilityPattern")
@RequiredArgsConstructor
public class AvailabilityPatternController {

    private final IAvailabilityPatternService availabilityPatternService;

    @GetMapping("/id/{id}")
    public ResponseEntity<AvailabilityPatternResponseDto> findAvailabilityPatternById(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityPatternService.findAvailabilityPatternById(id));
    }

    @GetMapping("/doctorCalendar/{id}")
    public ResponseEntity<List<AvailabilityPatternResponseDto>> findAvailabilityPatternByDoctorCalendarId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(availabilityPatternService.findAvailabilityPatternByDoctorCalendarId(id));
    }

    @GetMapping("/search/calendarAndDay")
    public ResponseEntity<List<AvailabilityPatternResponseDto>> findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(
            @RequestParam Long calendarId,
            @RequestParam DayOfWeek dayOfWeek
    ) {
        return ResponseEntity.ok(
                availabilityPatternService.findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(calendarId, dayOfWeek)
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AvailabilityPatternResponseDto>> findByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(availabilityPatternService.findByDoctorId(doctorId));
    }

    @GetMapping("/doctor/{doctorId}/day/{dayOfWeek}")
    public ResponseEntity<List<AvailabilityPatternResponseDto>> findByDoctorIdAndDay(
            @PathVariable Long doctorId,
            @PathVariable DayOfWeek dayOfWeek
    ) {
        return ResponseEntity.ok(availabilityPatternService.findByDoctorIdAndDay(doctorId, dayOfWeek));
    }

    @PostMapping("/create")
    public ResponseEntity<AvailabilityPatternResponseDto> createAvailabilityPattern(
            @RequestBody @Valid AvailabilityPatternCreateDto dto
    ) {
        return ResponseEntity.ok(availabilityPatternService.createAvailabilityPattern(dto));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<AvailabilityPatternResponseDto> updateAvailabilityPatternById(
            @PathVariable Long id,
            @RequestBody @Valid AvailabilityPatternUpdateDto dto
    ) {
        return ResponseEntity.ok(availabilityPatternService.updateAvailabilityPatternById(id, dto));
    }

    @PatchMapping("/softDelete/{id}")
    public ResponseEntity<String> softDeleteAvailabilityPatternById(@PathVariable Long id) {
        availabilityPatternService.softDeleteAvailabilityPatternById(id);
        return ResponseEntity.ok("Availability Pattern with id " + id + " successfully deactivated.");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAvailabilityPatternById(@PathVariable Long id) {
        availabilityPatternService.deleteAvailabilityPatternById(id);
        return ResponseEntity.ok("Availability Pattern with id " + id + " successfully deleted.");
    }
}