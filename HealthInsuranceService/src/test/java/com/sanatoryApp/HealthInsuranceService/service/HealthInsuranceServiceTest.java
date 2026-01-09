package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.HealthInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.exception.DuplicateResourceException;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.repository.IHealthInsuranceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HealthInsuranceServiceTest {

    @Mock
    private IHealthInsuranceRepository healthInsuranceRepository;

    @InjectMocks
    private HealthInsuranceService healthInsuranceService;

    @Test
    void getHealthInsuranceByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        when(healthInsuranceRepository.findById(1L)).thenReturn(Optional.of(healthInsurance));

        //Act
        HealthInsurance result = healthInsuranceService.getHealthInsuranceById(1L);

        //Assert & Verify
        assertEquals(1L, result.getId());
        assertEquals("OSDE", result.getCompanyName());
        assertEquals(123456L, result.getCompanyCode());
        verify(healthInsuranceRepository).findById(1L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void findHealthInsuranceByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        when(healthInsuranceRepository.findById(1L)).thenReturn(Optional.of(healthInsurance));

        //Act
        HealthInsuranceResponseDto result = healthInsuranceService.findHealthInsuranceById(1L);

        //Assert & Verify
        assertEquals(1L, result.id());
        assertEquals("OSDE", result.companyName());
        assertEquals(123456L, result.companyCode());
        verify(healthInsuranceRepository).findById(1L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void createHealthInsuranceTest() {
        //Arrange
        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto("OSDE", 123456L, "+5491112345678", "contact@osde.com");

        HealthInsurance healthInsurance = createDto.toEntity();
        healthInsurance.setId(1L);

        //Mock
        when(healthInsuranceRepository.existsByCompanyName("OSDE")).thenReturn(false);
        when(healthInsuranceRepository.existsByEmail("contact@osde.com")).thenReturn(false);
        when(healthInsuranceRepository.existsByCompanyCode(123456L)).thenReturn(false);
        when(healthInsuranceRepository.existsByPhoneNumber("+5491112345678")).thenReturn(false);
        when(healthInsuranceRepository.save(any(HealthInsurance.class))).thenReturn(healthInsurance);

        //Act
        HealthInsuranceResponseDto result = healthInsuranceService.createHealthInsurance(createDto);

        //Assert & Verify
        assertEquals("OSDE", result.companyName());
        assertEquals(123456L, result.companyCode());
        assertEquals("+5491112345678", result.phoneNumber());
        assertEquals("contact@osde.com", result.email());
        verify(healthInsuranceRepository).existsByCompanyName(anyString());
        verify(healthInsuranceRepository).existsByEmail(anyString());
        verify(healthInsuranceRepository).existsByCompanyCode(anyLong());
        verify(healthInsuranceRepository).existsByPhoneNumber(anyString());
        verify(healthInsuranceRepository).save(any(HealthInsurance.class));
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void updateHealthInsuranceByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        HealthInsurance updatedHealthInsurance = new HealthInsurance();
        updatedHealthInsurance.setId(1L);
        updatedHealthInsurance.setCompanyName("OSDE Updated");
        updatedHealthInsurance.setCompanyCode(123456L);
        updatedHealthInsurance.setPhoneNumber("+5491112345679");
        updatedHealthInsurance.setEmail("newcontact@osde.com");
        updatedHealthInsurance.setActive(true);

        HealthInsuranceUpdateDto updateDto = new HealthInsuranceUpdateDto("OSDE Updated", 123456L, "+5491112345679", "newcontact@osde.com");

        when(healthInsuranceRepository.findById(1L)).thenReturn(Optional.of(healthInsurance));
        when(healthInsuranceRepository.existsByCompanyName("OSDE Updated")).thenReturn(false);
        when(healthInsuranceRepository.existsByPhoneNumber("+5491112345679")).thenReturn(false);
        when(healthInsuranceRepository.existsByEmail("newcontact@osde.com")).thenReturn(false);
        when(healthInsuranceRepository.save(any(HealthInsurance.class))).thenReturn(updatedHealthInsurance);

        //Act
        HealthInsuranceResponseDto result = healthInsuranceService.updateHealthInsuranceById(1L, updateDto);

        //Assert & Verify
        assertEquals("OSDE Updated", result.companyName());
        assertEquals("+5491112345679", result.phoneNumber());
        assertEquals("newcontact@osde.com", result.email());
        verify(healthInsuranceRepository).findById(1L);
        verify(healthInsuranceRepository).save(any(HealthInsurance.class));
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void deleteHealthInsuranceByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        when(healthInsuranceRepository.findById(1L)).thenReturn(Optional.of(healthInsurance));

        //Act
        healthInsuranceService.deleteHealthInsuranceById(1L);

        //Assert & Verify
        verify(healthInsuranceRepository).findById(1L);
        verify(healthInsuranceRepository).delete(any(HealthInsurance.class));
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void softDeleteHealthInsuranceByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        when(healthInsuranceRepository.findById(1L)).thenReturn(Optional.of(healthInsurance));

        //Act
        healthInsuranceService.softDeleteHealthInsuranceById(1L);

        //Assert & Verify
        verify(healthInsuranceRepository).findById(1L);
        verify(healthInsuranceRepository).save(any(HealthInsurance.class));
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void activateHealthInsuranceByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(false);

        when(healthInsuranceRepository.findById(1L)).thenReturn(Optional.of(healthInsurance));

        //Act
        healthInsuranceService.activateHealthInsuranceById(1L);

        //Assert & Verify
        verify(healthInsuranceRepository).findById(1L);
        verify(healthInsuranceRepository).save(any(HealthInsurance.class));
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void findHealthInsuranceByCompanyNameTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        when(healthInsuranceRepository.findByCompanyName("OSDE")).thenReturn(Optional.of(healthInsurance));

        //Act
        HealthInsuranceResponseDto result = healthInsuranceService.findHealthInsuranceByCompanyName("OSDE");

        //Assert & Verify
        assertEquals("OSDE", result.companyName());
        verify(healthInsuranceRepository).findByCompanyName("OSDE");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void findHealthInsuranceByCompanyCodeTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        when(healthInsuranceRepository.findByCompanyCode(123456L)).thenReturn(Optional.of(healthInsurance));

        //Act
        HealthInsuranceResponseDto result = healthInsuranceService.findHealthInsuranceByCompanyCode(123456L);

        //Assert & Verify
        assertEquals(123456L, result.companyCode());
        verify(healthInsuranceRepository).findByCompanyCode(123456L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void findHealthInsuranceByPhoneNumberTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        when(healthInsuranceRepository.findByPhoneNumber("+5491112345678")).thenReturn(Optional.of(healthInsurance));

        //Act
        HealthInsuranceResponseDto result = healthInsuranceService.findHealthInsuranceByPhoneNumber("+5491112345678");

        //Assert & Verify
        assertEquals("+5491112345678", result.phoneNumber());
        verify(healthInsuranceRepository).findByPhoneNumber("+5491112345678");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void findHealthInsuranceByEmailTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        when(healthInsuranceRepository.findByEmail("contact@osde.com")).thenReturn(Optional.of(healthInsurance));

        //Act
        HealthInsuranceResponseDto result = healthInsuranceService.findHealthInsuranceByEmail("contact@osde.com");

        //Assert & Verify
        assertEquals("contact@osde.com", result.email());
        verify(healthInsuranceRepository).findByEmail("contact@osde.com");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void searchByNameTest() {
        //Arrange
        HealthInsurance healthInsurance1 = new HealthInsurance();
        healthInsurance1.setId(1L);
        healthInsurance1.setCompanyName("OSDE");
        healthInsurance1.setCompanyCode(123456L);
        healthInsurance1.setPhoneNumber("+5491112345678");
        healthInsurance1.setEmail("contact@osde.com");
        healthInsurance1.setActive(true);

        HealthInsurance healthInsurance2 = new HealthInsurance();
        healthInsurance2.setId(2L);
        healthInsurance2.setCompanyName("OSDE Plus");
        healthInsurance2.setCompanyCode(123457L);
        healthInsurance2.setPhoneNumber("+5491112345679");
        healthInsurance2.setEmail("contact@osdeplus.com");
        healthInsurance2.setActive(true);

        when(healthInsuranceRepository.findByCompanyNameContaining("OSDE")).thenReturn(List.of(healthInsurance1, healthInsurance2));

        //Act
        List<HealthInsuranceResponseDto> resultList = healthInsuranceService.searchByName("OSDE");

        //Assert & Verify
        assertEquals(2, resultList.size());
        assertTrue(resultList.get(0).companyName().contains("OSDE"));
        assertTrue(resultList.get(1).companyName().contains("OSDE"));
        verify(healthInsuranceRepository).findByCompanyNameContaining("OSDE");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void existsByCompanyNameTest() {
        when(healthInsuranceRepository.existsByCompanyName("OSDE")).thenReturn(true);

        Boolean exists = healthInsuranceService.existsByCompanyName("OSDE");

        assertTrue(exists);
        verify(healthInsuranceRepository).existsByCompanyName("OSDE");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void existsByCompanyCodeTest() {
        when(healthInsuranceRepository.existsByCompanyCode(123456L)).thenReturn(true);

        Boolean exists = healthInsuranceService.existsByCompanyCode(123456L);

        assertTrue(exists);
        verify(healthInsuranceRepository).existsByCompanyCode(123456L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void existsByPhoneNumberTest() {
        when(healthInsuranceRepository.existsByPhoneNumber("+5491112345678")).thenReturn(true);

        Boolean exists = healthInsuranceService.existsByPhoneNumber("+5491112345678");

        assertTrue(exists);
        verify(healthInsuranceRepository).existsByPhoneNumber("+5491112345678");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void existsByEmailTest() {
        when(healthInsuranceRepository.existsByEmail("contact@osde.com")).thenReturn(true);

        Boolean exists = healthInsuranceService.existsByEmail("contact@osde.com");

        assertTrue(exists);
        verify(healthInsuranceRepository).existsByEmail("contact@osde.com");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }


    // ERROR CASES - ResourceNotFound

    @Test
    void getInexistentHealthInsuranceById() {
        when(healthInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> healthInsuranceService.getHealthInsuranceById(99L));

        assertEquals("Health Insurance not found with id: 99", exception.getMessage());
        verify(healthInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void findInexistentHealthInsuranceById() {
        when(healthInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> healthInsuranceService.findHealthInsuranceById(99L));

        assertEquals("Health Insurance not found with id: 99", exception.getMessage());
        verify(healthInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void updateInexistentHealthInsuranceById() {
        HealthInsuranceUpdateDto updateDto = new HealthInsuranceUpdateDto("OSDE Updated", 123456L, "+5491112345679", "newcontact@osde.com");

        when(healthInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> healthInsuranceService.updateHealthInsuranceById(99L, updateDto));

        assertEquals("Health Insurance not found with id: 99", exception.getMessage());
        verify(healthInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void deleteInexistentHealthInsuranceById() {
        when(healthInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> healthInsuranceService.deleteHealthInsuranceById(99L));

        assertEquals("Health Insurance not found with id: 99", exception.getMessage());
        verify(healthInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void softDeleteInexistentHealthInsuranceById() {
        when(healthInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> healthInsuranceService.softDeleteHealthInsuranceById(99L));

        assertEquals("Health Insurance not found with id: 99", exception.getMessage());
        verify(healthInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void activateInexistentHealthInsuranceById() {
        when(healthInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> healthInsuranceService.activateHealthInsuranceById(99L));

        assertEquals("Health Insurance not found with id: 99", exception.getMessage());
        verify(healthInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void findInexistentHealthInsuranceByCompanyName() {
        when(healthInsuranceRepository.findByCompanyName("Inexistent")).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> healthInsuranceService.findHealthInsuranceByCompanyName("Inexistent"));

        assertEquals("Health Insurance not found with company name: Inexistent", exception.getMessage());
        verify(healthInsuranceRepository).findByCompanyName("Inexistent");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void findInexistentHealthInsuranceByCompanyCode() {
        when(healthInsuranceRepository.findByCompanyCode(999999L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> healthInsuranceService.findHealthInsuranceByCompanyCode(999999L));

        assertEquals("Health Insurance not found with company code: 999999", exception.getMessage());
        verify(healthInsuranceRepository).findByCompanyCode(999999L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void findInexistentHealthInsuranceByPhoneNumber() {
        when(healthInsuranceRepository.findByPhoneNumber("+5499999999999")).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> healthInsuranceService.findHealthInsuranceByPhoneNumber("+5499999999999"));

        assertEquals("Health Insurance not found with phone number: +5499999999999", exception.getMessage());
        verify(healthInsuranceRepository).findByPhoneNumber("+5499999999999");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void findInexistentHealthInsuranceByEmail() {
        when(healthInsuranceRepository.findByEmail("inexistent@test.com")).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> healthInsuranceService.findHealthInsuranceByEmail("inexistent@test.com"));

        assertEquals("Health Insurance not found with email: inexistent@test.com", exception.getMessage());
        verify(healthInsuranceRepository).findByEmail("inexistent@test.com");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }


    // ERROR CASES - DuplicateResourceException

    @Test
    void createHealthInsurance_withDuplicateCompanyName() {
        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto("OSDE", 123456L, "+5491112345678", "contact@osde.com");

        when(healthInsuranceRepository.existsByCompanyName("OSDE")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> healthInsuranceService.createHealthInsurance(createDto));

        assertEquals("Health Insurance with Company Name 'OSDE' already exists", exception.getMessage());
        verify(healthInsuranceRepository).existsByCompanyName("OSDE");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void createHealthInsurance_withDuplicateEmail() {
        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto("OSDE", 123456L, "+5491112345678", "contact@osde.com");

        when(healthInsuranceRepository.existsByCompanyName("OSDE")).thenReturn(false);
        when(healthInsuranceRepository.existsByEmail("contact@osde.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> healthInsuranceService.createHealthInsurance(createDto));

        assertEquals("Health Insurance with Email 'contact@osde.com' already exists", exception.getMessage());
        verify(healthInsuranceRepository).existsByCompanyName("OSDE");
        verify(healthInsuranceRepository).existsByEmail("contact@osde.com");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void createHealthInsurance_withDuplicateCompanyCode() {
        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto("OSDE", 123456L, "+5491112345678", "contact@osde.com");

        when(healthInsuranceRepository.existsByCompanyName("OSDE")).thenReturn(false);
        when(healthInsuranceRepository.existsByEmail("contact@osde.com")).thenReturn(false);
        when(healthInsuranceRepository.existsByCompanyCode(123456L)).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> healthInsuranceService.createHealthInsurance(createDto));

        assertEquals("Health Insurance with Company Code '123456' already exists", exception.getMessage());
        verify(healthInsuranceRepository).existsByCompanyName("OSDE");
        verify(healthInsuranceRepository).existsByEmail("contact@osde.com");
        verify(healthInsuranceRepository).existsByCompanyCode(123456L);
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void createHealthInsurance_withDuplicatePhoneNumber() {
        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto("OSDE", 123456L, "+5491112345678", "contact@osde.com");

        when(healthInsuranceRepository.existsByCompanyName("OSDE")).thenReturn(false);
        when(healthInsuranceRepository.existsByEmail("contact@osde.com")).thenReturn(false);
        when(healthInsuranceRepository.existsByCompanyCode(123456L)).thenReturn(false);
        when(healthInsuranceRepository.existsByPhoneNumber("+5491112345678")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> healthInsuranceService.createHealthInsurance(createDto));

        assertEquals("Health Insurance with Phone Number '+5491112345678' already exists", exception.getMessage());
        verify(healthInsuranceRepository).existsByCompanyName("OSDE");
        verify(healthInsuranceRepository).existsByEmail("contact@osde.com");
        verify(healthInsuranceRepository).existsByCompanyCode(123456L);
        verify(healthInsuranceRepository).existsByPhoneNumber("+5491112345678");
        verifyNoMoreInteractions(healthInsuranceRepository);
    }

    @Test
    void updateHealthInsuranceById_withDuplicateCompanyName() {
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        HealthInsuranceUpdateDto updateDto = new HealthInsuranceUpdateDto("Swiss Medical", null, null, null);

        when(healthInsuranceRepository.findById(1L)).thenReturn(Optional.of(healthInsurance));
        when(healthInsuranceRepository.existsByCompanyName("Swiss Medical")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> healthInsuranceService.updateHealthInsuranceById(1L, updateDto));

        assertEquals("Health Insurance with Company Name 'Swiss Medical' already exists", exception.getMessage());
        verify(healthInsuranceRepository).findById(1L);
        verify(healthInsuranceRepository).existsByCompanyName("Swiss Medical");
    }

    @Test
    void updateHealthInsuranceById_withDuplicateCompanyCode() {
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        HealthInsuranceUpdateDto updateDto = new HealthInsuranceUpdateDto(null, 999999L, null, null);

        when(healthInsuranceRepository.findById(1L)).thenReturn(Optional.of(healthInsurance));
        when(healthInsuranceRepository.existsByCompanyCode(999999L)).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> healthInsuranceService.updateHealthInsuranceById(1L, updateDto));

        assertEquals("Health Insurance with Company Code '999999' already exists", exception.getMessage());
        verify(healthInsuranceRepository).findById(1L);
        verify(healthInsuranceRepository).existsByCompanyCode(999999L);
    }

    @Test
    void updateHealthInsuranceById_withDuplicatePhoneNumber() {
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        HealthInsuranceUpdateDto updateDto = new HealthInsuranceUpdateDto(null, null, "+5499999999999", null);

        when(healthInsuranceRepository.findById(1L)).thenReturn(Optional.of(healthInsurance));
        when(healthInsuranceRepository.existsByPhoneNumber("+5499999999999")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> healthInsuranceService.updateHealthInsuranceById(1L, updateDto));

        assertEquals("Health Insurance with Phone Number '+5499999999999' already exists", exception.getMessage());
        verify(healthInsuranceRepository).findById(1L);
        verify(healthInsuranceRepository).existsByPhoneNumber("+5499999999999");
    }

    @Test
    void updateHealthInsuranceById_withDuplicateEmail() {
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");
        healthInsurance.setCompanyCode(123456L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);

        HealthInsuranceUpdateDto updateDto = new HealthInsuranceUpdateDto(null, null, null, "duplicate@test.com");

        when(healthInsuranceRepository.findById(1L)).thenReturn(Optional.of(healthInsurance));
        when(healthInsuranceRepository.existsByEmail("duplicate@test.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> healthInsuranceService.updateHealthInsuranceById(1L, updateDto));

        assertEquals("Health Insurance with Email 'duplicate@test.com' already exists", exception.getMessage());
        verify(healthInsuranceRepository).findById(1L);
        verify(healthInsuranceRepository).existsByEmail("duplicate@test.com");
    }
}
