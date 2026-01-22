package com.sanatoryApp.HealthInsuranceService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.HealthInsuranceService.config.SecurityConfig;
import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanUpdateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.CoveragePlanResponseDto;
import com.sanatoryApp.HealthInsuranceService.exception.DuplicateResourceException;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.service.ICoveragePlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CoveragePlanController.class)
@Import(SecurityConfig.class)
public class CoveragePlanControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICoveragePlanService coveragePlanService;

    // Helpers
    private final CoveragePlanResponseDto coveragePlanResponse = new CoveragePlanResponseDto(
            1L, 21L, "Osde", "Plan 310 G", "Basic coverage plan", new BigDecimal("75.00"), true
    );
    private static final String BASE_URL = "/coveragePlan";

    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findCoveragePlanByIdTest() throws Exception {
        when(coveragePlanService.findCoveragePlanById(1L)).thenReturn(coveragePlanResponse);

        mvc.perform(get(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(coveragePlanResponse.id()))
                .andExpect(jsonPath("$.name").value(coveragePlanResponse.name()));

        verify(coveragePlanService).findCoveragePlanById(anyLong());
        verifyNoMoreInteractions(coveragePlanService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentCoveragePlanByIdTest() throws Exception {
        when(coveragePlanService.findCoveragePlanById(1L))
                .thenThrow(new ResourceNotFound("Coverage Plan not found with id: 1"));

        mvc.perform(get(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coverage Plan not found with id: 1"));

        verify(coveragePlanService).findCoveragePlanById(anyLong());
        verifyNoMoreInteractions(coveragePlanService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByHealthInsuranceIdTest() throws Exception {
        when(coveragePlanService.findByHealthInsuranceId(coveragePlanResponse.healthInsuranceId()))
                .thenReturn(List.of(coveragePlanResponse));

        mvc.perform(get(BASE_URL + "/healthInsurance/{healthInsuranceId}", coveragePlanResponse.healthInsuranceId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].healthInsuranceId").value(coveragePlanResponse.healthInsuranceId()))
                .andExpect(jsonPath("$[0].healthInsuranceName").value(coveragePlanResponse.healthInsuranceName()));

        verify(coveragePlanService).findByHealthInsuranceId(anyLong());
        verifyNoMoreInteractions(coveragePlanService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByHealthInsuranceIdAndIsActiveTrueTest() throws Exception {
        when(coveragePlanService.findByHealthInsuranceIdAndIsActiveTrue(coveragePlanResponse.healthInsuranceId()))
                .thenReturn(List.of(coveragePlanResponse));

        mvc.perform(get(BASE_URL + "/active/healthInsurance/{healthInsuranceId}", coveragePlanResponse.healthInsuranceId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[0].name").value(coveragePlanResponse.name()));

        verify(coveragePlanService).findByHealthInsuranceIdAndIsActiveTrue(anyLong());
        verifyNoMoreInteractions(coveragePlanService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findCoveragePlanByNameTest() throws Exception {
        when(coveragePlanService.findCoveragePlanByName(coveragePlanResponse.name()))
                .thenReturn(coveragePlanResponse);

        mvc.perform(get(BASE_URL + "/name/{name}", coveragePlanResponse.name()).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(coveragePlanResponse.name()))
                .andExpect(jsonPath("$.description").value(coveragePlanResponse.description()));

        verify(coveragePlanService).findCoveragePlanByName(anyString());
        verifyNoMoreInteractions(coveragePlanService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void countActivePlansTest() throws Exception {
        when(coveragePlanService.countActivePlans()).thenReturn(10);

        mvc.perform(get(BASE_URL + "/count/activePlans").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(10));

        verify(coveragePlanService).countActivePlans();
        verifyNoMoreInteractions(coveragePlanService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void countActivePlanByHealthInsuranceTest() throws Exception {
        when(coveragePlanService.countActivePlanByHealthInsurance(coveragePlanResponse.healthInsuranceId()))
                .thenReturn(5);

        mvc.perform(get(BASE_URL + "/count/active/healthInsurance/{healthInsuranceId}",
                                coveragePlanResponse.healthInsuranceId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(5));

        verify(coveragePlanService).countActivePlanByHealthInsurance(anyLong());
        verifyNoMoreInteractions(coveragePlanService);
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCoveragePlanTest() throws Exception {
        CoveragePlanCreateDto createDto = new CoveragePlanCreateDto(
                21L, "Plan 310 G", "Basic coverage plan", new BigDecimal("75.00")
        );

        when(coveragePlanService.createCoveragePlan(any(CoveragePlanCreateDto.class)))
                .thenReturn(coveragePlanResponse);

        mvc.perform(post(BASE_URL )
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(coveragePlanResponse.name()))
                .andExpect(jsonPath("$.healthInsuranceId").value(coveragePlanResponse.healthInsuranceId()));

        verify(coveragePlanService).createCoveragePlan(any(CoveragePlanCreateDto.class));
        verifyNoMoreInteractions(coveragePlanService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCoveragePlan_withDuplicateNameTest() throws Exception {
        CoveragePlanCreateDto createDto = new CoveragePlanCreateDto(
                21L, "Plan 310 G", "Basic coverage plan", new BigDecimal("75.00")
        );

        when(coveragePlanService.createCoveragePlan(any(CoveragePlanCreateDto.class)))
                .thenThrow(new DuplicateResourceException("Coverage Plan already exists with name: Plan 310 G"));

        mvc.perform(post(BASE_URL )
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Coverage Plan already exists with name: Plan 310 G"));

        verify(coveragePlanService).createCoveragePlan(any(CoveragePlanCreateDto.class));
        verifyNoMoreInteractions(coveragePlanService);
    }

    /* =================== PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateCoveragePlanByIdTest() throws Exception {
        CoveragePlanUpdateDto updateDto = new CoveragePlanUpdateDto(
                "Plan 310 G Updated", "Updated description", new BigDecimal("80.00")
        );

        CoveragePlanResponseDto updatedResponse = new CoveragePlanResponseDto(
                1L, 21L, "Osde", "Plan 310 G Updated", "Updated description", new BigDecimal("80.00"), true
        );

        when(coveragePlanService.updateCoveragePlanById(eq(1L), any(CoveragePlanUpdateDto.class)))
                .thenReturn(updatedResponse);

        mvc.perform(patch(BASE_URL + "/update/{id}", 1L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Plan 310 G Updated"))
                .andExpect(jsonPath("$.coverageValuePercentage").value(80.00));

        verify(coveragePlanService).updateCoveragePlanById(eq(1L), any(CoveragePlanUpdateDto.class));
        verifyNoMoreInteractions(coveragePlanService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeleteCoveragePlanByIdTest() throws Exception {
        mvc.perform(patch(BASE_URL + "/softDelete/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Coverage Plan with id: 1 successfully deactivated (soft delete)"));

        verify(coveragePlanService).softDeleteCoveragePlanById(anyLong());
        verifyNoMoreInteractions(coveragePlanService);
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteCoveragePlanByIdTest() throws Exception {
        mvc.perform(delete(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Coverage Plan with id: 1 successfully deleted"));

        verify(coveragePlanService).deleteCoveragePlanById(anyLong());
        verifyNoMoreInteractions(coveragePlanService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteInexistentCoveragePlanByIdTest() throws Exception {
        doThrow(new ResourceNotFound("Coverage Plan not found with id: 1"))
                .when(coveragePlanService).deleteCoveragePlanById(anyLong());

        mvc.perform(delete(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coverage Plan not found with id: 1"));

        verify(coveragePlanService).deleteCoveragePlanById(anyLong());
        verifyNoMoreInteractions(coveragePlanService);
    }

    /* =================== AUTHORIZATION TESTS =================== */

    @Test
    @WithMockUser(roles = "PATIENT")
    void findCoveragePlanById_asPatient_shouldBeForbidden() throws Exception {
        mvc.perform(get(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCoveragePlan_unauthenticated_shouldBeForbidden() throws Exception {
        CoveragePlanCreateDto createDto = new CoveragePlanCreateDto(
                21L, "Plan 310 G", "Basic coverage plan", new BigDecimal("75.00")
        );

        mvc.perform(post(BASE_URL )
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());
    }
}
