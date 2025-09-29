package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.DoctorCreateDto;
import com.sanatoryApp.UserService.dto.Response.DoctorResponseDto;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public interface IDoctorService {
    /* BASIC CRUD */
    DoctorResponseDto findDoctorById(Long id);
    DoctorResponseDto updateDoctorById(Long id, Map<String, Object>updates);
    DoctorResponseDto createDoctor(DoctorCreateDto dto);
    void deleteDoctorById(Long id);

    List<DoctorResponseDto> findDoctorByFirstName(String firstName);
    List<DoctorResponseDto> findDoctorByLastName(String lastName);
    boolean existByEmail(String email);
    boolean existByPhoneNumber(String phoneNumber);


}
