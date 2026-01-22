package com.sanatoryApp.HealthInsuranceService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.HealthInsuranceService.dto.Request.PatientInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Request.externalService.PatientDto;
import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.repository.ICoveragePlanRepository;
import com.sanatoryApp.HealthInsuranceService.repository.IHealthInsuranceRepository;
import com.sanatoryApp.HealthInsuranceService.repository.IPatientInsuranceRepository;
import com.sanatoryApp.HealthInsuranceService.repository.UserServiceApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PatientInsuranceControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private IPatientInsuranceRepository patientInsuranceRepository;

    @Autowired
    private IHealthInsuranceRepository healthInsuranceRepository;

    @Autowired
    private ICoveragePlanRepository coveragePlanRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserServiceApi userServiceApi;

    private static final String BASE_URL = "/patientInsurance";

    @BeforeEach
    void cleanDB() {
        patientInsuranceRepository.deleteAll();
        coveragePlanRepository.deleteAll();
        healthInsuranceRepository.deleteAll();
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientInsuranceTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("12345678", healthInsurance.getId(), coveragePlan.getId());

        String expectedCredentialNumber = "CRED-0" + healthInsurance.getId() + "-5678";

        PatientDto patientDto = new PatientDto(1L,"12345678", "John", "Doe", "john@example.com","543234657890");
        when(userServiceApi.getPatientByDni("12345678")).thenReturn(patientDto);
        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.patientDni").value("12345678"))
                .andExpect(jsonPath("$.credentialNumber").value(expectedCredentialNumber))
                .andExpect(jsonPath("$.healthInsuranceId").value(healthInsurance.getId()));

        PatientInsurance inBD = patientInsuranceRepository.findAll().stream()
                .filter(p -> "12345678".equals(p.getPatientDni()))
                .filter(p -> expectedCredentialNumber.equals(p.getCredentialNumber()))
                .filter(p -> coveragePlan.getId().equals(p.getCoveragePlan().getId()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Patient Insurance not persisted in the database."));

        assertThat(inBD.getPatientDni()).isEqualTo("12345678");
        assertThat(inBD.getCredentialNumber()).isEqualTo(expectedCredentialNumber);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientInsurance_withDuplicateCredentialNumberTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        createPatientInsurance(healthInsurance, coveragePlan);
        String expectedCredentialNumber = "CRED-0" + healthInsurance.getId() + "-5678";

        PatientDto patientDto = new PatientDto(1L, "12345678", "John", "Doe", "john@example.com", "543234657890");
        when(userServiceApi.getPatientByDni("12345678")).thenReturn(patientDto);

        assertThat(patientInsuranceRepository.existsByCredentialNumber(expectedCredentialNumber)).isTrue();
        long bdBefore = patientInsuranceRepository.count();

        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("12345678", healthInsurance.getId(), coveragePlan.getId());
        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Patient Insurance already exists with Credential Number: " + expectedCredentialNumber));

        assertThat(patientInsuranceRepository.count()).isEqualTo(bdBefore);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientInsurance_withInexistentPatientTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("12345678", healthInsurance.getId(), coveragePlan.getId());

        when(userServiceApi.getPatientByDni(createDto.patientDni()))
                .thenThrow(new ResourceNotFound("Patient not found with dni: " + createDto.patientDni()));

        long bdBefore = patientInsuranceRepository.count();

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found with dni: " + createDto.patientDni()));

        assertThat(patientInsuranceRepository.count()).isEqualTo(bdBefore);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientInsurance_withInexistentHealthInsuranceTest() throws Exception {
        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("12345678", 999L, 1L);

        PatientDto patientDto = new PatientDto(1L, "12345678", "John", "Doe", "john@example.com", "543234657890");
        when(userServiceApi.getPatientByDni("12345678")).thenReturn(patientDto);

        long bdBefore = patientInsuranceRepository.count();

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Health Insurance not found with id: 999"));

        assertThat(patientInsuranceRepository.count()).isEqualTo(bdBefore);
    }



    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientInsurance_withInexistentCoveragePlanTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("12345678", healthInsurance.getId(), 999L);

        PatientDto patientDto = new PatientDto(1L, "12345678", "John", "Doe", "john@example.com", "543234657890");
        when(userServiceApi.getPatientByDni("12345678")).thenReturn(patientDto);

        long bdBefore = patientInsuranceRepository.count();

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coverage Plan not found with id: 999"));

        assertThat(patientInsuranceRepository.count()).isEqualTo(bdBefore);
    }

    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientInsuranceByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        PatientInsurance patientInsurance = createPatientInsurance(healthInsurance, coveragePlan);

        mvc.perform(get(BASE_URL + "/id/{id}", patientInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(patientInsurance.getId()))
                .andExpect(jsonPath("$.patientDni").value("12345678"))
                .andExpect(jsonPath("$.credentialNumber").value(patientInsurance.getCredentialNumber()));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentPatientInsuranceByIdTest() throws Exception {
        mvc.perform(get(BASE_URL + "/id/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient Insurance not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientInsuranceByPatientDniTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        PatientInsurance patientInsurance = createPatientInsurance(healthInsurance, coveragePlan);

        mvc.perform(get(BASE_URL + "/patientDni/{dni}", "12345678")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].patientDni").value("12345678"))
                .andExpect(jsonPath("$[0].credentialNumber").value(patientInsurance.getCredentialNumber()));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientInsuranceByCredentialNumberTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        PatientInsurance patientInsurance = createPatientInsurance(healthInsurance, coveragePlan);

        mvc.perform(get(BASE_URL + "/credentialNumber/{credentialNumber}", patientInsurance.getCredentialNumber())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.credentialNumber").value(patientInsurance.getCredentialNumber()))
                .andExpect(jsonPath("$.patientDni").value("12345678"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentPatientInsuranceByCredentialNumberTest() throws Exception {
        mvc.perform(get(BASE_URL + "/credentialNumber/{credentialNumber}", "CRED-999-9999")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient Insurance not found with credential number: CRED-999-9999"));
    }

    /* =================== PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updatePatientInsuranceCoveragePlanByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan1 = createCoveragePlan(healthInsurance);
        PatientInsurance patientInsurance = createPatientInsurance(healthInsurance, coveragePlan1);

        CoveragePlan coveragePlan2 = new CoveragePlan();
        coveragePlan2.setName("Plan 410");
        coveragePlan2.setDescription("Premium coverage plan");
        coveragePlan2.setCoverageValuePercentage(new BigDecimal("90.00"));
        coveragePlan2.setHealthInsurance(healthInsurance);
        coveragePlan2.setActive(true);
        coveragePlan2 = coveragePlanRepository.save(coveragePlan2);

        mvc.perform(patch(BASE_URL + "/update/{id}", patientInsurance.getId())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coveragePlan2.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.coveragePlanId").value(coveragePlan2.getId()))
                .andExpect(jsonPath("$.coveragePlanName").value("Plan 410"));

        PatientInsurance updated = patientInsuranceRepository.findById(patientInsurance.getId())
                .orElseThrow(() -> new ResourceNotFound("Patient Insurance not found with id: " + patientInsurance.getId()));
        assertThat(updated.getCoveragePlan().getId()).isEqualTo(coveragePlan2.getId());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updatePatientInsuranceCoveragePlanById_withInexistentIdTest() throws Exception {
        mvc.perform(patch(BASE_URL + "/update/{id}", 999L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(1L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient Insurance not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updatePatientInsuranceCoveragePlanById_withInexistentCoveragePlanTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        PatientInsurance patientInsurance = createPatientInsurance(healthInsurance, coveragePlan);

        mvc.perform(patch(BASE_URL + "/update/{id}", patientInsurance.getId())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(999L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coverage Plan not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeletePatientInsuranceByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        PatientInsurance patientInsurance = createPatientInsurance(healthInsurance, coveragePlan);

        assertThat(patientInsurance.getIsActive()).isTrue();

        mvc.perform(patch(BASE_URL + "/softDelete/{id}", patientInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient Insurance with id " + patientInsurance.getId() + " successfully deactivated (soft deleted)."));

        PatientInsurance deactivated = patientInsuranceRepository.findById(patientInsurance.getId()).orElseThrow();
        assertThat(deactivated.getIsActive()).isFalse();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeleteInexistentPatientInsuranceByIdTest() throws Exception {
        mvc.perform(patch(BASE_URL + "/softDelete/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient Insurance not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void activatePatientInsuranceByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        PatientInsurance patientInsurance = createPatientInsurance(healthInsurance, coveragePlan);

        patientInsurance.setIsActive(false);
        patientInsuranceRepository.save(patientInsurance);

        mvc.perform(patch(BASE_URL + "/activate/{id}", patientInsurance.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient Insurance with id " + patientInsurance.getId() + " successfully activated."));

        PatientInsurance activated = patientInsuranceRepository.findById(patientInsurance.getId()).orElseThrow();
        assertThat(activated.getIsActive()).isTrue();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void activateInexistentPatientInsuranceByIdTest() throws Exception {
        mvc.perform(patch(BASE_URL + "/activate/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient Insurance not found with id: 999"));
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deletePatientInsuranceByIdTest() throws Exception {
        HealthInsurance healthInsurance = createHealthInsurance();
        CoveragePlan coveragePlan = createCoveragePlan(healthInsurance);
        PatientInsurance patientInsurance = createPatientInsurance(healthInsurance, coveragePlan);

        Long id = patientInsurance.getId();
        assertThat(patientInsuranceRepository.existsById(id)).isTrue();

        mvc.perform(delete(BASE_URL + "/{id}", id)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient Insurance with id " + id + " successfully deleted."));

        assertThat(patientInsuranceRepository.existsById(id)).isFalse();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteInexistentPatientInsuranceByIdTest() throws Exception {
        mvc.perform(delete(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient Insurance not found with id: 999"));
    }

    /* =================== AUTHORIZATION TESTS =================== */

    @Test
    @WithMockUser(roles = "PATIENT")
    void createPatientInsurance_asPatient_shouldBeForbidden() throws Exception {
        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("12345678", 1L, 1L);

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createPatientInsurance_unauthenticated_shouldBeForbidden() throws Exception {
        PatientInsuranceCreateDto createDto = new PatientInsuranceCreateDto("12345678", 1L, 1L);

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
        patientInsurance.setCredentialNumber("CRED-0" + healthInsurance.getId() + "-" + patientInsurance.getPatientDni().substring(patientInsurance.getPatientDni().length() - 4));
        patientInsurance.setHealthInsurance(healthInsurance);
        patientInsurance.setCoveragePlan(coveragePlan);
        patientInsurance.setIsActive(true);
        return patientInsuranceRepository.save(patientInsurance);
    }
}
