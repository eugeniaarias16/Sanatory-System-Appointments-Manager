package com.sanatoryApp.HealthInsuranceService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.HealthInsuranceUpdateDto;
import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
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
public class HealthInsuranceControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private IHealthInsuranceRepository healthInsuranceRepository;

    @Autowired
    private ICoveragePlanRepository coveragePlanRepository;

    @Autowired
    private IPatientInsuranceRepository patientInsuranceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/health-insurances";

    @BeforeEach
    void cleanDB() {
        patientInsuranceRepository.deleteAll();
        coveragePlanRepository.deleteAll();
        healthInsuranceRepository.deleteAll();
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createHealthInsuranceTest() throws Exception {
        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto(
                "Osde Health", 12345L, "+5491112345678", "contact@osde.com"
        );

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.companyName").value("Osde Health"))
                .andExpect(jsonPath("$.companyCode").value(12345))
                .andExpect(jsonPath("$.phoneNumber").value("+5491112345678"))
                .andExpect(jsonPath("$.email").value("contact@osde.com"))
                .andExpect(jsonPath("$.isActive").value(true));

        HealthInsurance inBD = healthInsuranceRepository.findAll().stream()
                .filter(h -> "Osde Health".equals(h.getCompanyName()))
                .filter(h -> 12345L == h.getCompanyCode())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Health Insurance not persisted in the database."));

        assertThat(inBD.getCompanyName()).isEqualTo("Osde Health");
        assertThat(inBD.getEmail()).isEqualTo("contact@osde.com");
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createHealthInsurance_withDuplicateCompanyNameTest() throws Exception {
        createHealthInsurance();

        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto(
                "Osde Health", 99999L, "+5491199999999", "other@osde.com"
        );

        long bdBefore = healthInsuranceRepository.count();

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Health Insurance with Company Name 'Osde Health' already exists"));

        assertThat(healthInsuranceRepository.count()).isEqualTo(bdBefore);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createHealthInsurance_withDuplicateCompanyCodeTest() throws Exception {
        createHealthInsurance();

        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto(
                "Swiss Medical", 12345L, "+5491199999999", "contact@swiss.com"
        );

        long bdBefore = healthInsuranceRepository.count();

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Health Insurance with Company Code '12345' already exists"));

        assertThat(healthInsuranceRepository.count()).isEqualTo(bdBefore);
    }

    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findHealthInsuranceByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();

        mvc.perform(get(BASE_URL + "/{id}", healthInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(healthInsurance.getId()))
                .andExpect(jsonPath("$.companyName").value("Osde Health"))
                .andExpect(jsonPath("$.companyCode").value(12345));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentHealthInsuranceByIdTest() throws Exception {
        mvc.perform(get(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Health Insurance not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findHealthInsuranceByCompanyNameTest() throws Exception {
        createHealthInsurance();

        mvc.perform(get(BASE_URL + "/company-name/{companyName}", "Osde Health")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.companyName").value("Osde Health"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findHealthInsuranceByCompanyCodeTest() throws Exception {
        createHealthInsurance();

        mvc.perform(get(BASE_URL + "/company-code/{companyCode}", 12345L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.companyCode").value(12345));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findHealthInsuranceByPhoneNumberTest() throws Exception {
        createHealthInsurance();

        mvc.perform(get(BASE_URL + "/phone/{phoneNumber}", "+5491112345678")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.phoneNumber").value("+5491112345678"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findHealthInsuranceByEmailTest() throws Exception {
        createHealthInsurance();

        mvc.perform(get(BASE_URL + "/email/{email}", "contact@osde.com")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("contact@osde.com"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void searchByNameTest() throws Exception {
        createHealthInsurance();

        mvc.perform(get(BASE_URL + "/search")
                        .param("name", "Osde")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].companyName").value("Osde Health"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findCoveragePlansTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        createCoveragePlan(healthInsurance);

        mvc.perform(get(BASE_URL + "/{insuranceId}/coverage-plans", healthInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].healthInsuranceId").value(healthInsurance.getId()));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientsByInsuranceIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        createPatientInsurance(healthInsurance, coveragePlan);

        mvc.perform(get(BASE_URL + "/{insuranceId}/patients", healthInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].healthInsuranceId").value(healthInsurance.getId()));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void countActivePatientsTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        createPatientInsurance(healthInsurance, coveragePlan);

        mvc.perform(get(BASE_URL + "/{insuranceId}/active-patients-count", healthInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void countActivePlansTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        createCoveragePlan(healthInsurance);

        mvc.perform(get(BASE_URL + "/{insuranceId}/active-plans-count", healthInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(1));
    }

    /* =================== PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateHealthInsuranceByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();

        HealthInsuranceUpdateDto updateDto = new HealthInsuranceUpdateDto(
                "Osde Health Updated", 54321L, "+5491187654321", "updated@osde.com"
        );

        mvc.perform(patch(BASE_URL + "/{id}", healthInsurance.getId())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.companyName").value("Osde Health Updated"))
                .andExpect(jsonPath("$.companyCode").value(54321))
                .andExpect(jsonPath("$.email").value("updated@osde.com"));

        HealthInsurance updated = healthInsuranceRepository.findById(healthInsurance.getId()).orElseThrow();
        assertThat(updated.getCompanyName()).isEqualTo("Osde Health Updated");
        assertThat(updated.getCompanyCode()).isEqualTo(54321L);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateInexistentHealthInsuranceByIdTest() throws Exception {
        HealthInsuranceUpdateDto updateDto = new HealthInsuranceUpdateDto(
                "Osde Health", 12345L, "+5491112345678", "contact@osde.com"
        );

        mvc.perform(patch(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Health Insurance not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeleteHealthInsuranceByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();

        assertThat(healthInsurance.isActive()).isTrue();

        mvc.perform(patch(BASE_URL + "/{id}/deactivate", healthInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        HealthInsurance deactivated = healthInsuranceRepository.findById(healthInsurance.getId()).orElseThrow();
        assertThat(deactivated.isActive()).isFalse();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeleteInexistentHealthInsuranceByIdTest() throws Exception {
        mvc.perform(patch(BASE_URL + "/{id}/deactivate", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Health Insurance not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void activateHealthInsuranceByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        healthInsurance.setActive(false);
        healthInsuranceRepository.save(healthInsurance);

        mvc.perform(patch(BASE_URL + "/{id}/activate", healthInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        HealthInsurance activated = healthInsuranceRepository.findById(healthInsurance.getId()).orElseThrow();
        assertThat(activated.isActive()).isTrue();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void activateInexistentHealthInsuranceByIdTest() throws Exception {
        mvc.perform(patch(BASE_URL + "/{id}/activate", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Health Insurance not found with id: 999"));
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteHealthInsuranceByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();

        Long id = healthInsurance.getId();
        assertThat(healthInsuranceRepository.existsById(id)).isTrue();

        mvc.perform(delete(BASE_URL + "/{id}", id)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertThat(healthInsuranceRepository.existsById(id)).isFalse();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteInexistentHealthInsuranceByIdTest() throws Exception {
        mvc.perform(delete(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Health Insurance not found with id: 999"));
    }

    /* =================== AUTHORIZATION TESTS =================== */

    @Test
    @WithMockUser(roles = "PATIENT")
    void createHealthInsurance_asPatient_shouldBeForbidden() throws Exception {
        HealthInsuranceCreateDto createDto = new HealthInsuranceCreateDto(
                "Osde Health", 12345L, "+5491112345678", "contact@osde.com"
        );

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
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

    private PatientInsurance createPatientInsurance(HealthInsurance healthInsurance, CoveragePlan coveragePlan) {
        PatientInsurance patientInsurance = new PatientInsurance();
        patientInsurance.setPatientDni("12345678");
        patientInsurance.setCredentialNumber("CRED-0" + healthInsurance.getId() + "-5678");
        patientInsurance.setHealthInsurance(healthInsurance);
        patientInsurance.setCoveragePlan(coveragePlan);
        patientInsurance.setIsActive(true);
        return patientInsuranceRepository.save(patientInsurance);
    }
}
