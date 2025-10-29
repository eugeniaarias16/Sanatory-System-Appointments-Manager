package com.sanatoryApp.UserService.controllers;

import com.sanatoryApp.UserService.dto.Request.SecretaryCreateDto;
import com.sanatoryApp.UserService.dto.Request.SecretaryUpdateDto;
import com.sanatoryApp.UserService.dto.Response.SecretaryResponseDto;
import com.sanatoryApp.UserService.service.ISecretaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/secretary")
public class SecretaryController {

    private final ISecretaryService secretaryService;

    /* =================== GET ENDPOINTS =================== */
    @GetMapping
    public ResponseEntity<List<SecretaryResponseDto>>findAllSecretaries(){
        List<SecretaryResponseDto>list=secretaryService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<SecretaryResponseDto>findSecretaryById(@PathVariable Long id){
        SecretaryResponseDto responseDto=secretaryService.findSecretaryById(id);
        return ResponseEntity.ok(responseDto);
    };

    @GetMapping("/dni/{dni}")
    public ResponseEntity<SecretaryResponseDto>findSecretaryByDni(@PathVariable String dni){
        SecretaryResponseDto responseDto=secretaryService.findSecretaryByDni(dni);
        return ResponseEntity.ok(responseDto);
    };

    @GetMapping("/email/{email}")
    public ResponseEntity<SecretaryResponseDto>findSecretaryByEmail(@PathVariable String email){
        SecretaryResponseDto responseDto=secretaryService.findSecretaryByEmail(email);
        return ResponseEntity.ok(responseDto);
    };


    /* =================== POST ENDPOINTS =================== */
    @PostMapping("/create")
    public ResponseEntity<SecretaryResponseDto>createSecretary(@Valid @RequestBody SecretaryCreateDto dto){
        SecretaryResponseDto responseDto=secretaryService.createSecretary(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /* =================== PUT ENDPOINTS =================== */

    @PutMapping("/update/id/{id}")
    public ResponseEntity<SecretaryResponseDto>updateSecretaryById(@PathVariable Long id,
                                                                   @Valid @RequestBody SecretaryUpdateDto dto){
        SecretaryResponseDto responseDto=secretaryService.updateSecretaryById(id,dto);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/update/dni/{dni}")
    public ResponseEntity<SecretaryResponseDto>updateSecretaryByDni(@PathVariable String dni,
                                                                    @Valid @RequestBody SecretaryUpdateDto dto){
        SecretaryResponseDto responseDto=secretaryService.updateSecretaryByDni(dni,dto);
        return ResponseEntity.ok(responseDto);
    }

    /* =================== DELETE ENDPOINTS =================== */
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Void>deleteSecretaryById(@PathVariable Long id){
        secretaryService.deleteSecretaryById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/dni/{dni}")
    public ResponseEntity<Void>deleteSecretaryByDni(@PathVariable String dni){
        secretaryService.deleteSecretaryByDni(dni);
        return ResponseEntity.noContent().build();
    }

}