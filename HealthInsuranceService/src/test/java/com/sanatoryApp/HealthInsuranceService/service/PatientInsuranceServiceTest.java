package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.PatientInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.externalService.PatientDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceCreateResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.repository.IPatientInsuranceRepository;
import com.sanatoryApp.HealthInsuranceService.repository.UserServiceApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientInsuranceServiceTest {

    @Mock
    private IPatientInsuranceRepository patientInsuranceRepository;

    @Mock
    private IHealthInsuranceService healthInsuranceService;

    @Mock
    private ICoveragePlanService coveragePlanService;

    @Mock
    private UserServiceApi userServiceApi;

    @InjectMocks
    private PatientInsuranceService patientInsuranceService;

    @Test
    void findPatientInsuranceByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, LocalDate.now(), true);

        when(patientInsuranceRepository.findById(1L)).thenReturn(Optional.of(patientInsurance));

        //Act
        PatientInsuranceResponseDto responseDto = patientInsuranceService.findPatientInsuranceById(1L);

        //Assert & Verify
        assertEquals(1L, responseDto.id());
        assertEquals("30345678", responseDto.patientDni());
        assertEquals("CRED-003-1234", responseDto.credentialNumber());
        verify(patientInsuranceRepository).findById(1L);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }


    @Test
    void createPatientInsuranceTest() {
       //Arrange
        PatientDto patientDto=new PatientDto(2L,"30345678","Maria","Lopez","mariaLopez@gmail.com","+543456789132");
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, LocalDate.now(), true);

        PatientInsuranceCreateDto createDto=new PatientInsuranceCreateDto("30345678","CRED-003-1234",201L,21L);

        //Mock
        when(userServiceApi.getPatientByDni("30345678")).thenReturn(patientDto);
        when(healthInsuranceService.getHealthInsuranceById(201L)).thenReturn(healthInsurance);
        when(coveragePlanService.getCoveragePlanById(21L)).thenReturn(coveragePlan);
        when(coveragePlanService.existsByIdAndHealthInsuranceId(21L,201L)).thenReturn(true);
        when(patientInsuranceRepository.save(any(PatientInsurance.class))).thenReturn(patientInsurance);

        //Act
        PatientInsuranceCreateResponseDto responseDto=patientInsuranceService.createPatientInsurance(createDto);

        //Assert & Verify
        assertEquals(1L,responseDto.id());
        assertEquals("30345678",responseDto.patientDni());
        assertEquals("CRED-003-1234",responseDto.credentialNumber());
        verify(userServiceApi).getPatientByDni(anyString());
        verify(healthInsuranceService).getHealthInsuranceById(anyLong());
        verify(coveragePlanService).getCoveragePlanById(anyLong());
        verify(coveragePlanService).existsByIdAndHealthInsuranceId(anyLong(),anyLong());
        verify(patientInsuranceRepository).save(any(PatientInsurance.class));
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void getPatientInsuranceByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, LocalDate.now(), true);

        when(patientInsuranceRepository.findById(1L)).thenReturn(Optional.of(patientInsurance));

        //Act
        PatientInsurance result = patientInsuranceService.getPatientInsuranceById(1L);

        //Assert & Verify
        assertEquals(1L, result.getId());
        assertEquals("30345678", result.getPatientDni());
        assertEquals("CRED-003-1234", result.getCredentialNumber());
        verify(patientInsuranceRepository).findById(1L);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void updatePatientInsuranceCoveragePlanByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan oldCoveragePlan = new CoveragePlan();
        oldCoveragePlan.setId(21L);
        oldCoveragePlan.setName("Plan 210");

        CoveragePlan newCoveragePlan = new CoveragePlan();
        newCoveragePlan.setId(22L);
        newCoveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, oldCoveragePlan, LocalDate.now(), true);
        PatientInsurance updatedPatientInsurance = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, newCoveragePlan, LocalDate.now(), true);

        when(patientInsuranceRepository.findById(1L)).thenReturn(Optional.of(patientInsurance));
        when(coveragePlanService.getCoveragePlanById(22L)).thenReturn(newCoveragePlan);
        when(patientInsuranceRepository.save(any(PatientInsurance.class))).thenReturn(updatedPatientInsurance);

        //Act
        PatientInsuranceResponseDto responseDto = patientInsuranceService.updatePatientInsuranceCoveragePlanById(1L, 22L);

        //Assert & Verify
        assertEquals(1L, responseDto.id());
        assertEquals(22L, responseDto.coveragePlanId());
        verify(patientInsuranceRepository).findById(anyLong());
        verify(coveragePlanService).getCoveragePlanById(anyLong());
        verify(patientInsuranceRepository).save(any(PatientInsurance.class));
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void deletePatientInsuranceByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, LocalDate.now(), true);

        when(patientInsuranceRepository.findById(1L)).thenReturn(Optional.of(patientInsurance));

        //Act
        patientInsuranceService.deletePatientInsuranceById(1L);

        //Assert & Verify
        verify(patientInsuranceRepository).findById(anyLong());
        verify(patientInsuranceRepository).delete(any(PatientInsurance.class));
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void softDeletePatientInsuranceByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, LocalDate.now(), true);

        when(patientInsuranceRepository.findById(1L)).thenReturn(Optional.of(patientInsurance));

        //Act
        patientInsuranceService.softDeletePatientInsuranceById(1L);

        //Assert & Verify
        verify(patientInsuranceRepository).findById(anyLong());
        verify(patientInsuranceRepository).save(any(PatientInsurance.class));
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void activatePatientInsuranceByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, LocalDate.now(), false);

        when(patientInsuranceRepository.findById(1L)).thenReturn(Optional.of(patientInsurance));

        //Act
        patientInsuranceService.activatePatientInsuranceById(1L);

        //Assert & Verify
        verify(patientInsuranceRepository).findById(anyLong());
        verify(patientInsuranceRepository).save(any(PatientInsurance.class));
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void countActivePatientsByInsuranceIdTest() {
        //Arrange
        when(patientInsuranceRepository.countActivePatientsByInsuranceId(201L)).thenReturn(5);

        //Act
        Integer count = patientInsuranceService.countActivePatientsByInsuranceId(201L);

        //Assert & Verify
        assertEquals(5, count);
        verify(patientInsuranceRepository).countActivePatientsByInsuranceId(anyLong());
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByPatientDniTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance1 = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, LocalDate.now(), true);
        PatientInsurance patientInsurance2 = new PatientInsurance(2L, "30345678", "CRED-003-5678", healthInsurance, coveragePlan, LocalDate.now(), true);

        when(patientInsuranceRepository.findByPatientDni("30345678")).thenReturn(List.of(patientInsurance1, patientInsurance2));

        //Act
        List<PatientInsuranceResponseDto> resultList = patientInsuranceService.findPatientInsuranceByPatientDni("30345678");

        //Assert & Verify
        assertEquals(2, resultList.size());
        assertEquals("30345678", resultList.get(0).patientDni());
        assertEquals("30345678", resultList.get(1).patientDni());
        verify(patientInsuranceRepository).findByPatientDni(anyString());
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByCredentialNumberTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, LocalDate.now(), true);

        when(patientInsuranceRepository.findByCredentialNumber("CRED-003-1234")).thenReturn(Optional.of(patientInsurance));

        //Act
        PatientInsuranceResponseDto responseDto = patientInsuranceService.findPatientInsuranceByCredentialNumber("CRED-003-1234");

        //Assert & Verify
        assertEquals("CRED-003-1234", responseDto.credentialNumber());
        assertEquals("30345678", responseDto.patientDni());
        verify(patientInsuranceRepository).findByCredentialNumber(anyString());
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByHealthInsuranceTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance1 = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, LocalDate.now(), true);
        PatientInsurance patientInsurance2 = new PatientInsurance(2L, "40345678", "CRED-003-5678", healthInsurance, coveragePlan, LocalDate.now(), true);

        when(patientInsuranceRepository.findByHealthInsurance_Id(201L)).thenReturn(List.of(patientInsurance1, patientInsurance2));

        //Act
        List<PatientInsuranceResponseDto> resultList = patientInsuranceService.findPatientInsuranceByHealthInsurance(201L);

        //Assert & Verify
        assertEquals(2, resultList.size());
        assertEquals(201L, resultList.get(0).healthInsuranceId());
        assertEquals(201L, resultList.get(1).healthInsuranceId());
        verify(patientInsuranceRepository).findByHealthInsurance_Id(anyLong());
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByCoveragePlanIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance1 = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, LocalDate.now(), true);
        PatientInsurance patientInsurance2 = new PatientInsurance(2L, "40345678", "CRED-003-5678", healthInsurance, coveragePlan, LocalDate.now(), true);

        when(patientInsuranceRepository.findByCoveragePlan_Id(21L)).thenReturn(List.of(patientInsurance1, patientInsurance2));

        //Act
        List<PatientInsuranceResponseDto> resultList = patientInsuranceService.findPatientInsuranceByCoveragePlanId(21L);

        //Assert & Verify
        assertEquals(2, resultList.size());
        assertEquals(21L, resultList.get(0).coveragePlanId());
        assertEquals(21L, resultList.get(1).coveragePlanId());
        verify(patientInsuranceRepository).findByCoveragePlan_Id(anyLong());
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByCreatedAtTest() {
        //Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 15);
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance1 = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, testDate, true);
        PatientInsurance patientInsurance2 = new PatientInsurance(2L, "40345678", "CRED-003-5678", healthInsurance, coveragePlan, testDate, true);

        when(patientInsuranceRepository.findByCreatedAt(testDate)).thenReturn(List.of(patientInsurance1, patientInsurance2));

        //Act
        List<PatientInsuranceResponseDto> resultList = patientInsuranceService.findPatientInsuranceByCreatedAt(testDate);

        //Assert & Verify
        assertEquals(2, resultList.size());
        assertEquals(testDate, resultList.get(0).createdAt());
        assertEquals(testDate, resultList.get(1).createdAt());
        verify(patientInsuranceRepository).findByCreatedAt(any(LocalDate.class));
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByCreatedAfterDateTest() {
        //Arrange
        LocalDate testDate = LocalDate.of(2024, 1, 1);
        LocalDate afterDate = LocalDate.of(2024, 1, 15);
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsurance patientInsurance1 = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, coveragePlan, afterDate, true);
        PatientInsurance patientInsurance2 = new PatientInsurance(2L, "40345678", "CRED-003-5678", healthInsurance, coveragePlan, afterDate, true);

        when(patientInsuranceRepository.findByCreatedAtAfter(testDate)).thenReturn(List.of(patientInsurance1, patientInsurance2));

        //Act
        List<PatientInsuranceResponseDto> resultList = patientInsuranceService.findPatientInsuranceByCreatedAfterDate(testDate);

        //Assert & Verify
        assertEquals(2, resultList.size());
        verify(patientInsuranceRepository).findByCreatedAtAfter(any(LocalDate.class));
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void existsByCredentialNumberTest() {
        //Arrange
        when(patientInsuranceRepository.existsByCredentialNumber("CRED-003-1234")).thenReturn(true);

        //Act
        Boolean exists = patientInsuranceService.existsByCredentialNumber("CRED-003-1234");

        //Assert & Verify
        assertTrue(exists);
        verify(patientInsuranceRepository).existsByCredentialNumber(anyString());
        verifyNoMoreInteractions(patientInsuranceRepository);
    }


    // ERROR CASES - ResourceNotFound

    @Test
    void findInexistentPatientInsuranceById() {
        when(patientInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.findPatientInsuranceById(99L));

        assertEquals("Patient Insurance not found with id: 99", exception.getMessage());
        verify(patientInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void getInexistentPatientInsuranceById() {
        when(patientInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.getPatientInsuranceById(99L));

        assertEquals("Patient Insurance not found with id: 99", exception.getMessage());
        verify(patientInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void updateInexistentPatientInsuranceCoveragePlanById() {
        when(patientInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.updatePatientInsuranceCoveragePlanById(99L, 22L));

        assertEquals("Patient Insurance not found with id: 99", exception.getMessage());
        verify(patientInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void deleteInexistentPatientInsuranceById() {
        when(patientInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.deletePatientInsuranceById(99L));

        assertEquals("Patient Insurance not found with id: 99", exception.getMessage());
        verify(patientInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void softDeleteInexistentPatientInsuranceById() {
        when(patientInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.softDeletePatientInsuranceById(99L));

        assertEquals("Patient Insurance not found with id: 99", exception.getMessage());
        verify(patientInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void activateInexistentPatientInsuranceById() {
        when(patientInsuranceRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.activatePatientInsuranceById(99L));

        assertEquals("Patient Insurance not found with id: 99", exception.getMessage());
        verify(patientInsuranceRepository).findById(99L);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByInexistentPatientDni() {
        when(patientInsuranceRepository.findByPatientDni("99999999")).thenReturn(List.of());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.findPatientInsuranceByPatientDni("99999999"));

        assertEquals("Patients Insurance not found with patient dni 99999999", exception.getMessage());
        verify(patientInsuranceRepository).findByPatientDni("99999999");
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByInexistentCredentialNumber() {
        when(patientInsuranceRepository.findByCredentialNumber("CRED-999-9999")).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.findPatientInsuranceByCredentialNumber("CRED-999-9999"));

        assertEquals("Patient Insurance not found with credential number: CRED-999-9999", exception.getMessage());
        verify(patientInsuranceRepository).findByCredentialNumber("CRED-999-9999");
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByInexistentHealthInsurance() {
        when(patientInsuranceRepository.findByHealthInsurance_Id(999L)).thenReturn(List.of());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.findPatientInsuranceByHealthInsurance(999L));

        assertEquals("No Patients Insurance found with health insurance id 999", exception.getMessage());
        verify(patientInsuranceRepository).findByHealthInsurance_Id(999L);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByCoveragePlanId_notFound() {
        when(patientInsuranceRepository.findByCoveragePlan_Id(999L)).thenReturn(List.of());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.findPatientInsuranceByCoveragePlanId(999L));

        assertEquals("No Patients Insurance found with coverage plan id 999", exception.getMessage());
        verify(patientInsuranceRepository).findByCoveragePlan_Id(999L);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByCreatedAt_notFound() {
        LocalDate testDate = LocalDate.of(2020, 1, 1);
        when(patientInsuranceRepository.findByCreatedAt(testDate)).thenReturn(List.of());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.findPatientInsuranceByCreatedAt(testDate));

        assertEquals("No Patients Insurance found created at 2020-01-01", exception.getMessage());
        verify(patientInsuranceRepository).findByCreatedAt(testDate);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }

    @Test
    void findPatientInsuranceByCreatedAfterDate_notFound() {
        LocalDate testDate = LocalDate.of(2030, 1, 1);
        when(patientInsuranceRepository.findByCreatedAtAfter(testDate)).thenReturn(List.of());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.findPatientInsuranceByCreatedAfterDate(testDate));

        assertEquals("No Patients Insurance found created after 2030-01-01", exception.getMessage());
        verify(patientInsuranceRepository).findByCreatedAtAfter(testDate);
        verifyNoMoreInteractions(patientInsuranceRepository);
    }


    // ERROR CASES - Other exceptions

    @Test
    void createPatientInsurance_withInexistentPatient() {
        //Arrange
        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("99999999", "CRED-003-1234", 201L, 21L);

        when(userServiceApi.getPatientByDni("99999999")).thenThrow(feign.FeignException.NotFound.class);

        //Act & Assert
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.createPatientInsurance(createDto));

        assertEquals("Patient with DNI: 99999999 not found.", exception.getMessage());
        verify(userServiceApi).getPatientByDni("99999999");
    }

    @Test
    void createPatientInsurance_withInexistentHealthInsurance() {
        //Arrange
        PatientDto patientDto = new PatientDto(2L, "30345678", "Maria", "Lopez", "mariaLopez@gmail.com", "+543456789132");
        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("30345678", "CRED-003-1234", 999L, 21L);

        when(userServiceApi.getPatientByDni("30345678")).thenReturn(patientDto);
        when(healthInsuranceService.getHealthInsuranceById(999L)).thenThrow(new ResourceNotFound("Health Insurance not found with id: 999"));

        //Act & Assert
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.createPatientInsurance(createDto));

        assertEquals("Health Insurance not found with id: 999", exception.getMessage());
        verify(userServiceApi).getPatientByDni("30345678");
        verify(healthInsuranceService).getHealthInsuranceById(999L);
    }

    @Test
    void createPatientInsurance_withInexistentCoveragePlan() {
        //Arrange
        PatientDto patientDto = new PatientDto(2L, "30345678", "Maria", "Lopez", "mariaLopez@gmail.com", "+543456789132");
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("30345678", "CRED-003-1234", 201L, 999L);

        when(userServiceApi.getPatientByDni("30345678")).thenReturn(patientDto);
        when(healthInsuranceService.getHealthInsuranceById(201L)).thenReturn(healthInsurance);
        when(coveragePlanService.getCoveragePlanById(999L)).thenThrow(new ResourceNotFound("Coverage Plan not found with id: 999"));

        //Act & Assert
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.createPatientInsurance(createDto));

        assertEquals("Coverage Plan not found with id: 999", exception.getMessage());
        verify(userServiceApi).getPatientByDni("30345678");
        verify(healthInsuranceService).getHealthInsuranceById(201L);
        verify(coveragePlanService).getCoveragePlanById(999L);
    }

    @Test
    void createPatientInsurance_withInvalidCoveragePlanForHealthInsurance() {
        //Arrange
        PatientDto patientDto = new PatientDto(2L, "30345678", "Maria", "Lopez", "mariaLopez@gmail.com", "+543456789132");
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(21L);
        coveragePlan.setName("Plan 310");

        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("30345678", "CRED-003-1234", 201L, 21L);

        when(userServiceApi.getPatientByDni("30345678")).thenReturn(patientDto);
        when(healthInsuranceService.getHealthInsuranceById(201L)).thenReturn(healthInsurance);
        when(coveragePlanService.getCoveragePlanById(21L)).thenReturn(coveragePlan);
        when(coveragePlanService.existsByIdAndHealthInsuranceId(21L, 201L)).thenReturn(false);

        //Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> patientInsuranceService.createPatientInsurance(createDto));

        assertEquals("No coverage Plan found with id: 21 and Health Insurance id: 201", exception.getMessage());
        verify(userServiceApi).getPatientByDni("30345678");
        verify(healthInsuranceService).getHealthInsuranceById(201L);
        verify(coveragePlanService).getCoveragePlanById(21L);
        verify(coveragePlanService).existsByIdAndHealthInsuranceId(21L, 201L);
    }

    @Test
    void createPatientInsurance_withUserServiceCommunicationError() {
        //Arrange
        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("30345678", "CRED-003-1234", 201L, 21L);

        when(userServiceApi.getPatientByDni("30345678")).thenThrow(feign.FeignException.InternalServerError.class);

        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> patientInsuranceService.createPatientInsurance(createDto));

        assertTrue(exception.getMessage().contains("Error communicating with User Service"));
        verify(userServiceApi).getPatientByDni("30345678");
    }

    @Test
    void updatePatientInsuranceCoveragePlanById_withInexistentCoveragePlan() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(201L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan oldCoveragePlan = new CoveragePlan();
        oldCoveragePlan.setId(21L);
        oldCoveragePlan.setName("Plan 210");

        PatientInsurance patientInsurance = new PatientInsurance(1L, "30345678", "CRED-003-1234", healthInsurance, oldCoveragePlan, LocalDate.now(), true);

        when(patientInsuranceRepository.findById(1L)).thenReturn(Optional.of(patientInsurance));
        when(coveragePlanService.getCoveragePlanById(999L)).thenThrow(new ResourceNotFound("Coverage Plan not found with id: 999"));

        //Act & Assert
        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> patientInsuranceService.updatePatientInsuranceCoveragePlanById(1L, 999L));

        assertEquals("Coverage Plan not found with id: 999", exception.getMessage());
        verify(patientInsuranceRepository).findById(1L);
        verify(coveragePlanService).getCoveragePlanById(999L);
    }
}
