package com.sanatoryApp.HealthInsuranceService.controller;

import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.service.ICoveragePlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coveragePlan")
@RequiredArgsConstructor
public class CoveragePlanController {

    private final ICoveragePlanService coveragePlanService;

    @GetMapping("/{id}")
    public ResponseEntity<CoveragePlanResponseDto> findCoveragePlanById(@PathVariable Long id) {
        return ResponseEntity.ok(coveragePlanService.findCoveragePlanById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<CoveragePlanResponseDto> createCoveragePlan(
            @Valid @RequestBody CoveragePlanCreateDto dto
    ) {
        return ResponseEntity.ok(coveragePlanService.createCoveragePlan(dto));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<CoveragePlanResponseDto> updateCoveragePlanById(
            @PathVariable Long id,
            @Valid @RequestBody CoveragePlanUpdateDto dto
    ) {
        return ResponseEntity.ok(coveragePlanService.updateCoveragePlanById(id, dto));
    }

    @PatchMapping("/softDelete/{id}")
    public ResponseEntity<String> softDeleteCoveragePlanById(@PathVariable Long id) {
        coveragePlanService.softDeleteCoveragePlanById(id);
        return ResponseEntity.ok("Coverage Plan with id: " + id + " successfully deactivated (soft delete)");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCoveragePlanById(@PathVariable Long id) {
        coveragePlanService.deleteCoveragePlanById(id);
        return ResponseEntity.ok("Coverage Plan with id: " + id + " successfully deleted");
    }

    @GetMapping("/healthInsurance/{healthInsuranceId}")
    public ResponseEntity<List<CoveragePlanResponseDto>> findByHealthInsuranceId(
            @PathVariable Long healthInsuranceId
    ) {
        return ResponseEntity.ok(coveragePlanService.findByHealthInsuranceId(healthInsuranceId));
    }

    @GetMapping("/active/healthInsurance/{healthInsuranceId}")
    public ResponseEntity<List<CoveragePlanResponseDto>> findByHealthInsuranceIdAndIsActiveTrue(
            @PathVariable Long healthInsuranceId
    ) {
        return ResponseEntity.ok(coveragePlanService.findByHealthInsuranceIdAndIsActiveTrue(healthInsuranceId));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CoveragePlanResponseDto> findCoveragePlanByName(@PathVariable String name) {
        return ResponseEntity.ok(coveragePlanService.findCoveragePlanByName(name));
    }

    @GetMapping("/count/activePlans")
    public ResponseEntity<Integer> countActivePlans() {
        return ResponseEntity.ok(coveragePlanService.countActivePlans());
    }

    @GetMapping("/count/active/healthInsurance/{healthInsuranceId}")
    public ResponseEntity<Integer> countActivePlanByHealthInsurance(@PathVariable Long healthInsuranceId) {
        return ResponseEntity.ok(coveragePlanService.countActivePlanByHealthInsurance(healthInsuranceId));
    }
}