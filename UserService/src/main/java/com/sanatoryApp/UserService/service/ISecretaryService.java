package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.SecretaryCreateDto;
import com.sanatoryApp.UserService.dto.Request.SecretaryUpdateDto;
import com.sanatoryApp.UserService.dto.Response.SecretaryResponseDto;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ISecretaryService  {

    /* BASIC CRUD */
    List<SecretaryResponseDto> findAll();
    SecretaryResponseDto findSecretaryById(Long id);
    SecretaryResponseDto createSecretary(SecretaryCreateDto dto);
    SecretaryResponseDto updateSecretaryById(Long id, SecretaryUpdateDto dto);
    SecretaryResponseDto updateSecretaryByDni(String dni, SecretaryUpdateDto dto);
    void deleteSecretaryById(Long id);
    void deleteSecretaryByDni(String dni);

    SecretaryResponseDto findSecretaryByEmail(String email);
    SecretaryResponseDto findSecretaryByDni(String dni);
    boolean existsByEmail(String email);
    boolean existsByDni(String dni);
    void disableSecretaryByDni(String dni);
    void enableSecretaryByDni(String dni);
}
