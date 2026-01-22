package com.sanatoryApp.HealthInsuranceService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.HealthInsuranceService.config.SecurityConfig;
import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.HealthInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.exception.DuplicateResourceException;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.service.ICoveragePlanService;
import com.sanatoryApp.HealthInsuranceService.service.IHealthInsuranceService;
import com.sanatoryApp.HealthInsuranceService.service.IPatientInsuranceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HealthInsuranceController.class)
@Import(SecurityConfig.class)
public class HealthInsuranceControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IHealthInsuranceService healthInsuranceService;

    @MockBean
    private ICoveragePlanService coveragePlanService;

    @MockBean
    private IPatientInsuranceService patientInsuranceService;

    // Helpers
    private final HealthInsuranceResponseDto healthInsuranceResponse = new HealthInsuranceResponseDto(
            1L, "Osde", 12345L, "+5491112345678", "contact@osde.com", true
    );
    private static final String BASE_URL = "/api/v1/health-insurances";

    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findHealthInsuranceByIdTest() throws Exception {
        when(healthInsuranceService.findHealthInsuranceById(1L)).thenReturn(healthInsuranceResponse);

        mvc.perform(get(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(healthInsuranceResponse.id()))
                .andExpect(jsonPath("$.companyName").value(healthInsuranceResponse.companyName()));

        verify(healthInsuranceService).findHealthInsuranceById(anyLong());
        verifyNoMoreInteractions(healthInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentHealthInsuranceByIdTest() throws Exception {
        when(healthInsuranceService.findHealthInsuranceById(1L))
                .thenThrow(new ResourceNotFound("Health Insurance not found with id: 1"));

        mvc.perform(get(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Health Insurance not found with id: 1"));

        verify(healthInsuranceService).findHealthInsuranceById(anyLong());
        verifyNoMoreInteractions(healthInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findHealthInsuranceByCompanyNameTest() throws Exception {
        when(healthInsuranceService.findHealthInsuranceByCompanyName(healthInsuranceResponse.companyName()))
                .thenReturn(healthInsuranceResponse);

        mvc.perform(get(BASE_URL + "/company-name/{companyName}", healthInsuranceResponse.companyName())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.companyName").value(healthInsuranceResponse.companyName()))
                .andExpect(jsonPath("$.companyCode").value(healthInsuranceResponse.companyCode()));

        verify(healthInsuranceService).findHealthInsuranceByCompanyName(anyString());
        verifyNoMoreInteractions(healthInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findHealthInsuranceByCompanyCodeTest() throws Exception {
        when(healthInsuranceService.findHealthInsuranceByCompanyCode(healthInsuranceResponse.companyCode()))
                .thenReturn(healthInsuranceResponse);

        mvc.perform(get(BASE_URL + "/company-code/{companyCode}", healthInsuranceResponse.companyCode())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.companyCode").value(healthInsuranceResponse.companyCode()));

        verify(healthInsuranceService).findHealthInsuranceByCompanyCode(anyLong());
        verifyNoMoreInteractions(healthInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findHealthInsuranceByPhoneNumberTest() throws Exception {
        when(healthInsuranceService.findHealthInsuranceByPhoneNumber(healthInsuranceResponse.phoneNumber()))
                .thenReturn(healthInsuranceResponse);

        mvc.perform(get(BASE_URL + "/phone/{phoneNumber}", healthInsuranceResponse.phoneNumber())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.phoneNumber").value(healthInsuranceResponse.phoneNumber()));

        verify(healthInsuranceService).findHealthInsuranceByPhoneNumber(anyString());
        verifyNoMoreInteractions(healthInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findHealthInsuranceByEmailTest() throws Exception {
        when(healthInsuranceService.findHealthInsuranceByEmail(healthInsuranceResponse.email()))
                .thenReturn(healthInsuranceResponse);

        mvc.perform(get(BASE_URL + "/email/{email}", healthInsuranceResponse.email())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(healthInsuranceResponse.email()));

        verify(healthInsuranceService).findHealthInsuranceByEmail(anyString());
        verifyNoMoreInteractions(healthInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void searchByNameTest() throws Exception {
        when(healthInsuranceService.searchByName("Osde")).thenReturn(List.of(healthInsuranceResponse));

        mvc.perform(get(BASE_URL + "/search")
                        .param("name", "Osde")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].companyName").value(healthInsuranceResponse.companyName()));

        verify(healthInsuranceService).searchByName(anyString());
        verifyNoMoreInteractions(healthInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findCoveragePlansTest() throws Exception {
        CoveragePlanResponseDto coveragePlan = new CoveragePlanResponseDto(
                1L, 1L, "Osde", "Plan 310", "Basic plan", new BigDecimal("75.00"), true
        );

        when(coveragePlanService.findByHealthInsuranceIdAndIsActiveTrue(1L))
                .thenReturn(List.of(coveragePlan));

        mvc.perform(get(BASE_URL + "/{insuranceId}/coverage-plans", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].healthInsuranceId").value(1L))
                .andExpect(jsonPath("$[0].name").value("Plan 310"));

        verify(coveragePlanService).findByHealthInsuranceIdAndIsActiveTrue(anyLong());
        verifyNoMoreInteractions(coveragePlanService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientsByInsuranceIdTest() throws Exception {
        PatientInsuranceResponseDto patientInsurance = new PatientInsuranceResponseDto(
                1L, "12345678", "CRED-001-5678", 1L, "Osde", 1L, "Plan 310",
                LocalDate.of(2024, 1, 1), true
        );

        when(patientInsuranceService.findPatientInsuranceByHealthInsurance(1L))
                .thenReturn(List.of(patientInsurance));

        mvc.perform(get(BASE_URL + "/{insuranceId}/patients", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].healthInsuranceId").value(1L))
                .andExpect(jsonPath("$[0].patientDni").value("12345678"));

        verify(patientInsuranceService).findPatientInsuranceByHealthInsurance(anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void countActivePatientsTest() throws Exception {
        when(patientInsuranceService.countActivePatientsByInsuranceId(1L)).thenReturn(15);

        mvc.perform(get(BASE_URL + "/{insuranceId}/active-patients-count", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(15));

        verify(patientInsuranceService).countActivePatientsByInsuranceId(anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void countActivePlansTest() throws Exception {
        when(coveragePlanService.countActivePlanByHealthInsurance(1L)).thenReturn(5);

        mvc.perform(get(BASE_URL + "/{insuranceId}/active-plans-count", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(5));

        verify(coveragePlanService).countActivePlanByHealthInsurance(anyLong());
        verifyNoMoreInteractions(coveragePlanService);
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createHealthInsuranceTest() throws Exception {
        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto(
                "Osde Health", 12345L, "+5491112345678", "contact@osde.com"
        );

        HealthInsuranceResponseDto createResponse = new HealthInsuranceResponseDto(
                1L, "Osde Health", 12345L, "+5491112345678", "contact@osde.com", true
        );

        when(healthInsuranceService.createHealthInsurance(any(HealthInsuranceCreateDto.class)))
                .thenReturn(createResponse);

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.companyName").value("Osde Health"));

        verify(healthInsuranceService).createHealthInsurance(any(HealthInsuranceCreateDto.class));
        verifyNoMoreInteractions(healthInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createHealthInsurance_withDuplicateCompanyCodeTest() throws Exception {
        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto(
                "Osde Health", 12345L, "+5491112345678", "contact@osde.com"
        );

        when(healthInsuranceService.createHealthInsurance(any(HealthInsuranceCreateDto.class)))
                .thenThrow(new DuplicateResourceException("Health Insurance already exists with company code: 12345"));

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Health Insurance already exists with company code: 12345"));

        verify(healthInsuranceService).createHealthInsurance(any(HealthInsuranceCreateDto.class));
        verifyNoMoreInteractions(healthInsuranceService);
    }

    /* =================== PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateHealthInsuranceByIdTest() throws Exception {
        HealthInsuranceUpdateDto updateDto = new HealthInsuranceUpdateDto(
                "Osde Updated", 12345L, "+5491112345678", "newemail@osde.com"
        );

        HealthInsuranceResponseDto updatedResponse = new HealthInsuranceResponseDto(
                1L, "Osde Updated", 12345L, "+5491112345678", "newemail@osde.com", true
        );

        when(healthInsuranceService.updateHealthInsuranceById(eq(1L), any(HealthInsuranceUpdateDto.class)))
                .thenReturn(updatedResponse);

        mvc.perform(patch(BASE_URL + "/{insuranceId}", 1L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.companyName").value("Osde Updated"))
                .andExpect(jsonPath("$.email").value("newemail@osde.com"));

        verify(healthInsuranceService).updateHealthInsuranceById(eq(1L), any(HealthInsuranceUpdateDto.class));
        verifyNoMoreInteractions(healthInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeleteHealthInsuranceByIdTest() throws Exception {
        mvc.perform(patch(BASE_URL + "/{insuranceId}/deactivate", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(healthInsuranceService).softDeleteHealthInsuranceById(anyLong());
        verifyNoMoreInteractions(healthInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void activateHealthInsuranceByIdTest() throws Exception {
        mvc.perform(patch(BASE_URL + "/{insuranceId}/activate", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(healthInsuranceService).activateHealthInsuranceById(anyLong());
        verifyNoMoreInteractions(healthInsuranceService);
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteHealthInsuranceByIdTest() throws Exception {
        mvc.perform(delete(BASE_URL + "/{insuranceId}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(healthInsuranceService).deleteHealthInsuranceById(anyLong());
        verifyNoMoreInteractions(healthInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteInexistentHealthInsuranceByIdTest() throws Exception {
        doThrow(new ResourceNotFound("Health Insurance not found with id: 1"))
                .when(healthInsuranceService).deleteHealthInsuranceById(anyLong());

        mvc.perform(delete(BASE_URL + "/{insuranceId}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Health Insurance not found with id: 1"));

        verify(healthInsuranceService).deleteHealthInsuranceById(anyLong());
        verifyNoMoreInteractions(healthInsuranceService);
    }

    /* =================== AUTHORIZATION TESTS =================== */

    @Test
    @WithMockUser(roles = "PATIENT")
    void findHealthInsuranceById_asPatient_shouldBeForbidden() throws Exception {
        mvc.perform(get(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void createHealthInsurance_unauthenticated_shouldBeForbidden() throws Exception {
        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto(
                "Osde Health", 12345L, "+5491112345678", "contact@osde.com"
        );

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());
    }
}
