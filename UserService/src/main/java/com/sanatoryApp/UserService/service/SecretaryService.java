package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.SecretaryCreateDto;
import com.sanatoryApp.UserService.dto.Request.SecretaryUpdateDto;
import com.sanatoryApp.UserService.dto.Response.SecretaryResponseDto;

import com.sanatoryApp.UserService.entity.Secretary;
import com.sanatoryApp.UserService.exception.DuplicateResourceException;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.repository.ISecretaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SecretaryService implements ISecretaryService{

    private final ISecretaryRepository secretaryRepository;

    @Override
    public List<SecretaryResponseDto> findAll() {
        List<Secretary> list=secretaryRepository.findAll();
        return list.stream()
                .map(SecretaryResponseDto::fromEntity)
                .toList();
    }

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

    @Override
    public boolean existsByDni(String dni) {
        return secretaryRepository.existByDni(dni);
    }

    @Transactional
    @Override
    public SecretaryResponseDto createSecretary(SecretaryCreateDto dto) {
        if(existsByEmail(dto.getEmail())){
        throw new DuplicateResourceException("Secretary already exists with email:"+dto.getEmail());
        }
        if (existsByDni(dto.getDni())){
            throw new DuplicateResourceException("Secretary already exists with dni:"+dto.getDni());
        }
        log.debug("Creating Secretary...");
        Secretary secretary=dto.toEntity();
        Secretary saved =secretaryRepository.save(secretary);
        log.info("Secretary successfully created");
        return SecretaryResponseDto.fromEntity(saved);

    }
    @Transactional
    @Override
    public SecretaryResponseDto updateSecretaryById(Long id, SecretaryUpdateDto dto) {
        Secretary existingSecretary=secretaryRepository.findById(id)
                .orElseThrow(()->new ResourceNotFound("Secretary not found with id:"+id));

        if(dto.dni()!=null & !dto.dni().isEmpty()){
            String newDni= dto.dni();
            if(!existingSecretary.getDni().equalsIgnoreCase(newDni)){
                if(existsByDni(newDni)){
                    throw new DuplicateResourceException("Secretary already exists with dni: "+ newDni);
                }
                existingSecretary.setDni(newDni);
            }
        }
        if(dto.email()!=null & !dto.email().isEmpty()){
            String newEmail= dto.email();
            if(!existingSecretary.getEmail().equalsIgnoreCase(newEmail)){
                if(existsByEmail(newEmail)){
                    throw new DuplicateResourceException("Secretary already exists with email: "+ newEmail);
                }
                existingSecretary.setEmail(newEmail);
            }
        }

        if(dto.firstName()!=null && !dto.firstName().isEmpty()){
            existingSecretary.setFirstName(dto.firstName().trim());
        }
        if(dto.lastName()!=null && !dto.lastName().isEmpty()){
            existingSecretary.setFirstName(dto.lastName().trim());
        }

        Secretary saved=secretaryRepository.save(existingSecretary);
        return SecretaryResponseDto.fromEntity(saved);
    }

    @Transactional
    @Override
    public SecretaryResponseDto updateSecretaryByDni(String dni, SecretaryUpdateDto dto) {
        Secretary existingSecretary=secretaryRepository.findSecretaryByDni(dni)
                .orElseThrow(()->new ResourceNotFound("Secretary not found with dni:"+dni));

        if(dto.dni()!=null & !dto.dni().isEmpty()){
            String newDni= dto.dni();
            if(!existingSecretary.getDni().equalsIgnoreCase(newDni)){
                if(existsByDni(newDni)){
                    throw new DuplicateResourceException("Secretary already exists with dni: "+ newDni);
                }
                existingSecretary.setDni(newDni);
            }
        }
        if(dto.email()!=null & !dto.email().isEmpty()){
            String newEmail= dto.email();
            if(!existingSecretary.getEmail().equalsIgnoreCase(newEmail)){
                if(existsByEmail(newEmail)){
                    throw new DuplicateResourceException("Secretary already exists with email: "+ newEmail);
                }
                existingSecretary.setEmail(newEmail);
            }
        }

        if(dto.firstName()!=null && !dto.firstName().isEmpty()){
            existingSecretary.setFirstName(dto.firstName().trim());
        }
        if(dto.lastName()!=null && !dto.lastName().isEmpty()){
            existingSecretary.setFirstName(dto.lastName().trim());
        }

        Secretary saved=secretaryRepository.save(existingSecretary);
        return SecretaryResponseDto.fromEntity(saved);
    }



    @Transactional
    @Override
    public void deleteSecretaryById(Long id) {
        Secretary secretary= secretaryRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFound("Secretary not found with id: "+id));
        log.debug("Deleting Secretary with id:{}",id);
        secretaryRepository.delete(secretary);
        log.info("Secretary with id: {} successfully deleted.",id);
    }

    @Override
    public void deleteSecretaryByDni(String dni) {
        Secretary secretary= secretaryRepository.findSecretaryByDni(dni)
                .orElseThrow(()-> new ResourceNotFound("Secretary not found with id: "+dni));
        log.debug("Deleting Secretary with dni:{}",dni);
        secretaryRepository.delete(secretary);
        log.info("Secretary with dni: {} successfully deleted.",dni);
    }

    @Override
    public SecretaryResponseDto findSecretaryByEmail(String email) {
        log.debug("Searching Secretary by email : {}",email);
        Secretary secretary=secretaryRepository.findSecretaryByEmail(email)
                .orElseThrow(()->new ResourceNotFound("Secretary not found with email:"+email));
        return SecretaryResponseDto.fromEntity(secretary);
    }

    @Override
    public SecretaryResponseDto findSecretaryByDni(String dni) {
        log.debug("Searching Secretary by dni : {}",dni);
        Secretary secretary=secretaryRepository.findSecretaryByDni(dni)
                .orElseThrow(()->new ResourceNotFound("Secretary not found with dni:"+dni));
        return SecretaryResponseDto.fromEntity(secretary);
    }


}
