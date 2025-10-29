package com.sanatoryApp.AppointmentService.controller;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeCreateDto;
import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeUpdateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentTypeResponseDto;
import com.sanatoryApp.AppointmentService.service.IAppointmentTypeService;
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
public class AppointmentTypeController {

    private final IAppointmentTypeService appointmentTypeService;

    /* ========== GET ENDPOINTS ========== */

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentTypeResponseDto> findAppointmentTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<AppointmentTypeResponseDto> findAppointmentTypeByName(@PathVariable String name) {
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeByName(name));
    }


    @GetMapping("/search/name")
    public ResponseEntity<List<AppointmentTypeResponseDto>> findAppointmentTypeByLikeName(
            @RequestParam String name) {
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeByLikeName(name));
    }


    @GetMapping("/search/price-range")
    public ResponseEntity<List<AppointmentTypeResponseDto>> findAppointmentTypeByRangeBasePrice(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeByRangeBasePrice(minPrice, maxPrice));
    }

    /* ========== POST ENDPOINTS ========== */

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AppointmentTypeResponseDto> createAppointmentType(
            @Valid @RequestBody AppointmentTypeCreateDto dto) {
        AppointmentTypeResponseDto response = appointmentTypeService.createAppointmentType(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /* ========== PATCH ENDPOINTS ========== */

    @PatchMapping("/{id}")
    public ResponseEntity<AppointmentTypeResponseDto> updateAppointmentType(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentTypeUpdateDto dto) {
        return ResponseEntity.ok(appointmentTypeService.updateAppointmentType(id, dto));
    }

    /* ========== DELETE ENDPOINTS ========== */

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteAppointmentType(@PathVariable Long id) {
        appointmentTypeService.deleteAppointmentType(id);
        return ResponseEntity.noContent().build();
    }
}