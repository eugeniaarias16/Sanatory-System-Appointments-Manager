package com.sanatoryApp.HealthInsuranceService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.CoveragePlanUpdateDto;
import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.repository.ICoveragePlanRepository;
import com.sanatoryApp.HealthInsuranceService.repository.IHealthInsuranceRepository;
import com.sanatoryApp.HealthInsuranceService.repository.IPatientInsuranceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CoveragePlanControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ICoveragePlanRepository coveragePlanRepository;

    @Autowired
    private IHealthInsuranceRepository healthInsuranceRepository;

    @Autowired
    private IPatientInsuranceRepository patientInsuranceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/coveragePlan";

    @BeforeEach
    void cleanDB() {
        patientInsuranceRepository.deleteAll();
        coveragePlanRepository.deleteAll();
        healthInsuranceRepository.deleteAll();
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCoveragePlanTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();

        CoveragePlanCreateDto createDto = new CoveragePlanCreateDto(
                healthInsurance.getId(), "Plan 310", "Basic coverage plan", new BigDecimal("75.00")
        );

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Plan 310"))
                .andExpect(jsonPath("$.description").value("Basic coverage plan"))
                .andExpect(jsonPath("$.coverageValuePercentage").value(75.00))
                .andExpect(jsonPath("$.healthInsuranceId").value(healthInsurance.getId()))
                .andExpect(jsonPath("$.isActive").value(true));

        CoveragePlan inBD = coveragePlanRepository.findAll().stream()
                .filter(cp -> "Plan 310".equals(cp.getName()))
                .filter(cp -> healthInsurance.getId().equals(cp.getHealthInsurance().getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Coverage Plan not persisted in the database."));

        assertThat(inBD.getName()).isEqualTo("Plan 310");
        assertThat(inBD.getCoverageValuePercentage()).isEqualByComparingTo(new BigDecimal("75.00"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCoveragePlan_withDuplicateNameTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        createCoveragePlan(healthInsurance);

        CoveragePlanCreateDto createDto = new CoveragePlanCreateDto(
                healthInsurance.getId(), "Plan 310", "Another plan", new BigDecimal("80.00")
        );

        long bdBefore = coveragePlanRepository.count();

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Already exists a Coverage Plan with name Plan 310 and insurance id " + healthInsurance.getId()));

        assertThat(coveragePlanRepository.count()).isEqualTo(bdBefore);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCoveragePlan_withInexistentHealthInsuranceTest() throws Exception {
        CoveragePlanCreateDto createDto = new CoveragePlanCreateDto(
                999L, "Plan 310", "Basic coverage plan", new BigDecimal("75.00")
        );

        long bdBefore = coveragePlanRepository.count();

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Health Insurance not found with id: 999"));

        assertThat(coveragePlanRepository.count()).isEqualTo(bdBefore);
    }

    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findCoveragePlanByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);

        mvc.perform(get(BASE_URL + "/{id}", coveragePlan.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(coveragePlan.getId()))
                .andExpect(jsonPath("$.name").value("Plan 310"))
                .andExpect(jsonPath("$.healthInsuranceId").value(healthInsurance.getId()));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentCoveragePlanByIdTest() throws Exception {
        mvc.perform(get(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coverage Plan not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByHealthInsuranceIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);

        mvc.perform(get(BASE_URL + "/healthInsurance/{healthInsuranceId}", healthInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].healthInsuranceId").value(healthInsurance.getId()))
                .andExpect(jsonPath("$[0].name").value("Plan 310"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByHealthInsuranceIdAndIsActiveTrueTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);

        mvc.perform(get(BASE_URL + "/active/healthInsurance/{healthInsuranceId}", healthInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[0].name").value("Plan 310"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findCoveragePlanByNameTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        createCoveragePlan(healthInsurance);

        mvc.perform(get(BASE_URL + "/name/{name}", "Plan 310")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Plan 310"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void countActivePlansTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        createCoveragePlan(healthInsurance);

        mvc.perform(get(BASE_URL + "/count/activePlans")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void countActivePlanByHealthInsuranceTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        createCoveragePlan(healthInsurance);

        mvc.perform(get(BASE_URL + "/count/active/healthInsurance/{healthInsuranceId}", healthInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1));
    }

    /* =================== PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateCoveragePlanByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);

        CoveragePlanUpdateDto updateDto = new CoveragePlanUpdateDto(
                "Plan 310 Updated", "Updated description", new BigDecimal("85.00")
        );

        mvc.perform(patch(BASE_URL + "/update/{id}", coveragePlan.getId())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Plan 310 Updated"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.coverageValuePercentage").value(85.00));

        CoveragePlan updated = coveragePlanRepository.findById(coveragePlan.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Plan 310 Updated");
        assertThat(updated.getCoverageValuePercentage()).isEqualByComparingTo(new BigDecimal("85.00"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateInexistentCoveragePlanByIdTest() throws Exception {
        CoveragePlanUpdateDto updateDto = new CoveragePlanUpdateDto(
                "Plan 310", "Description", new BigDecimal("75.00")
        );

        mvc.perform(patch(BASE_URL + "/update/{id}", 999L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coverage Plan not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeleteCoveragePlanByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);

        assertThat(coveragePlan.isActive()).isTrue();

        mvc.perform(patch(BASE_URL + "/softDelete/{id}", coveragePlan.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Coverage Plan with id: " + coveragePlan.getId() + " successfully deactivated (soft delete)"));

        CoveragePlan deactivated = coveragePlanRepository.findById(coveragePlan.getId()).orElseThrow();
        assertThat(deactivated.isActive()).isFalse();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeleteInexistentCoveragePlanByIdTest() throws Exception {
        mvc.perform(patch(BASE_URL + "/softDelete/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coverage Plan not found with id: 999"));
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteCoveragePlanByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);

        Long id = coveragePlan.getId();
        assertThat(coveragePlanRepository.existsById(id)).isTrue();

        mvc.perform(delete(BASE_URL + "/{id}", id)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Coverage Plan with id: " + id + " successfully deleted"));

        assertThat(coveragePlanRepository.existsById(id)).isFalse();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteInexistentCoveragePlanByIdTest() throws Exception {
        mvc.perform(delete(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coverage Plan not found with id: 999"));
    }

    /* =================== AUTHORIZATION TESTS =================== */

    @Test
    @WithMockUser(roles = "PATIENT")
    void createCoveragePlan_asPatient_shouldBeForbidden() throws Exception {
        CoveragePlanCreateDto createDto = new CoveragePlanCreateDto(
                1L, "Plan 310", "Basic coverage plan", new BigDecimal("75.00")
        );

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createCoveragePlan_unauthenticated_shouldBeForbidden() throws Exception {
        CoveragePlanCreateDto createDto = new CoveragePlanCreateDto(
                1L, "Plan 310", "Basic coverage plan", new BigDecimal("75.00")
        );

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());
    }

    /* =================== HELPER METHODS =================== */

    private HealthInsurance createHealthInsurance() {
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setCompanyName("Osde Health");
        healthInsurance.setCompanyCode(12345L);
        healthInsurance.setPhoneNumber("+5491112345678");
        healthInsurance.setEmail("contact@osde.com");
        healthInsurance.setActive(true);
        return healthInsuranceRepository.save(healthInsurance);
    }

    private CoveragePlan createCoveragePlan(HealthInsurance healthInsurance) {
        CoveragePlan coveragePlan = new CoveragePlan();
        coveragePlan.setName("Plan 310");
        coveragePlan.setDescription("Basic coverage plan");
        coveragePlan.setCoverageValuePercentage(new BigDecimal("75.00"));
        coveragePlan.setHealthInsurance(healthInsurance);
        coveragePlan.setActive(true);
        return coveragePlanRepository.save(coveragePlan);
    }
}
