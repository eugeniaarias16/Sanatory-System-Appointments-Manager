package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.PatientCreateDto;
import com.sanatoryApp.UserService.dto.Request.PatientUpdateDto;
import com.sanatoryApp.UserService.dto.Response.PatientResponseDto;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface IPatientService {
    /*  BASIC CRUD */
    List<PatientResponseDto>findAll();
    PatientResponseDto findPatientById(Long id);
    PatientResponseDto updatePatientById(Long id, PatientUpdateDto dto);
    PatientResponseDto updatePatientByDni(String dni, PatientUpdateDto dto);
    PatientResponseDto createPatient(PatientCreateDto dto);
    void deletePatientById(Long id);
    void deletePatientByDni(String dni);

    PatientResponseDto findPatientByDni(String dni);
    PatientResponseDto findPatientByEmail(String email);
    boolean existsByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    void disablePatientByDni(String dni);
    void enablePatientByDni(String dni);

}