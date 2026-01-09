package com.sanatoryApp.HealthInsuranceService.service;

import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.exception.DuplicateResourceException;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.repository.ICoveragePlanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CoveragePlanServiceTest {

    @Mock
    private ICoveragePlanRepository coveragePlanRepository;

    @Mock
    private IHealthInsuranceService healthInsuranceService;

    @InjectMocks
    private CoveragePlanService coveragePlanService;

    @Test
    void getCoveragePlanByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(1L);
        coveragePlan.setName("Plan 310");
        coveragePlan.setDescription("Plan básico");
        coveragePlan.setCoverageValuePercentage(new BigDecimal("75.00"));
        coveragePlan.setHealthInsurance(healthInsurance);
        coveragePlan.setActive(true);

        when(coveragePlanRepository.findById(1L)).thenReturn(Optional.of(coveragePlan));

        //Act
        CoveragePlan result = coveragePlanService.getCoveragePlanById(1L);

        //Assert & Verify
        assertEquals(1L, result.getId());
        assertEquals("Plan 310", result.getName());
        assertEquals(new BigDecimal("75.00"), result.getCoverageValuePercentage());
        verify(coveragePlanRepository).findById(1L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void findCoveragePlanByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(1L);
        coveragePlan.setName("Plan 310");
        coveragePlan.setDescription("Plan básico");
        coveragePlan.setCoverageValuePercentage(new BigDecimal("75.00"));
        coveragePlan.setHealthInsurance(healthInsurance);
        coveragePlan.setActive(true);

        when(coveragePlanRepository.findById(1L)).thenReturn(Optional.of(coveragePlan));

        //Act
        CoveragePlanResponseDto result = coveragePlanService.findCoveragePlanById(1L);

        //Assert & Verify
        assertEquals(1L, result.id());
        assertEquals("Plan 310", result.name());
        assertEquals(new BigDecimal("75.00"), result.coverageValuePercentage());
        verify(coveragePlanRepository).findById(1L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void createCoveragePlanTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlanCreateDto createDto = new CoveragePlanCreateDto(1L, "Plan 310", "Plan básico", new BigDecimal("75.00"));

        CoveragePlan coveragePlan = createDto.toEntity(healthInsurance);
        coveragePlan.setId(1L);

        when(healthInsuranceService.getHealthInsuranceById(1L)).thenReturn(healthInsurance);
        when(coveragePlanRepository.existsByNameAndInsurance("Plan 310", 1L, null)).thenReturn(false);
        when(coveragePlanRepository.save(any(CoveragePlan.class))).thenReturn(coveragePlan);

        //Act
        CoveragePlanResponseDto result = coveragePlanService.createCoveragePlan(createDto);

        //Assert & Verify
        assertEquals("Plan 310", result.name());
        assertEquals("Plan básico", result.description());
        assertEquals(new BigDecimal("75.00"), result.coverageValuePercentage());
        verify(healthInsuranceService).getHealthInsuranceById(1L);
        verify(coveragePlanRepository).existsByNameAndInsurance(anyString(), anyLong(), eq(null));
        verify(coveragePlanRepository).save(any(CoveragePlan.class));
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void updateCoveragePlanByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(1L);
        coveragePlan.setName("Plan 310");
        coveragePlan.setDescription("Plan básico");
        coveragePlan.setCoverageValuePercentage(new BigDecimal("75.00"));
        coveragePlan.setHealthInsurance(healthInsurance);
        coveragePlan.setActive(true);

        CoveragePlan updatedCoveragePlan = new CoveragePlan();
        updatedCoveragePlan.setId(1L);
        updatedCoveragePlan.setName("Plan 310 Plus");
        updatedCoveragePlan.setDescription("Plan premium");
        updatedCoveragePlan.setCoverageValuePercentage(new BigDecimal("90.00"));
        updatedCoveragePlan.setHealthInsurance(healthInsurance);
        updatedCoveragePlan.setActive(true);

        CoveragePlanUpdateDto updateDto = new CoveragePlanUpdateDto("Plan 310 Plus", "Plan premium", new BigDecimal("90.00"));

        when(coveragePlanRepository.findById(1L)).thenReturn(Optional.of(coveragePlan));
        when(coveragePlanRepository.existsByNameAndInsurance("Plan 310 Plus", 1L, 1L)).thenReturn(false);
        when(coveragePlanRepository.save(any(CoveragePlan.class))).thenReturn(updatedCoveragePlan);

        //Act
        CoveragePlanResponseDto result = coveragePlanService.updateCoveragePlanById(1L, updateDto);

        //Assert & Verify
        assertEquals("Plan 310 Plus", result.name());
        assertEquals("Plan premium", result.description());
        assertEquals(new BigDecimal("90.00"), result.coverageValuePercentage());
        verify(coveragePlanRepository).findById(1L);
        verify(coveragePlanRepository).save(any(CoveragePlan.class));
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void softDeleteCoveragePlanByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(1L);
        coveragePlan.setName("Plan 310");
        coveragePlan.setDescription("Plan básico");
        coveragePlan.setCoverageValuePercentage(new BigDecimal("75.00"));
        coveragePlan.setHealthInsurance(healthInsurance);
        coveragePlan.setActive(true);

        when(coveragePlanRepository.findById(1L)).thenReturn(Optional.of(coveragePlan));

        //Act
        coveragePlanService.softDeleteCoveragePlanById(1L);

        //Assert & Verify
        verify(coveragePlanRepository).findById(1L);
        verify(coveragePlanRepository).save(any(CoveragePlan.class));
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void deleteCoveragePlanByIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(1L);
        coveragePlan.setName("Plan 310");
        coveragePlan.setDescription("Plan básico");
        coveragePlan.setCoverageValuePercentage(new BigDecimal("75.00"));
        coveragePlan.setHealthInsurance(healthInsurance);
        coveragePlan.setActive(true);

        when(coveragePlanRepository.findById(1L)).thenReturn(Optional.of(coveragePlan));

        //Act
        coveragePlanService.deleteCoveragePlanById(1L);

        //Assert & Verify
        verify(coveragePlanRepository).findById(1L);
        verify(coveragePlanRepository).delete(any(CoveragePlan.class));
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void findByHealthInsuranceIdTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan1 = new CoveragePlan();
        coveragePlan1.setId(1L);
        coveragePlan1.setName("Plan 210");
        coveragePlan1.setDescription("Plan básico");
        coveragePlan1.setCoverageValuePercentage(new BigDecimal("60.00"));
        coveragePlan1.setHealthInsurance(healthInsurance);
        coveragePlan1.setActive(true);

        CoveragePlan coveragePlan2 = new CoveragePlan();
        coveragePlan2.setId(2L);
        coveragePlan2.setName("Plan 310");
        coveragePlan2.setDescription("Plan premium");
        coveragePlan2.setCoverageValuePercentage(new BigDecimal("80.00"));
        coveragePlan2.setHealthInsurance(healthInsurance);
        coveragePlan2.setActive(true);

        when(coveragePlanRepository.findByHealthInsurance_Id(1L)).thenReturn(List.of(coveragePlan1, coveragePlan2));

        //Act
        List<CoveragePlanResponseDto> resultList = coveragePlanService.findByHealthInsuranceId(1L);

        //Assert & Verify
        assertEquals(2, resultList.size());
        assertEquals("Plan 210", resultList.get(0).name());
        assertEquals("Plan 310", resultList.get(1).name());
        verify(coveragePlanRepository).findByHealthInsurance_Id(1L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void findByHealthInsuranceIdAndIsActiveTrueTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan1 = new CoveragePlan();
        coveragePlan1.setId(1L);
        coveragePlan1.setName("Plan 210");
        coveragePlan1.setDescription("Plan básico");
        coveragePlan1.setCoverageValuePercentage(new BigDecimal("60.00"));
        coveragePlan1.setHealthInsurance(healthInsurance);
        coveragePlan1.setActive(true);

        when(coveragePlanRepository.findByHealthInsurance_IdAndIsActiveTrue(1L)).thenReturn(List.of(coveragePlan1));

        //Act
        List<CoveragePlanResponseDto> resultList = coveragePlanService.findByHealthInsuranceIdAndIsActiveTrue(1L);

        //Assert & Verify
        assertEquals(1, resultList.size());
        assertEquals("Plan 210", resultList.get(0).name());
        assertTrue(resultList.get(0).isActive());
        verify(coveragePlanRepository).findByHealthInsurance_IdAndIsActiveTrue(1L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void findCoveragePlanByNameTest() {
        //Arrange
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(1L);
        coveragePlan.setName("Plan 310");
        coveragePlan.setDescription("Plan básico");
        coveragePlan.setCoverageValuePercentage(new BigDecimal("75.00"));
        coveragePlan.setHealthInsurance(healthInsurance);
        coveragePlan.setActive(true);

        when(coveragePlanRepository.findByName("Plan 310")).thenReturn(Optional.of(coveragePlan));

        //Act
        CoveragePlanResponseDto result = coveragePlanService.findCoveragePlanByName("Plan 310");

        //Assert & Verify
        assertEquals("Plan 310", result.name());
        verify(coveragePlanRepository).findByName("Plan 310");
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void existsByNameTest() {
        when(coveragePlanRepository.existsByNameIgnoreCase("Plan 310")).thenReturn(true);

        Boolean exists = coveragePlanService.existsByName("Plan 310");

        assertTrue(exists);
        verify(coveragePlanRepository).existsByNameIgnoreCase("Plan 310");
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void existsByIdTest() {
        when(coveragePlanRepository.existsById(1L)).thenReturn(true);

        boolean exists = coveragePlanService.existsById(1L);

        assertTrue(exists);
        verify(coveragePlanRepository).existsById(1L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void existsByIdAndHealthInsuranceIdTest() {
        when(coveragePlanRepository.existsByIdAndHealthInsuranceId(1L, 1L)).thenReturn(true);

        boolean exists = coveragePlanService.existsByIdAndHealthInsuranceId(1L, 1L);

        assertTrue(exists);
        verify(coveragePlanRepository).existsByIdAndHealthInsuranceId(1L, 1L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void existsByNameAndInsuranceTest() {
        when(coveragePlanRepository.existsByNameAndInsurance("Plan 310", 1L, null)).thenReturn(true);

        Boolean exists = coveragePlanService.existsByNameAndInsurance("Plan 310", 1L, null);

        assertTrue(exists);
        verify(coveragePlanRepository).existsByNameAndInsurance("Plan 310", 1L, null);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void countActivePlansTest() {
        when(coveragePlanRepository.countActivePlans()).thenReturn(5);

        Integer count = coveragePlanService.countActivePlans();

        assertEquals(5, count);
        verify(coveragePlanRepository).countActivePlans();
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void countActivePlanByHealthInsuranceTest() {
        when(coveragePlanRepository.countActivePlanByHealthInsurance(1L)).thenReturn(3);

        Integer count = coveragePlanService.countActivePlanByHealthInsurance(1L);

        assertEquals(3, count);
        verify(coveragePlanRepository).countActivePlanByHealthInsurance(1L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }


    // ERROR CASES - ResourceNotFound

    @Test
    void getInexistentCoveragePlanById() {
        when(coveragePlanRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> coveragePlanService.getCoveragePlanById(99L));

        assertEquals("Coverage Plan not found with id: 99", exception.getMessage());
        verify(coveragePlanRepository).findById(99L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void findInexistentCoveragePlanById() {
        when(coveragePlanRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> coveragePlanService.findCoveragePlanById(99L));

        assertEquals("Coverage Plan not found with id: 99", exception.getMessage());
        verify(coveragePlanRepository).findById(99L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void updateInexistentCoveragePlanById() {
        CoveragePlanUpdateDto updateDto = new CoveragePlanUpdateDto("Plan 310 Plus", "Plan premium", new BigDecimal("90.00"));

        when(coveragePlanRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> coveragePlanService.updateCoveragePlanById(99L, updateDto));

        assertEquals("Coverage Plan not found with id: 99", exception.getMessage());
        verify(coveragePlanRepository).findById(99L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void softDeleteInexistentCoveragePlanById() {
        when(coveragePlanRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> coveragePlanService.softDeleteCoveragePlanById(99L));

        assertEquals("Coverage Plan not found with id: 99", exception.getMessage());
        verify(coveragePlanRepository).findById(99L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void deleteInexistentCoveragePlanById() {
        when(coveragePlanRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> coveragePlanService.deleteCoveragePlanById(99L));

        assertEquals("Coverage Plan not found with id: 99", exception.getMessage());
        verify(coveragePlanRepository).findById(99L);
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void findInexistentCoveragePlanByName() {
        when(coveragePlanRepository.findByName("Inexistent Plan")).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> coveragePlanService.findCoveragePlanByName("Inexistent Plan"));

        assertEquals("Coverage Plan not found with name: Inexistent Plan", exception.getMessage());
        verify(coveragePlanRepository).findByName("Inexistent Plan");
        verifyNoMoreInteractions(coveragePlanRepository);
    }

    @Test
    void createCoveragePlan_withInexistentHealthInsurance() {
        CoveragePlanCreateDto createDto = new CoveragePlanCreateDto(999L, "Plan 310", "Plan básico", new BigDecimal("75.00"));

        when(healthInsuranceService.getHealthInsuranceById(999L))
                .thenThrow(new ResourceNotFound("Health Insurance not found with id: 999"));

        ResourceNotFound exception = assertThrows(ResourceNotFound.class,
                () -> coveragePlanService.createCoveragePlan(createDto));

        assertEquals("Health Insurance not found with id: 999", exception.getMessage());
        verify(healthInsuranceService).getHealthInsuranceById(999L);
    }


    // ERROR CASES - DuplicateResourceException

    @Test
    void createCoveragePlan_withDuplicateNameForSameInsurance() {
        CoveragePlanCreateDto createDto = new CoveragePlanCreateDto(1L, "Plan 310", "Plan básico", new BigDecimal("75.00"));

        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");

        when(healthInsuranceService.getHealthInsuranceById(1L)).thenReturn(healthInsurance);
        when(coveragePlanRepository.existsByNameAndInsurance("Plan 310", 1L, null)).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> coveragePlanService.createCoveragePlan(createDto));

        assertEquals("Already exists a Coverage Plan with name Plan 310 and insurance id 1", exception.getMessage());
        verify(healthInsuranceService).getHealthInsuranceById(1L);
        verify(coveragePlanRepository).existsByNameAndInsurance("Plan 310", 1L, null);
    }

    @Test
    void updateCoveragePlanById_withDuplicateNameForSameInsurance() {
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setId(1L);
        healthInsurance.setCompanyName("OSDE");

        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setId(1L);
        coveragePlan.setName("Plan 310");
        coveragePlan.setDescription("Plan básico");
        coveragePlan.setCoverageValuePercentage(new BigDecimal("75.00"));
        coveragePlan.setHealthInsurance(healthInsurance);
        coveragePlan.setActive(true);

        CoveragePlanUpdateDto updateDto = new CoveragePlanUpdateDto("Plan 210", null, null);

        when(coveragePlanRepository.findById(1L)).thenReturn(Optional.of(coveragePlan));
        when(coveragePlanRepository.existsByNameAndInsurance("Plan 210", 1L, 1L)).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
                () -> coveragePlanService.updateCoveragePlanById(1L, updateDto));

        assertEquals("Already exists a Coverage Plan with name Plan 210 and insurance id 1", exception.getMessage());
        verify(coveragePlanRepository).findById(1L);
        verify(coveragePlanRepository).existsByNameAndInsurance("Plan 210", 1L, 1L);
    }
}
