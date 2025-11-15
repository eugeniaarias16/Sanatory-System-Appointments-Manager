package com.sanatoryApp.AppointmentService.controller;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeCreateDto;
import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeUpdateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentTypeResponseDto;
import com.sanatoryApp.AppointmentService.service.IAppointmentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("appointmentTypes")
@RequiredArgsConstructor
@Tag(name = "Appointment Type", description = "Appointment Type management endpoints")
public class AppointmentTypeController {

    private final IAppointmentTypeService appointmentTypeService;

    /* ========== GET ENDPOINTS ========== */

    @Operation(summary = "Get Appointment Type by id")
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentTypeResponseDto> findAppointmentTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeById(id));
    }

    @Operation(summary = "Get Appointment Type by name")
    @GetMapping("/name/{name}")
    public ResponseEntity<AppointmentTypeResponseDto> findAppointmentTypeByName(@PathVariable String name) {
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeByName(name));
    }


    @Operation(summary = "Get Appointment Type by containing the name")
    @GetMapping("/search/name")
    public ResponseEntity<List<AppointmentTypeResponseDto>> findAppointmentTypeByLikeName(
            @RequestParam String name) {
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeByLikeName(name));
    }


    @Operation(summary = "Get Appointment Type by range price")
    @GetMapping("/search/price-range")
    public ResponseEntity<List<AppointmentTypeResponseDto>> findAppointmentTypeByRangeBasePrice(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeByRangeBasePrice(minPrice, maxPrice));
    }

    /* ========== POST ENDPOINTS ========== */

    @Operation(summary = "Creating new Appointment Type")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppointmentTypeResponseDto> createAppointmentType(
            @Valid @RequestBody AppointmentTypeCreateDto dto) {
        AppointmentTypeResponseDto response = appointmentTypeService.createAppointmentType(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* ========== PATCH ENDPOINTS ========== */

    @Operation(summary = "Update Appointment Type by id")
    @PatchMapping("/{id}")
    public ResponseEntity<AppointmentTypeResponseDto> updateAppointmentType(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentTypeUpdateDto dto) {
        return ResponseEntity.ok(appointmentTypeService.updateAppointmentType(id, dto));
    }

    /* ========== DELETE ENDPOINTS ========== */

    @Operation(summary = "Delete Appointment Type by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAppointmentType(@PathVariable Long id) {
        appointmentTypeService.deleteAppointmentType(id);
        return ResponseEntity.noContent().build();
    }
}