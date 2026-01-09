package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.SecretaryCreateDto;
import com.sanatoryApp.UserService.dto.Request.SecretaryUpdateDto;
import com.sanatoryApp.UserService.dto.Response.SecretaryResponseDto;
import com.sanatoryApp.UserService.entity.Secretary;
import com.sanatoryApp.UserService.exception.DuplicateResourceException;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.repository.ISecretaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class SecretaryServiceTest {

    @Mock
    private ISecretaryRepository secretaryRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SecretaryService secretaryService;

    @Test
    void findAllTest(){
        //Arrange
        Secretary secretary1=new Secretary(2L,"34567890","Lucrecia","Gonzalez","lucrecia16@gmail.com","EncodedPassword123",true,false,false);
        Secretary secretary2=new Secretary(3L,"43876234","Marisa","Lopez","marisaLopez@Gmail.com","EncodedPassword1245",true,false,false);

        when(secretaryRepository.findAll()).thenReturn(List.of(secretary1,secretary2));

        //Act
        List< SecretaryResponseDto>secretaryDtoList=secretaryService.findAll();

        //Assert & Verify
        assertEquals(2,secretaryDtoList.size());
        assertEquals("Gonzalez",secretaryDtoList.get(0).lastName());
        assertEquals("Marisa",secretaryDtoList.get(1).firstName());
        verify(secretaryRepository).findAll();
        verifyNoMoreInteractions(secretaryRepository);

    }

    @Test
    void createSecretaryTest(){
        //Arrange
        SecretaryCreateDto createDto=new SecretaryCreateDto("34567890","Lucrecia","Gonzalez","lucrecia16@gmail.com");
        Secretary secretary=createDto.toEntity();
        secretary.setId(2L);

        //Mock
        when(secretaryRepository.existsByEmail("lucrecia16@gmail.com")).thenReturn(false);
        when(secretaryRepository.existsByDni("34567890")).thenReturn(false);
        when(passwordEncoder.encode("34567890")).thenReturn("EncodedPassword123");
        when(secretaryRepository.save(any(Secretary.class))).thenReturn(secretary);

        //Act
        SecretaryResponseDto secretaryResponseDto=secretaryService.createSecretary(createDto);

        //Assert & Verify
        assertEquals("Lucrecia",secretaryResponseDto.firstName());
        assertEquals("34567890",secretaryResponseDto.dni());
        verify(secretaryRepository).existsByEmail(anyString());
        verify(secretaryRepository).existsByDni(anyString());
        verify(passwordEncoder).encode("34567890");
        verify(secretaryRepository).save(any(Secretary.class));
        verifyNoMoreInteractions(secretaryRepository);

    }

    @Test
    void updateSecretaryByIdTest(){
        Secretary secretary1=new Secretary(2L,"34567890","Lucrecia","Gonzalez","lucrecia16@gmail.com","EncodedPassword123",true,false,false);
        Secretary secretaryUpdated=new Secretary(2L,"34567890","Lucila","Gonzalez","lucila16@gmail.com","EncodedPassword123",true,false,false);
        SecretaryUpdateDto updateDto=new SecretaryUpdateDto(null,"Lucila",null,"lucila16@gmail.com");

        when(secretaryRepository.findById(2L)).thenReturn(Optional.of(secretary1));
        when(secretaryRepository.existsByEmail("lucila16@gmail.com")).thenReturn(false);
        when(secretaryRepository.save(any(Secretary.class))).thenReturn(secretaryUpdated);

        SecretaryResponseDto secretaryResponseDto=secretaryService.updateSecretaryById(2L,updateDto);

        assertEquals("Lucila",secretaryResponseDto.firstName());
        assertEquals("lucila16@gmail.com",secretaryResponseDto.email());
        verify(secretaryRepository).findById(anyLong());
        verify(secretaryRepository).existsByEmail(anyString());
        verify(secretaryRepository,never()).existsByDni(anyString()); //It does not run because it is null in the dto
        verify(secretaryRepository).save(any(Secretary.class));
        verifyNoMoreInteractions(secretaryRepository);
    }

    @Test
    void updateSecretaryByDniTest() {
        Secretary secretary1=new Secretary(2L,"34567890","Lucrecia","Gonzalez","lucrecia16@gmail.com","EncodedPassword123",true,false,false);
        Secretary secretaryUpdated=new Secretary(2L,"34567890","Lucila","Gonzalez","lucila16@gmail.com","EncodedPassword123",true,false,false);
        SecretaryUpdateDto updateDto=new SecretaryUpdateDto(null,"Lucila",null,"lucila16@gmail.com");

        when(secretaryRepository.findSecretaryByDni("34567890")).thenReturn(Optional.of(secretary1));
        when(secretaryRepository.existsByEmail("lucila16@gmail.com")).thenReturn(false);
        when(secretaryRepository.save(any(Secretary.class))).thenReturn(secretaryUpdated);

        SecretaryResponseDto secretaryResponseDto=secretaryService.updateSecretaryByDni("34567890",updateDto);
        assertEquals("Lucila",secretaryResponseDto.firstName());
        assertEquals("lucila16@gmail.com",secretaryResponseDto.email());
        verify(secretaryRepository).findSecretaryByDni(anyString());
        verify(secretaryRepository).existsByEmail(anyString());
        verify(secretaryRepository,never()).existsByDni(anyString()); //It does not run because it is null in the dto
        verify(secretaryRepository).save(any(Secretary.class));
        verifyNoMoreInteractions(secretaryRepository);
    }

    @Test
    void enableSecretaryByDni() {
        when(secretaryRepository.existsByDni("34567890")).thenReturn(true);
        secretaryService.enableSecretaryByDni("34567890");
        verify(secretaryRepository).existsByDni(anyString());
        verify(secretaryRepository).enableSecretaryByDni(anyString());
        verifyNoMoreInteractions(secretaryRepository);
    }

    @Test
    void disableSecretaryByDni() {
        when(secretaryRepository.existsByDni("34567890")).thenReturn(true);
        secretaryService.disableSecretaryByDni("34567890");
        verify(secretaryRepository).existsByDni(anyString());
        verify(secretaryRepository).disableSecretaryByDni(anyString());
        verifyNoMoreInteractions(secretaryRepository);
    }

    @Test
    void deleteSecretaryByIdTest(){
        Secretary secretary1=new Secretary(2L,"34567890","Lucrecia","Gonzalez","lucrecia16@gmail.com","EncodedPassword123",true,false,false);
        when(secretaryRepository.findById(2L)).thenReturn(Optional.of(secretary1));
        secretaryService.deleteSecretaryById(2L);
        verify(secretaryRepository).findById(anyLong());
        verify(secretaryRepository).delete(any(Secretary.class));
        verifyNoMoreInteractions(secretaryRepository);
    }

    @Test
    void deleteSecretaryByDniTest(){
        Secretary secretary1=new Secretary(2L,"34567890","Lucrecia","Gonzalez","lucrecia16@gmail.com","EncodedPassword123",true,false,false);
        when(secretaryRepository.findSecretaryByDni("34567890")).thenReturn(Optional.of(secretary1));
        secretaryService.deleteSecretaryByDni("34567890");
        verify(secretaryRepository).findSecretaryByDni(anyString());
        verify(secretaryRepository).delete(any(Secretary.class));
        verifyNoMoreInteractions(secretaryRepository);
    }



    // ERROR CASES- DuplicateResourceException
    @Test
    void createSecretary_withDuplicateEmail() {
        SecretaryCreateDto createDto=new SecretaryCreateDto("34567890","Lucrecia","Gonzalez","lucrecia16@gmail.com");


        when(secretaryRepository.existsByEmail("lucrecia16@gmail.com")).thenReturn(true);

       DuplicateResourceException exception= assertThrows(DuplicateResourceException.class,()->secretaryService.createSecretary(createDto));
        assertEquals("Secretary already exists with email: lucrecia16@gmail.com",exception.getMessage());
        verify(secretaryRepository).existsByEmail(anyString());
        verifyNoMoreInteractions(secretaryRepository);
    }

    @Test
    void createSecretary_withDuplicateDni() {
        SecretaryCreateDto createDto=new SecretaryCreateDto("34567890","Lucrecia","Gonzalez","lucrecia16@gmail.com");
        when(secretaryRepository.existsByEmail("lucrecia16@gmail.com")).thenReturn(false);
        when(secretaryRepository.existsByDni("34567890")).thenReturn(true);
        DuplicateResourceException exception=assertThrows(DuplicateResourceException.class,()->secretaryService.createSecretary(createDto));
        assertEquals("Secretary already exists with dni: 34567890",exception.getMessage());

        verify(secretaryRepository).existsByEmail(anyString());
        verify(secretaryRepository).existsByDni(anyString());
        verifyNoMoreInteractions(secretaryRepository);
    }


    @Test
    void updateSecretaryById_withDuplicateEmail() {}

    @Test
    void updateSecretaryById_withDuplicateDni() {}

    @Test
    void updateSecretaryByDni_withDuplicateEmail() {}

    @Test
    void updateSecretaryByDni_withDuplicateDni() {}


    // ERROR CASES - ResourceNotFound
    @Test
    void updateInexistentSecretaryById() {

        SecretaryUpdateDto updateDto=new SecretaryUpdateDto(null,"Lucila",null,"lucila16@gmail.com");

        when(secretaryRepository.findById(2L)).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->secretaryService.updateSecretaryById(2L,updateDto));

        assertEquals("Secretary not found with id: 2",exception.getMessage());

        verify(secretaryRepository).findById(anyLong());
        verifyNoMoreInteractions(secretaryRepository);
    }

    @Test
    void updateInexistentSecretaryByDni() {

        SecretaryUpdateDto updateDto=new SecretaryUpdateDto(null,"Lucila",null,"lucila16@gmail.com");
        when(secretaryRepository.findSecretaryByDni("34567890")).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->secretaryService.updateSecretaryByDni("34567890",updateDto));
        assertEquals("Secretary not found with dni: 34567890", exception.getMessage());
        verify(secretaryRepository).findSecretaryByDni(anyString());
        verifyNoMoreInteractions(secretaryRepository);
    }

    @Test
    void deleteInexistentSecretaryById() {
        when(secretaryRepository.findById(2L)).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->secretaryService.deleteSecretaryById(2L));

        assertEquals("Secretary not found with id: 2",exception.getMessage());

        verify(secretaryRepository).findById(anyLong());
        verifyNoMoreInteractions(secretaryRepository);
    }

    @Test
    void deleteInexistentSecretaryByDni() {
        when(secretaryRepository.findSecretaryByDni("34567890")).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->secretaryService.deleteSecretaryByDni("34567890"));
        assertEquals("Secretary not found with dni: 34567890", exception.getMessage());
        verify(secretaryRepository).findSecretaryByDni(anyString());
        verifyNoMoreInteractions(secretaryRepository);
    }


    @Test
    void disableInexistentSecretaryByDni() {
        when(secretaryRepository.existsByDni("34567890")).thenReturn(false);
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->secretaryService.disableSecretaryByDni("34567890"));
        assertEquals("Secretary not found with dni: 34567890", exception.getMessage());
        verify(secretaryRepository).existsByDni(anyString());
        verifyNoMoreInteractions(secretaryRepository);
    }

    @Test
    void enableInexistentSecretaryByDni() {
        when(secretaryRepository.existsByDni("34567890")).thenReturn(false);
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->secretaryService.enableSecretaryByDni("34567890"));
        assertEquals("Secretary not found with dni: 34567890", exception.getMessage());
        verify(secretaryRepository).existsByDni(anyString());
        verifyNoMoreInteractions(secretaryRepository);
    }

}
