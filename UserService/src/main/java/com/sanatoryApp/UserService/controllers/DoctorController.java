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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
@Tag(name = "Doctor Management",description ="API for managing Doctor's information." )
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
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponseDto> findDoctorById(@PathVariable Long id){
        DoctorResponseDto doctor= doctorService.findDoctorById(id);
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

    /* =================== POST ENDPOINTS =================== */
    @Operation(summary = "Create a new Doctor")
    @PostMapping("/create")
    public ResponseEntity<DoctorResponseDto>createDoctor(@Valid @RequestBody DoctorCreateDto dto){
        DoctorResponseDto doctor=doctorService.createDoctor(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(doctor);
    }

    /* =================== PUT ENDPOINTS =================== */
    @Operation(summary = "Update Doctor by id")
    @PutMapping("/update/{id}")
    public ResponseEntity<DoctorResponseDto> updateDoctorById(@PathVariable Long id,
                                                              @Valid @RequestBody DoctorUpdateDto dto){
        DoctorResponseDto doctor= doctorService.updateDoctorById(id,dto);
        return ResponseEntity.ok(doctor);
    }

    /* =================== DELETE ENDPOINTS =================== */
    @Operation(summary = "Delete Doctor by id")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDoctorById(@PathVariable Long id){
        doctorService.deleteDoctorById(id);
        return ResponseEntity.ok("Doctor with id "+id+"successfully deleted.");
    }
}