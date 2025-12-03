package com.sanatoryApp.HealthInsuranceService.controller;

import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.service.ICoveragePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coveragePlan")
@RequiredArgsConstructor
@Tag(name = "Coverage Plan", description = "Coverage Plan management endpoints")
@PreAuthorize("hasRole('SECRETARY')")
public class CoveragePlanController {

    private final ICoveragePlanService coveragePlanService;

    @Operation(summary = "Get Coverage Plan by id ")
    @GetMapping("/{id}")
    public ResponseEntity<CoveragePlanResponseDto> findCoveragePlanById(@PathVariable Long id) {
        return ResponseEntity.ok(coveragePlanService.findCoveragePlanById(id));
    }

    @Operation(summary = "Create a new Coverage Plan")
    @PostMapping("/create")
    public ResponseEntity<CoveragePlanResponseDto> createCoveragePlan(
            @Valid @RequestBody CoveragePlanCreateDto dto
    ) {
        return ResponseEntity.ok(coveragePlanService.createCoveragePlan(dto));
    }

    @Operation(summary = "Update Coverage Plan by id")
    @PatchMapping("/update/{id}")
    public ResponseEntity<CoveragePlanResponseDto> updateCoveragePlanById(
            @PathVariable Long id,
            @Valid @RequestBody CoveragePlanUpdateDto dto
    ) {
        return ResponseEntity.ok(coveragePlanService.updateCoveragePlanById(id, dto));
    }

    @Operation(summary = "Deactivate Coverage Plan by id")
    @PatchMapping("/softDelete/{id}")
    public ResponseEntity<String> softDeleteCoveragePlanById(@PathVariable Long id) {
        coveragePlanService.softDeleteCoveragePlanById(id);
        return ResponseEntity.ok("Coverage Plan with id: " + id + " successfully deactivated (soft delete)");
    }

    @Operation(summary = "Delete Coverage Plan by Id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCoveragePlanById(@PathVariable Long id) {
        coveragePlanService.deleteCoveragePlanById(id);
        return ResponseEntity.ok("Coverage Plan with id: " + id + " successfully deleted");
    }

    @Operation(summary = "Get all Coverage Plan by Health Insurance id ")
    @GetMapping("/healthInsurance/{healthInsuranceId}")
    public ResponseEntity<List<CoveragePlanResponseDto>> findByHealthInsuranceId(
            @PathVariable Long healthInsuranceId
    ) {
        return ResponseEntity.ok(coveragePlanService.findByHealthInsuranceId(healthInsuranceId));
    }

    @Operation(summary = "Get all actives Coverage Plan by Health Insurance id ")
    @GetMapping("/active/healthInsurance/{healthInsuranceId}")
    public ResponseEntity<List<CoveragePlanResponseDto>> findByHealthInsuranceIdAndIsActiveTrue(
            @PathVariable Long healthInsuranceId
    ) {
        return ResponseEntity.ok(coveragePlanService.findByHealthInsuranceIdAndIsActiveTrue(healthInsuranceId));
    }

    @Operation(summary = "Get Coverage Plan by name")
    @GetMapping("/name/{name}")
    public ResponseEntity<CoveragePlanResponseDto> findCoveragePlanByName(@PathVariable String name) {
        return ResponseEntity.ok(coveragePlanService.findCoveragePlanByName(name));
    }

    @Operation(summary = "Count active Coverage Plans")
    @GetMapping("/count/activePlans")
    public ResponseEntity<Integer> countActivePlans() {
        return ResponseEntity.ok(coveragePlanService.countActivePlans());
    }

    @Operation(summary = "Count active Coverage Plans by health insurance")
    @GetMapping("/count/active/healthInsurance/{healthInsuranceId}")
    public ResponseEntity<Integer> countActivePlanByHealthInsurance(@PathVariable Long healthInsuranceId) {
        return ResponseEntity.ok(coveragePlanService.countActivePlanByHealthInsurance(healthInsuranceId));
    }
}