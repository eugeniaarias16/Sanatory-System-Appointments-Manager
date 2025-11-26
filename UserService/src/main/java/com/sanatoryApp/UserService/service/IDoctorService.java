package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.DoctorCreateDto;
import com.sanatoryApp.UserService.dto.Request.DoctorUpdateDto;
import com.sanatoryApp.UserService.dto.Response.DoctorResponseDto;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface IDoctorService {
    /* BASIC CRUD */

    List<DoctorResponseDto>findAll();
    DoctorResponseDto findDoctorById(Long id);
    DoctorResponseDto updateDoctorById(Long id, DoctorUpdateDto dto);
    DoctorResponseDto createDoctor(DoctorCreateDto dto);
    DoctorResponseDto findDoctorByDni(String dni);
    void deleteDoctorById(Long id);

    DoctorResponseDto findByEmail(String email);


    List<DoctorResponseDto> findDoctorByFirstName(String firstName);
    List<DoctorResponseDto> findDoctorByLastName(String lastName);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByDni(String dni);

    void disableDoctorByDni(String dni);
    void enableDoctorByDni(String dni);

}