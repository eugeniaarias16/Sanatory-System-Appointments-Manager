package com.sanatoryApp.UserService.controllers;

import com.sanatoryApp.UserService.dto.Request.PatientCreateDto;
import com.sanatoryApp.UserService.dto.Request.PatientUpdateDto;
import com.sanatoryApp.UserService.dto.Response.PatientResponseDto;
import com.sanatoryApp.UserService.service.IPatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final IPatientService patientService;

    /* =================== GET ENDPOINTS =================== */
    @GetMapping
    public ResponseEntity<List<PatientResponseDto>>findAllPatients(){
        List<PatientResponseDto>list=patientService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<PatientResponseDto>findPatientById(@PathVariable Long id){
        PatientResponseDto responseDto=patientService.findPatientById(id);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<PatientResponseDto>findPatientByDni(@PathVariable String dni){
        PatientResponseDto responseDto=patientService.findPatientByDni(dni);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PatientResponseDto>findPatientByEmail(@PathVariable String email){
        PatientResponseDto responseDto=patientService.findPatientByEmail(email);
        return ResponseEntity.ok(responseDto);
    }

    /* =================== POST ENDPOINTS =================== */
    @PostMapping("/create")
    public ResponseEntity<PatientResponseDto>createPatient(@Valid @RequestBody PatientCreateDto dto){
        PatientResponseDto responseDto=patientService.createPatient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /* =================== PUT ENDPOINTS =================== */
    @PutMapping("/update/id/{id}")
    public ResponseEntity<PatientResponseDto>updatePatientById(@PathVariable Long id,
                                                               @Valid @RequestBody PatientUpdateDto dto){
        PatientResponseDto responseDto=patientService.updatePatientById(id,dto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/update/dni/{dni}")
    public ResponseEntity<PatientResponseDto>updatePatientByDni(@PathVariable String dni,
                                                                @Valid @RequestBody PatientUpdateDto dto){
        PatientResponseDto responseDto=patientService.updatePatientByDni(dni,dto);
        return ResponseEntity.ok(responseDto);
    }

    /* =================== DELETE ENDPOINTS =================== */
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Void>deletePatientById(@PathVariable Long id){
        patientService.deletePatientById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/dni/{dni}")
    public ResponseEntity<Void>deletePatientByDni(@PathVariable String dni){
        patientService.deletePatientByDni(dni);
        return ResponseEntity.noContent().build();
    }
}