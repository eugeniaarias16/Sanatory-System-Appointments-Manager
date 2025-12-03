package com.sanatoryApp.UserService.controllers;

import com.sanatoryApp.UserService.dto.Request.DoctorCreateDto;
import com.sanatoryApp.UserService.dto.Request.DoctorUpdateDto;
import com.sanatoryApp.UserService.dto.Response.DoctorResponseDto;
import com.sanatoryApp.UserService.service.IDoctorService;
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
@RequestMapping("/doctor")
@RequiredArgsConstructor
@Tag(name = "Doctor Management",description ="API for managing Doctor's information." )
@PreAuthorize("hasRole('SECRETARY')")
public class DoctorController {

    private final IDoctorService doctorService;

    /* =================== GET ENDPOINTS =================== */

    @Operation(summary = "Get all Doctors")
    @GetMapping
    
    public ResponseEntity<List<DoctorResponseDto>>findAllDoctors(){
        List<DoctorResponseDto>list=doctorService.findAll();
        return ResponseEntity.ok(list);
    }

    
    @Operation(summary = "Get Doctor by id")
    @GetMapping("/{doctorId}")
    @PreAuthorize("@securityService.isSecretaryOrDoctor(#doctorId)")
    public ResponseEntity<DoctorResponseDto> findDoctorById(@PathVariable Long doctorId){
        DoctorResponseDto doctor= doctorService.findDoctorById(doctorId);
        return ResponseEntity.ok(doctor);
    }

        
    @Operation(summary = "Get Doctor by First Name")
    @GetMapping("/firstName/{firstName}")
    public ResponseEntity<List<DoctorResponseDto>> findDoctorByFirstName(@PathVariable String firstName){
        List<DoctorResponseDto> doctorList=doctorService.findDoctorByFirstName(firstName);
        return ResponseEntity.ok(doctorList);
    }

    
    @Operation(summary = "Get Doctor by Last Name")
    @GetMapping("/lastName/{lastName}")
    public ResponseEntity<List<DoctorResponseDto>> findDoctorByLastName(@PathVariable String lastName){
        List<DoctorResponseDto> doctorList=doctorService.findDoctorByLastName(lastName);
        return ResponseEntity.ok(doctorList);
    }

    
    @Operation(summary = "Get Doctor by dni")
    @GetMapping("/dni/{dni}")
    public ResponseEntity<DoctorResponseDto> findDoctorByDni(@PathVariable String dni){
        DoctorResponseDto doctor=doctorService.findDoctorByDni(dni);
        return ResponseEntity.ok(doctor);
    }

    /* =================== POST ENDPOINTS =================== */
    
    @Operation(summary = "Create a new Doctor")
    @PostMapping("/create")
    public ResponseEntity<DoctorResponseDto>createDoctor(@Valid @RequestBody DoctorCreateDto dto){
        DoctorResponseDto doctor=doctorService.createDoctor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
    }

    /* =================== PUT/PATCH ENDPOINTS =================== */
    
    @Operation(summary = "Update Doctor by id")
    @PutMapping("/update/{doctorId}")
    @PreAuthorize("@securityService.isSecretaryOrDoctor(#doctorId)")
    public ResponseEntity<DoctorResponseDto> updateDoctorById(@PathVariable Long doctorId,
                                                              @Valid @RequestBody DoctorUpdateDto dto){
        DoctorResponseDto doctor= doctorService.updateDoctorById(doctorId,dto);
        return ResponseEntity.ok(doctor);
    }

    
    @Operation(summary = "Disable Doctor by dni")
    @PatchMapping("/disable/{dni}")
    public ResponseEntity<String>disableDoctorByDni(@PathVariable String dni){
        doctorService.disableDoctorByDni(dni);
        return ResponseEntity.ok("Doctor with dni "+" successfully disabled.");
    }

    
    @Operation(summary = "Enable Doctor by dni")
    @PatchMapping("/enable/{dni}")
    public ResponseEntity<String>enableDoctorByDni(@PathVariable String dni){
        doctorService.enableDoctorByDni(dni);
        return ResponseEntity.ok("Doctor with dni "+" successfully enabled.");
    }

    /* =================== DELETE ENDPOINTS =================== */
    
    @Operation(summary = "Delete Doctor by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDoctorById(@PathVariable Long id){
        doctorService.deleteDoctorById(id);
        return ResponseEntity.ok("Doctor with id "+id+"successfully deleted.");
    }
}