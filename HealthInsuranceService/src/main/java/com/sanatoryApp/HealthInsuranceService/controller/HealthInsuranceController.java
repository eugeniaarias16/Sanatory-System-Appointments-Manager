package com.sanatoryApp.HealthInsuranceService.controller;

import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.HealthInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.service.ICoveragePlanService;
import com.sanatoryApp.HealthInsuranceService.service.IHealthInsuranceService;
import com.sanatoryApp.HealthInsuranceService.service.IPatientInsuranceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/health-insurances")
@Tag(name = "Health Insurance", description = "Health Insurance management endpoints")
@PreAuthorize("hasRole('SECRETARY')")
public class HealthInsuranceController {

    private final IHealthInsuranceService healthInsuranceService;
    private final ICoveragePlanService coveragePlanService;
    private  final IPatientInsuranceService patientInsuranceService;

    @Operation(summary = "Get health insurance by ID")
    @GetMapping("/{id}")
    public ResponseEntity<HealthInsuranceResponseDto> findHealthInsuranceById(@PathVariable Long id) {
        return ResponseEntity.ok(healthInsuranceService.findHealthInsuranceById(id));
    }

    @GetMapping("/company-name/{companyName}")
    @Operation(summary = "Get health insurance by company name")
    public ResponseEntity<HealthInsuranceResponseDto> findHealthInsuranceByCompanyName(
            @PathVariable String companyName) {
        return ResponseEntity.ok(healthInsuranceService.findHealthInsuranceByCompanyName(companyName));
    }

    @GetMapping("/company-code/{companyCode}")
    @Operation(summary = "Get health insurance by company code")
    public ResponseEntity<HealthInsuranceResponseDto> findHealthInsuranceByCompanyCode(
            @PathVariable Long companyCode) {
        return ResponseEntity.ok(healthInsuranceService.findHealthInsuranceByCompanyCode(companyCode));
    }

    @GetMapping("/phone/{phoneNumber}")
    @Operation(summary = "Get health insurance by phone number")
    public ResponseEntity<HealthInsuranceResponseDto> findHealthInsuranceByPhoneNumber(
            @PathVariable String phoneNumber) {
        return ResponseEntity.ok(healthInsuranceService.findHealthInsuranceByPhoneNumber(phoneNumber));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get health insurance by email")
    public ResponseEntity<HealthInsuranceResponseDto> findHealthInsuranceByEmail(@PathVariable String email) {
        return ResponseEntity.ok(healthInsuranceService.findHealthInsuranceByEmail(email));
    }

    @GetMapping("/search")
    @Operation(summary = "Search health insurances by name")
    public ResponseEntity<List<HealthInsuranceResponseDto>> searchByName(
            @RequestParam(name = "name") String name) {
        return ResponseEntity.ok(healthInsuranceService.searchByName(name));
    }

    @GetMapping("/{insuranceId}/coverage-plans")
    @Operation(summary = "Get all coverage plans for a health insurance")
    public ResponseEntity<List<CoveragePlanResponseDto>> findCoveragePlans(@PathVariable Long insuranceId) {
       List<CoveragePlanResponseDto>plans= coveragePlanService.findByHealthInsuranceIdAndIsActiveTrue(insuranceId);
       return ResponseEntity.ok(plans);
    }

    @GetMapping("/{insuranceId}/patients")
    @Operation(summary = "Get all patients for a health insurance")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientsByInsuranceId(
            @PathVariable Long insuranceId) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByHealthInsurance(insuranceId));
    }

    @GetMapping("/{insuranceId}/active-patients-count")
    @Operation(summary = "Count active patients for a health insurance")
    public ResponseEntity<Integer> countActivePatients(@PathVariable Long insuranceId) {
        return ResponseEntity.ok(patientInsuranceService.countActivePatientsByInsuranceId(insuranceId));
    }

    @GetMapping("/{insuranceId}/active-plans-count")
    @Operation(summary = "Count active coverage plans for a health insurance")
    public ResponseEntity<Integer> countActivePlans(@PathVariable Long insuranceId) {
        return ResponseEntity.ok(coveragePlanService.countActivePlanByHealthInsurance(insuranceId));
    }

    @PostMapping
    @Operation(summary = "Create a new health insurance")
    @ApiResponse(responseCode = "201", description = "Health insurance created successfully")
    public ResponseEntity<HealthInsuranceResponseDto> createHealthInsurance(
            @RequestBody @Valid HealthInsuranceCreateDto dto) {
        HealthInsuranceResponseDto created = healthInsuranceService.createHealthInsurance(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @PatchMapping("/{insuranceId}")
    @Operation(summary = "Update an existing health insurance")
    public ResponseEntity<HealthInsuranceResponseDto> updateHealthInsuranceById(
            @PathVariable Long insuranceId,
            @RequestBody @Valid HealthInsuranceUpdateDto dto) {
        return ResponseEntity.ok(healthInsuranceService.updateHealthInsuranceById(insuranceId, dto));
    }

    @PatchMapping("/{insuranceId}/deactivate")
    @Operation(summary = "Soft delete (deactivate) a health insurance")
    public ResponseEntity<Void> softDeleteHealthInsuranceById(@PathVariable Long insuranceId) {
        healthInsuranceService.softDeleteHealthInsuranceById(insuranceId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{insuranceId}/activate")
    @Operation(summary = "Activate a health insurance")
    public ResponseEntity<Void> activateHealthInsuranceById(@PathVariable Long insuranceId) {
        healthInsuranceService.activateHealthInsuranceById(insuranceId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{insuranceId}")
    @Operation(summary = "Permanently delete a health insurance")
    @ApiResponse(responseCode = "204", description = "Health insurance deleted successfully")
    public ResponseEntity<Void> deleteHealthInsuranceById(@PathVariable Long insuranceId) {
        healthInsuranceService.deleteHealthInsuranceById(insuranceId);
        return ResponseEntity.noContent().build();
    }
}