package com.sanatoryApp.UserService.controllers;

import com.sanatoryApp.UserService.dto.Request.PatientCreateDto;
import com.sanatoryApp.UserService.dto.Request.PatientUpdateDto;
import com.sanatoryApp.UserService.dto.Response.PatientResponseDto;
import com.sanatoryApp.UserService.service.IPatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
@Tag(name = "Patient Management",description ="API for managing Patient's information." )
@PreAuthorize("hasRole('SECRETARY')")
public class PatientController {

    private final IPatientService patientService;

    /* =================== GET ENDPOINTS =================== */
    @Operation(summary = "Get all Patients")
    @GetMapping
    public ResponseEntity<List<PatientResponseDto>>findAllPatients(){
        List<PatientResponseDto>list=patientService.findAll();
        return ResponseEntity.ok(list);
    }

    @PreAuthorize("@securityService.isSecretaryOrPatient(#patientId)")
    @Operation(summary = "Get Patient by id")
    @GetMapping("/id/{patientId}")
    public ResponseEntity<PatientResponseDto>findPatientById(@PathVariable Long patientId){
        PatientResponseDto responseDto=patientService.findPatientById(patientId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Get Patient by dni")
    @GetMapping("/dni/{dni}")
    public ResponseEntity<PatientResponseDto>findPatientByDni(@PathVariable String dni){
        PatientResponseDto responseDto=patientService.findPatientByDni(dni);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Get Patient by email")
    @GetMapping("/email/{email}")
    public ResponseEntity<PatientResponseDto>findPatientByEmail(@PathVariable String email){
        PatientResponseDto responseDto=patientService.findPatientByEmail(email);
        return ResponseEntity.ok(responseDto);
    }

    /* =================== POST ENDPOINTS =================== */
    @Operation(summary = "Create a new Patient")
    @PostMapping("/create")
    public ResponseEntity<PatientResponseDto>createPatient(@Valid @RequestBody PatientCreateDto dto){
        PatientResponseDto responseDto=patientService.createPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /* =================== PUT/PATCH ENDPOINTS =================== */
    @PreAuthorize("@securityService.isSecretaryOrPatient(#patientId)")
    @Operation(summary = "Update Patient by id")
    @PutMapping("/update/id/{patientId}")
    public ResponseEntity<PatientResponseDto>updatePatientById(@PathVariable Long patientId,
                                                               @Valid @RequestBody PatientUpdateDto dto){
        PatientResponseDto responseDto=patientService.updatePatientById(patientId,dto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/update/dni/{dni}")
    @Operation(summary = "Update Patient by dni")
    public ResponseEntity<PatientResponseDto>updatePatientByDni(@PathVariable String dni,
                                                                @Valid @RequestBody PatientUpdateDto dto){
        PatientResponseDto responseDto=patientService.updatePatientByDni(dni,dto);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/disable/{dni}")
    @Operation(summary = "Disable Patient by dni")
    public ResponseEntity<String> disablePatientByDni(@PathVariable("dni") String dni){
        patientService.disablePatientByDni(dni);
        return ResponseEntity.ok("Patient with dni "+dni+" successfully disabled.");
    }

    @PatchMapping("/enable/{dni}")
    @Operation(summary = "Enable Patient by dni")
    public ResponseEntity<String> enablePatientByDni(@PathVariable("dni") String dni){
        patientService.enablePatientByDni(dni);
        return ResponseEntity.ok("Patient with dni "+dni+" successfully enabled.");
    }

    /* =================== DELETE ENDPOINTS =================== */
    @Operation(summary = "Delete Patient by id")
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Void>deletePatientById(@PathVariable Long id){
        patientService.deletePatientById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete Patient by dni")
    @DeleteMapping("/delete/dni/{dni}")
    public ResponseEntity<Void>deletePatientByDni(@PathVariable String dni){
        patientService.deletePatientByDni(dni);
        return ResponseEntity.noContent().build();
    }
}