package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.PatientCreateDto;
import com.sanatoryApp.UserService.dto.Response.PatientResponseDto;

import java.util.Map;

public interface IPatientService {
    /*  BASIC CRUD */
    PatientResponseDto findPatientById(Long id);
    PatientResponseDto updatePatientById(Long id, Map<String,Object>updates);
    PatientResponseDto createPatient(PatientCreateDto dto);
    void deletePatientById(Long id);

    PatientResponseDto findPatientByDni(String dni);
    PatientResponseDto findPatientByEmail(String email);
    boolean existByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
