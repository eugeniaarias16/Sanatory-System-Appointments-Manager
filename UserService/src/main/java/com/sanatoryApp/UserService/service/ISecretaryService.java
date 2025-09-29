package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.SecretaryCreateDto;
import com.sanatoryApp.UserService.dto.Response.SecretaryResponseDto;

import java.util.Map;

public interface ISecretaryService  {

    /* BASIC CRUD */
    SecretaryResponseDto findSecretaryById(Long id);
    SecretaryResponseDto createSecretary(SecretaryCreateDto dto);
    SecretaryResponseDto updateSecretaryById(Long id, Map<String,Object>update);
    void deleteSecretaryById(Long id);

    SecretaryResponseDto findSecretaryByEmail(String email);
    boolean existsByEmail(String email);
}
