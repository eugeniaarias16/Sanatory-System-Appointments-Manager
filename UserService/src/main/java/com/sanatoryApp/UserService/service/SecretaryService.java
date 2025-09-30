package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.SecretaryCreateDto;
import com.sanatoryApp.UserService.dto.Response.SecretaryResponseDto;

import com.sanatoryApp.UserService.entity.Secretary;
import com.sanatoryApp.UserService.exception.DuplicateResourceException;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.repository.ISecretaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecretaryService implements ISecretaryService{

    private final ISecretaryRepository secretaryRepository;

    @Override
    public SecretaryResponseDto findSecretaryById(Long id) {
        log.debug("Searching Secretary by Id: {}",id);
        Secretary secretary= secretaryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Secretary not found with id: "+id));
        return SecretaryResponseDto.fromEntity(secretary);
    }
    @Override
    public boolean existsByEmail(String email) {
        log.debug("Check if there is a secretary with email: {}",email);
        return secretaryRepository.existsByEmail(email);
    }

    @Transactional
    @Override
    public SecretaryResponseDto createSecretary(SecretaryCreateDto dto) {
        if(existsByEmail(dto.getEmail())){
        throw new DuplicateResourceException("Secretary already exists with email:"+dto.getEmail());
        }
        log.debug("Creating Secretary...");
        Secretary secretary=dto.toEntity();
        Secretary saved =secretaryRepository.save(secretary);
        log.info("Secretary successfully created");
        return SecretaryResponseDto.fromEntity(saved);

    }
    @Transactional
    @Override
    public SecretaryResponseDto updateSecretaryById(Long id, Map<String, Object> updates) {
        Secretary existingSecretary=secretaryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Secretary not found with id:"+id));
        updates.forEach((key,value)->{
            switch(key){
                case"firstName"->existingSecretary.setFirstName((String) value);
                case "lastName"->existingSecretary.setLastName((String) value);
                case "email"->{
                    String email=(String) value;
                    if(existsByEmail(email)){
                        throw new DuplicateResourceException("Secretary already exists with email:"+email);
                    }
                   existingSecretary.setEmail(email);
                }
                default -> log.warn("Field not recognized: ",key);
            }
        });
        Secretary saved=secretaryRepository.save(existingSecretary);
        return SecretaryResponseDto.fromEntity(saved);
    }
    @Transactional
    @Override
    public void deleteSecretaryById(Long id) {
        findSecretaryById(id);
        log.debug("Deleting Secretary with id:{}",id);
        secretaryRepository.deleteById(id);
        log.info("Secretary successfully deleted.");
    }

    @Override
    public SecretaryResponseDto findSecretaryByEmail(String email) {
        log.debug("Searching Secretary by email : {}",email);
        Secretary secretary=secretaryRepository.findSecretaryByEmail(email)
                .orElseThrow(()->new ResourceNotFound("Secretary not found with email:"+email));
        return SecretaryResponseDto.fromEntity(secretary);
    }




}
