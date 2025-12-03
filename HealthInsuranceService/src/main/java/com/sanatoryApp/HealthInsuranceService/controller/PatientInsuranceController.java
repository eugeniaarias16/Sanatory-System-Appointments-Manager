package com.sanatoryApp.HealthInsuranceService.controller;

import com.sanatoryApp.HealthInsuranceService.dto.Request.PatientInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceCreateResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.service.IPatientInsuranceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patientInsurance")
@Tag(name = "Patient Insurance",description = "Operations related to Patient Insurance ")
@PreAuthorize("hasRole('SECRETARY')")
public class PatientInsuranceController {

    private final IPatientInsuranceService patientInsuranceService;

    @Operation(summary = "Get Patient Insurance by Health Insurance id")
    @GetMapping("/id/{patientInsuranceId}")
    public ResponseEntity<PatientInsuranceResponseDto> findPatientInsuranceById(@PathVariable Long patientInsuranceId) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceById(patientInsuranceId));
    }

    @Operation(summary = "Get Patient Insurance by dni")
    @GetMapping("/patientDni/{dni}")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientInsuranceByPatientDni(@PathVariable String dni) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByPatientDni(dni));
    }

    @Operation(summary = "Get Patient Insurance by credential number")
    @GetMapping("/credentialNumber/{credentialNumber}")
    public ResponseEntity<PatientInsuranceResponseDto> findPatientInsuranceByCredentialNumber(@PathVariable String credentialNumber) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByCredentialNumber(credentialNumber));
    }

    @Operation(summary = "Get Patient Insurance by Health Insurance id")
    @GetMapping("/healthInsurance/{healthInsuranceId}")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientInsuranceByHealthInsurance(@PathVariable Long healthInsuranceId) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByHealthInsurance(healthInsuranceId));
    }

    @Operation(summary = "Get Patient Insurance by Coverage Plan id")
    @GetMapping("/coveragePlan/{coveragePlanId}")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientInsuranceByCoveragePlanId(@PathVariable Long coveragePlanId) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByCoveragePlanId(coveragePlanId));
    }

    @Operation(summary = "Get Patient Insurance by created date")
    @GetMapping("/createdAt/{date}")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientInsuranceByCreatedAt(@PathVariable LocalDate date) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByCreatedAt(date));
    }

    @Operation(summary = "Get Patient Insurance after specific date")
    @GetMapping("/createdAfter/{date}")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientInsuranceByCreatedAfterDate(@PathVariable LocalDate date) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByCreatedAfterDate(date));
    }

    @Operation(summary = "Count active Patients by Health Insurance id")
    @GetMapping("/active/countPatients/{insuranceId}")
    public ResponseEntity<Integer> countActivePatientsByInsuranceId(@PathVariable Long insuranceId) {
        return ResponseEntity.ok(patientInsuranceService.countActivePatientsByInsuranceId(insuranceId));
    }

    @Operation(summary = "Create a new Patient Insurance")
    @PostMapping("/create")
    public ResponseEntity<PatientInsuranceCreateResponseDto> createPatientInsurance(@RequestBody @Valid PatientInsuranceCreateDto dto) {
        return ResponseEntity.ok(patientInsuranceService.createPatientInsurance(dto));
    }

    @Operation(summary = "Update Patient Insurance's coverage plan", description = "Update Patient Insurance's coverage plan using patient id and the new coverage plan's id")
    @PatchMapping("/update/{patientInsuranceId}")
    public ResponseEntity<PatientInsuranceResponseDto> updatePatientInsuranceCoveragePlanById(@PathVariable Long patientInsuranceId,
                                                                                              @RequestBody @Valid Long coveragePlanId) {
        return ResponseEntity.ok(patientInsuranceService.updatePatientInsuranceCoveragePlanById(patientInsuranceId, coveragePlanId));
    }

    @Operation(summary = "Deactivate Patient Insurance by id")
    @PatchMapping("/softDelete/{patientInsuranceId}")
    public ResponseEntity<String> softDeletePatientInsuranceById(@PathVariable Long patientInsuranceId) {
        patientInsuranceService.softDeletePatientInsuranceById(patientInsuranceId);
        return ResponseEntity.ok("Patient Insurance with id " + patientInsuranceId + " successfully deactivated (soft deleted).");
    }

    @Operation(summary = "Activate Patient Insurance")
    @PatchMapping("/activate/{patientInsuranceId}")
    public ResponseEntity<String> activatePatientInsuranceById(@PathVariable Long patientInsuranceId) {
        patientInsuranceService.activatePatientInsuranceById(patientInsuranceId);
        return ResponseEntity.ok("Patient Insurance with id " + patientInsuranceId + " successfully activated.");
    }

    @Operation(summary = "Delete Patient Insurance by id")
    @DeleteMapping("/delete/{patientInsuranceId}")
    public ResponseEntity<String> deletePatientInsuranceById(@PathVariable Long patientInsuranceId) {
        patientInsuranceService.deletePatientInsuranceById(patientInsuranceId);
        return ResponseEntity.ok("Patient Insurance with id " + patientInsuranceId + " successfully deleted.");
    }
}