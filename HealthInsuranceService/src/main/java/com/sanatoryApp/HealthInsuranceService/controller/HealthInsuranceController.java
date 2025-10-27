package com.sanatoryApp.HealthInsuranceService.controller;

import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.HealthInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.service.IHealthInsuranceService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/healthInsurance")
public class HealthInsuranceController {
    private final IHealthInsuranceService healthInsuranceService;

    /* GET ENDPOINTS */
    @GetMapping("/id/{id}")
    public ResponseEntity  <HealthInsuranceResponseDto> findHealthInsuranceById(@PathVariable Long id){
        return ResponseEntity.ok(healthInsuranceService.findHealthInsuranceById(id));
    }

    @GetMapping("/companyName/{companyName}")
    public ResponseEntity<HealthInsuranceResponseDto> findHealthInsuranceByCompanyName(@PathVariable String companyName){
        return ResponseEntity.ok(healthInsuranceService.findHealthInsuranceByCompanyName(companyName));
    }

    @GetMapping("/companyCode/{companyCode}")
    public ResponseEntity<HealthInsuranceResponseDto> findHealthInsuranceByCompanyCode(@PathVariable Long companyCode){
        return ResponseEntity.ok(healthInsuranceService.findHealthInsuranceByCompanyCode(companyCode));
    }

    @GetMapping("/phoneNumber/{phoneNumber}")
    public ResponseEntity<HealthInsuranceResponseDto> findHealthInsuranceByPhoneNumber(@PathVariable String phoneNumber){
        return ResponseEntity.ok(healthInsuranceService.findHealthInsuranceByPhoneNumber(phoneNumber));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<HealthInsuranceResponseDto> findHealthInsuranceByEmail(@PathVariable String email){
        return ResponseEntity.ok(healthInsuranceService.findHealthInsuranceByEmail(email));
    }

    @GetMapping("search/{name}")
    public ResponseEntity<List<HealthInsuranceResponseDto>> searchByName(@PathVariable String name){
        return ResponseEntity.ok(healthInsuranceService.searchByName(name));
    }

    @GetMapping("/coveragePlan/{insuranceId}")
    public ResponseEntity<List<CoveragePlanResponseDto>> findCoveragePlansByInsuranceId(@PathVariable Long insuranceId){
        return ResponseEntity.ok(healthInsuranceService.findCoveragePlans(insuranceId));
    }

    @GetMapping("/patients/{insuranceId}")
    public ResponseEntity<List<PatientInsuranceResponseDto>> findPatientsByInsuranceId(@PathVariable Long insuranceId){
        return ResponseEntity.ok(healthInsuranceService.findPatientsByInsuranceId(insuranceId));
    }

    @GetMapping("/active/countPatients/{insuranceId}")
    public ResponseEntity<Integer>countActivePatients(@PathVariable Long insuranceId){
        return ResponseEntity.ok(healthInsuranceService.countActivePatients(insuranceId));
    }

    @GetMapping("/active/countPlans/{insuranceId}")
    public ResponseEntity<Integer> countActivePlans(@PathVariable Long insuranceId){
        return ResponseEntity.ok(healthInsuranceService.countActivePlans(insuranceId));
    }

    /* POST ENDPOINT */
    @PostMapping("/create")
    public ResponseEntity <HealthInsuranceResponseDto> createHealthInsurance(@RequestBody @Valid HealthInsuranceCreateDto dto){
        return ResponseEntity.ok(healthInsuranceService.createHealthInsurance(dto));
    }


    /* PATCH ENDPOINT */
    @PatchMapping("/update/{insuranceId}")
    public ResponseEntity<HealthInsuranceResponseDto> updateHealthInsuranceById(@PathVariable Long id,
                                                                                @RequestBody @Valid HealthInsuranceUpdateDto dto){
        return ResponseEntity.ok(healthInsuranceService.updateHealthInsuranceById(id,dto));
    }

    @PatchMapping("/softDelete/{insuranceId}")
    public ResponseEntity<String> softDeleteHealthInsuranceById(@PathVariable Long insuranceId){
        healthInsuranceService.softDeleteHealthInsuranceById(insuranceId);
        return ResponseEntity.ok("Health Insurance with id "+insuranceId+" successfully deactivated(soft deleted)");
    }

    @PatchMapping("/activate/{insuranceId}")
    public ResponseEntity<String>activatedHealthInsuranceById(@PathVariable Long insuranceId){
        healthInsuranceService.activatedHealthInsuranceById(insuranceId);
        return ResponseEntity.ok("Health Insurance with id "+insuranceId+" successfully activated");
    }


    /* DELETE ENDPOINT */
    @DeleteMapping("/delete/{insuranceId}")
    public ResponseEntity<String>deleteHealthInsuranceById(Long insuranceId){
        healthInsuranceService.deleteHealthInsuranceById(insuranceId);
        return ResponseEntity.ok("Health Insurance with id "+insuranceId+" successfully deleted.");
    }

}
