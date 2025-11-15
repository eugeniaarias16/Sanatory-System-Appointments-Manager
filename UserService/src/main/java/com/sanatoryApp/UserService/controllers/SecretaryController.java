package com.sanatoryApp.UserService.controllers;

import com.sanatoryApp.UserService.dto.Request.SecretaryCreateDto;
import com.sanatoryApp.UserService.dto.Request.SecretaryUpdateDto;
import com.sanatoryApp.UserService.dto.Response.SecretaryResponseDto;
import com.sanatoryApp.UserService.service.ISecretaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/secretary")
@Tag(name = "Secretary Management",description ="API for managing Secretary's information." )

public class SecretaryController {

    private final ISecretaryService secretaryService;

    /* =================== GET ENDPOINTS =================== */

    @Operation(summary ="Get all secretaries" ,description ="Get the data for all secretaries in the system." )
    @GetMapping
    public ResponseEntity<List<SecretaryResponseDto>>findAllSecretaries(){
        List<SecretaryResponseDto>list=secretaryService.findAll();
        return ResponseEntity.ok(list);
    }


    @Operation(summary ="Get secretary by id" ,description ="Get the data from a specific Secretary by id" )
    @GetMapping("/id/{id}")
    public ResponseEntity<SecretaryResponseDto>findSecretaryById(@PathVariable Long id){
        SecretaryResponseDto responseDto=secretaryService.findSecretaryById(id);
        return ResponseEntity.ok(responseDto);
    };


    @Operation(summary ="Get secretary by dni" ,description ="Get the data from a specific Secretary by dni" )
    @GetMapping("/dni/{dni}")
    public ResponseEntity<SecretaryResponseDto>findSecretaryByDni(@PathVariable String dni){
        SecretaryResponseDto responseDto=secretaryService.findSecretaryByDni(dni);
        return ResponseEntity.ok(responseDto);
    };


    @Operation(summary ="Get secretary by email" ,description ="Get the data from a specific Secretary by email" )
    @GetMapping("/email/{email}")
    public ResponseEntity<SecretaryResponseDto>findSecretaryByEmail(@PathVariable String email){
        SecretaryResponseDto responseDto=secretaryService.findSecretaryByEmail(email);
        return ResponseEntity.ok(responseDto);
    };


    /* =================== POST ENDPOINTS =================== */

    @Operation(summary ="Create secretary" ,description ="Create a new Secretary" )
    @PostMapping("/create")
    public ResponseEntity<SecretaryResponseDto>createSecretary(@Valid @RequestBody SecretaryCreateDto dto){
        SecretaryResponseDto responseDto=secretaryService.createSecretary(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /* =================== PUT ENDPOINTS =================== */

    @Operation(summary ="Update Secretary by id" ,description ="Update Secretary's data by id" )
    @PutMapping("/update/id/{id}")
    public ResponseEntity<SecretaryResponseDto>updateSecretaryById(@PathVariable Long id,
                                                                   @Valid @RequestBody SecretaryUpdateDto dto){
        SecretaryResponseDto responseDto=secretaryService.updateSecretaryById(id,dto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary ="Update Secretary by dni" ,description ="Update Secretary's data by dni" )
    @PutMapping("/update/dni/{dni}")
    public ResponseEntity<SecretaryResponseDto>updateSecretaryByDni(@PathVariable String dni,
                                                                    @Valid @RequestBody SecretaryUpdateDto dto){
        SecretaryResponseDto responseDto=secretaryService.updateSecretaryByDni(dni,dto);
        return ResponseEntity.ok(responseDto);
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Operation(summary ="Delete Secretary by id" )
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Void>deleteSecretaryById(@PathVariable Long id){
        secretaryService.deleteSecretaryById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary ="Delete Secretary by dni" )
    @DeleteMapping("/delete/dni/{dni}")
    public ResponseEntity<Void>deleteSecretaryByDni(@PathVariable String dni){
        secretaryService.deleteSecretaryByDni(dni);
        return ResponseEntity.noContent().build();
    }

}