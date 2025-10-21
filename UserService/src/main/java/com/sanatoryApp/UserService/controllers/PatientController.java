package com.sanatoryApp.UserService.controllers;

import com.sanatoryApp.UserService.dto.Request.PatientCreateDto;
import com.sanatoryApp.UserService.dto.Request.PatientUpdateDto;
import com.sanatoryApp.UserService.dto.Response.PatientResponseDto;
import com.sanatoryApp.UserService.service.IPatientService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<PatientResponseDto>createPatient(@RequestBody PatientCreateDto dto){
        PatientResponseDto responseDto=patientService.createPatient(dto);
        return ResponseEntity.ok(responseDto);
    }

    /* =================== PUT ENDPOINTS =================== */
    @PostMapping("/update/id/{id}")
    public ResponseEntity<PatientResponseDto>createPatient(@PathVariable Long id,
                                                           @RequestBody PatientUpdateDto dto){
        PatientResponseDto responseDto=patientService.updatePatientById(id,dto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/update/dni/{dni}")
    public ResponseEntity<PatientResponseDto>createPatient(@PathVariable String dni,
                                                           @RequestBody PatientUpdateDto dto){
        PatientResponseDto responseDto=patientService.updatePatientByDni(dni,dto);
        return ResponseEntity.ok(responseDto);
    }

    /* =================== DELETE ENDPOINTS =================== */
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<String>deletePatientById(@PathVariable Long id){
        patientService.deletePatientById(id);
        return ResponseEntity.ok("Patient successfully deleted.");
    }

    @DeleteMapping("/delete/dni/{dni}")
    public ResponseEntity<String>deletePatientByDni(@PathVariable String dni){
        patientService.deletePatientByDni(dni);
        return ResponseEntity.ok("Patient successfully deleted.");
    }
}
