package com.sanatoryApp.HealthInsuranceService.controller;

import com.sanatoryApp.HealthInsuranceService.dto.Request.PatientInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceCreateResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.service.IPatientInsuranceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patientInsurance")
public class PatientInsuranceController {

    private final IPatientInsuranceService patientInsuranceService;

    @GetMapping("/id/{patientInsuranceId}")
    public ResponseEntity<PatientInsuranceResponseDto> findPatientInsuranceById(@PathVariable Long patientInsuranceId) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceById(patientInsuranceId));
    }

    @GetMapping("/patientDni/{dni}")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientInsuranceByPatientDni(@PathVariable String dni) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByPatientDni(dni));
    }

    @GetMapping("/credentialNumber/{credentialNumber}")
    public ResponseEntity<PatientInsuranceResponseDto> findPatientInsuranceByCredentialNumber(@PathVariable String credentialNumber) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByCredentialNumber(credentialNumber));
    }

    @GetMapping("/healthInsurance/{healthInsuranceId}")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientInsuranceByHealthInsurance(@PathVariable Long healthInsuranceId) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByHealthInsurance(healthInsuranceId));
    }

    @GetMapping("/coveragePlan/{coveragePlanId}")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientInsuranceByCoveragePlanId(@PathVariable Long coveragePlanId) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByCoveragePlanId(coveragePlanId));
    }

    @GetMapping("/createdAt/{date}")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientInsuranceByCreatedAt(@PathVariable LocalDate date) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByCreatedAt(date));
    }

    @GetMapping("/createdAfter/{date}")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientInsuranceByCreatedAfterDate(@PathVariable LocalDate date) {
        return ResponseEntity.ok(patientInsuranceService.findPatientInsuranceByCreatedAfterDate(date));
    }

    @GetMapping("/active/countPatients/{insuranceId}")
    public ResponseEntity<Integer> countActivePatientsByInsuranceId(@PathVariable Long insuranceId) {
        return ResponseEntity.ok(patientInsuranceService.countActivePatientsByInsuranceId(insuranceId));
    }

    @PostMapping("/create")
    public ResponseEntity<PatientInsuranceCreateResponseDto> createPatientInsurance(@RequestBody @Valid PatientInsuranceCreateDto dto) {
        return ResponseEntity.ok(patientInsuranceService.createPatientInsurance(dto));
    }

    @PatchMapping("/update/{patientInsuranceId}")
    public ResponseEntity<PatientInsuranceResponseDto> updatePatientInsuranceCoveragePlanById(@PathVariable Long patientInsuranceId,
                                                                                              @RequestBody @Valid Long coveragePlanId) {
        return ResponseEntity.ok(patientInsuranceService.updatePatientInsuranceCoveragePlanById(patientInsuranceId, coveragePlanId));
    }

    @PatchMapping("/softDelete/{patientInsuranceId}")
    public ResponseEntity<String> softDeletePatientInsuranceById(@PathVariable Long patientInsuranceId) {
        patientInsuranceService.softDeletePatientInsuranceById(patientInsuranceId);
        return ResponseEntity.ok("Patient Insurance with id " + patientInsuranceId + " successfully deactivated (soft deleted).");
    }

    @PatchMapping("/activate/{patientInsuranceId}")
    public ResponseEntity<String> activatePatientInsuranceById(@PathVariable Long patientInsuranceId) {
        patientInsuranceService.activatePatientInsuranceById(patientInsuranceId);
        return ResponseEntity.ok("Patient Insurance with id " + patientInsuranceId + " successfully activated.");
    }

    @DeleteMapping("/delete/{patientInsuranceId}")
    public ResponseEntity<String> deletePatientInsuranceById(@PathVariable Long patientInsuranceId) {
        patientInsuranceService.deletePatientInsuranceById(patientInsuranceId);
        return ResponseEntity.ok("Patient Insurance with id " + patientInsuranceId + " successfully deleted.");
    }
}