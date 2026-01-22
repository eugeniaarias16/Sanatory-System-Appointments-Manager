package com.sanatoryApp.HealthInsuranceService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.HealthInsuranceService.config.SecurityConfig;
import com.sanatoryApp.HealthInsuranceService.dto.Request.PatientInsuranceCreateDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceCreateResponseDto;
import com.sanatoryApp.HealthInsuranceService.dto.Response.PatientInsuranceResponseDto;
import com.sanatoryApp.HealthInsuranceService.entity.CoveragePlan;
import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import com.sanatoryApp.HealthInsuranceService.entity.PatientInsurance;
import com.sanatoryApp.HealthInsuranceService.exception.DuplicateResourceException;
import com.sanatoryApp.HealthInsuranceService.exception.ResourceNotFound;
import com.sanatoryApp.HealthInsuranceService.service.IPatientInsuranceService;
import com.sanatoryApp.shared_security.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(PatientInsuranceController.class)
@Import(SecurityConfig.class)
public class PatientInsuranceControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean private IPatientInsuranceService patientInsuranceService;


    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientInsuranceByIdTest() throws Exception {

       when(patientInsuranceService.findPatientInsuranceById(1L)).thenReturn(patientInsuranceResponse);
        mvc.perform(get(BASE_URL+"/id/{id}",1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.patientDni").value(patientInsuranceResponse.patientDni()))
                .andExpect(jsonPath("$.credentialNumber").value(patientInsuranceResponse.credentialNumber()));
        verify(patientInsuranceService).findPatientInsuranceById(anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentPatientInsuranceByIdTest() throws Exception {
        when(patientInsuranceService.findPatientInsuranceById(1L)).thenThrow(new ResourceNotFound("Patient Insurance not found with id: 1"));
        mvc.perform(get(BASE_URL+"/id/{id}",1L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient Insurance not found with id: 1"));


        verify(patientInsuranceService).findPatientInsuranceById(anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientInsuranceByPatientDniTest() throws Exception {
        when(patientInsuranceService.findPatientInsuranceByPatientDni(patientInsuranceResponse.patientDni())).thenReturn(List.of(patientInsuranceResponse));
        mvc.perform(get(BASE_URL+"/patientDni/{dni}",patientInsuranceResponse.patientDni()).accept(APPLICATION_JSON).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].credentialNumber").value(patientInsuranceResponse.credentialNumber()))
                .andExpect(jsonPath("$[0].coveragePlanName").value(patientInsuranceResponse.coveragePlanName()));
        verify(patientInsuranceService).findPatientInsuranceByPatientDni(anyString());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientInsuranceByCredentialNumberTest() throws Exception {
        when(patientInsuranceService.findPatientInsuranceByCredentialNumber(patientInsuranceResponse.credentialNumber())).thenReturn(patientInsuranceResponse);
        mvc.perform(get(BASE_URL+"/credentialNumber/{credentialNumber}",patientInsuranceResponse.credentialNumber()).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.patientDni").value(patientInsuranceResponse.patientDni()))
                .andExpect(jsonPath("$.coveragePlanName").value(patientInsuranceResponse.coveragePlanName()));
        verify(patientInsuranceService).findPatientInsuranceByCredentialNumber(anyString());
        verifyNoMoreInteractions(patientInsuranceService);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentPatientInsuranceByCredentialNumberTest() throws Exception {
        when(patientInsuranceService.findPatientInsuranceByCredentialNumber("CRED-999-999"))
                .thenThrow(new ResourceNotFound("Patient Insurance not found with credential number: CRED-999-999"));

        mvc.perform(get(BASE_URL + "/credentialNumber/{credentialNumber}", "CRED-999-999").accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient Insurance not found with credential number: CRED-999-999"));

        verify(patientInsuranceService).findPatientInsuranceByCredentialNumber(anyString());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientInsuranceByHealthInsuranceTest() throws Exception {
        when(patientInsuranceService.findPatientInsuranceByHealthInsurance(patientInsuranceResponse.healthInsuranceId()))
                .thenReturn(List.of(patientInsuranceResponse));

        mvc.perform(get(BASE_URL + "/healthInsurance/{healthInsuranceId}", patientInsuranceResponse.healthInsuranceId()).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].healthInsuranceId").value(patientInsuranceResponse.healthInsuranceId()))
                .andExpect(jsonPath("$[0].healthInsuranceName").value(patientInsuranceResponse.healthInsuranceName()));

        verify(patientInsuranceService).findPatientInsuranceByHealthInsurance(anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientInsuranceByCoveragePlanIdTest() throws Exception {
        when(patientInsuranceService.findPatientInsuranceByCoveragePlanId(patientInsuranceResponse.coveragePlanId()))
                .thenReturn(List.of(patientInsuranceResponse));

        mvc.perform(get(BASE_URL + "/coveragePlan/{coveragePlanId}", patientInsuranceResponse.coveragePlanId()).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].coveragePlanId").value(patientInsuranceResponse.coveragePlanId()))
                .andExpect(jsonPath("$[0].coveragePlanName").value(patientInsuranceResponse.coveragePlanName()));

        verify(patientInsuranceService).findPatientInsuranceByCoveragePlanId(anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientInsuranceByCreatedAtTest() throws Exception {
        when(patientInsuranceService.findPatientInsuranceByCreatedAt(patientInsuranceResponse.createdAt()))
                .thenReturn(List.of(patientInsuranceResponse));

        mvc.perform(get(BASE_URL + "/createdAt/{date}", patientInsuranceResponse.createdAt()).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].createdAt").value(patientInsuranceResponse.createdAt().toString()));

        verify(patientInsuranceService).findPatientInsuranceByCreatedAt(any(LocalDate.class));
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientInsuranceByCreatedAfterDateTest() throws Exception {
        LocalDate afterDate = LocalDate.of(2023, 12, 1);
        when(patientInsuranceService.findPatientInsuranceByCreatedAfterDate(afterDate))
                .thenReturn(List.of(patientInsuranceResponse));

        mvc.perform(get(BASE_URL + "/createdAfter/{date}", afterDate).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(patientInsuranceResponse.id()))
                .andExpect(jsonPath("$[0].createdAt").value(patientInsuranceResponse.createdAt().toString()));

        verify(patientInsuranceService).findPatientInsuranceByCreatedAfterDate(any(LocalDate.class));
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void countActivePatientsByInsuranceIdTest() throws Exception {
        when(patientInsuranceService.countActivePatientsByInsuranceId(patientInsuranceResponse.healthInsuranceId())).thenReturn(5);

        mvc.perform(get(BASE_URL + "/active/countPatients/{insuranceId}", patientInsuranceResponse.healthInsuranceId()).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").value(5));

        verify(patientInsuranceService).countActivePatientsByInsuranceId(anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientInsuranceTest() throws Exception {
        PatientInsuranceCreateDto createDto=new PatientInsuranceCreateDto("45167897",21L,3L);

        PatientInsuranceCreateResponseDto responseDto=new PatientInsuranceCreateResponseDto(1L,"45167897","Ana","Grey","ana_grey@gmail.com", patientInsuranceResponse.credentialNumber(), patientInsuranceResponse.healthInsuranceId(),patientInsuranceResponse.coveragePlanId(),patientInsuranceResponse.createdAt(),patientInsuranceResponse.isActive());
        when(patientInsuranceService.createPatientInsurance(createDto)).thenReturn(responseDto);
        mvc.perform(post(BASE_URL).accept(APPLICATION_JSON).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.credentialNumber").value("CRED-021-7897"))
                .andExpect(jsonPath("$.patientDni").value(patientInsuranceResponse.patientDni()));
        verify(patientInsuranceService).createPatientInsurance(any(PatientInsuranceCreateDto.class));
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientInsurance_withDuplicateCredentialNumberTest() throws Exception {
        PatientInsuranceCreateDto createDto=new PatientInsuranceCreateDto("45167897",21L,3L);
        when(patientInsuranceService.createPatientInsurance(any(PatientInsuranceCreateDto.class)))
                .thenThrow(new DuplicateResourceException("Patient Insurance already exists with Credential Number: CRED-021-7897"));

        mvc.perform(post(BASE_URL).accept(APPLICATION_JSON).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Patient Insurance already exists with Credential Number: CRED-021-7897"));

        verify(patientInsuranceService).createPatientInsurance(any(PatientInsuranceCreateDto.class));
        verifyNoMoreInteractions(patientInsuranceService);
    }


    /* =================== PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updatePatientInsuranceCoveragePlanByIdTest() throws Exception {
        PatientInsuranceResponseDto patientInsuranceUpdated=new PatientInsuranceResponseDto(1L,"45167897","CRED-021-7897",21L,"Osde",4L,"Plan 410 G", LocalDate.now(),true);

        when(patientInsuranceService.updatePatientInsuranceCoveragePlanById(1L,4L)).thenReturn(patientInsuranceUpdated);
        mvc.perform(patch(BASE_URL+"/update/{patientInsuranceId}",1L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(4L)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.coveragePlanId").value(4L))
                .andExpect(jsonPath("$.coveragePlanName").value("Plan 410 G"));
        verify(patientInsuranceService).updatePatientInsuranceCoveragePlanById(anyLong(),anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updatePatientInsuranceCoveragePlanById_withInexistentIdTest() throws Exception {
        when(patientInsuranceService.updatePatientInsuranceCoveragePlanById(1L,4L)).thenThrow(new ResourceNotFound("Coverage Plan not found with id: 4"));

        mvc.perform(patch(BASE_URL+"/update/{patientInsuranceId}",1L)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(4L)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Coverage Plan not found with id: 4"));

        verify(patientInsuranceService).updatePatientInsuranceCoveragePlanById(anyLong(),anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeletePatientInsuranceByIdTest() throws Exception {

        mvc.perform(patch(BASE_URL+"/softDelete/{patientInsuranceId}",1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient Insurance with id 1 successfully deactivated (soft deleted)."));
        verify(patientInsuranceService).softDeletePatientInsuranceById(anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void activatePatientInsuranceByIdTest() throws Exception {
        mvc.perform(patch(BASE_URL+"/activate/{patientInsuranceId}",1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient Insurance with id 1 successfully activated."));
        verify(patientInsuranceService).activatePatientInsuranceById(anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deletePatientInsuranceByIdTest() throws Exception {
        mvc.perform(delete(BASE_URL+"/{patientInsuranceId}",1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient Insurance with id 1 successfully deleted."));
        verify(patientInsuranceService).deletePatientInsuranceById(anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteInexistentPatientInsuranceByIdTest() throws Exception {
        doThrow(new ResourceNotFound("Patient Insurance not found with id: 1")).when(patientInsuranceService).deletePatientInsuranceById(anyLong());
        mvc.perform(delete(BASE_URL+"/{patientInsuranceId}",1L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient Insurance not found with id: 1"));

        verify(patientInsuranceService).deletePatientInsuranceById(anyLong());
        verifyNoMoreInteractions(patientInsuranceService);
    }

    /* =================== AUTHORIZATION TESTS =================== */

    @Test
    @WithMockUser(roles = "PATIENT")
    void findPatientInsuranceById_asPatient_shouldBeForbidden() throws Exception {
        mvc.perform(get(BASE_URL+"/id/{id}",1L).accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());


    }

    @Test
    void createPatientInsurance_unauthenticated_shouldBeForbidden() throws Exception {
        PatientInsuranceCreateDto createDto=new PatientInsuranceCreateDto("45167897",21L,3L);
        mvc.perform(post(BASE_URL).accept(APPLICATION_JSON).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());
    }



    //Helpers
    private final PatientInsuranceResponseDto patientInsuranceResponse=new PatientInsuranceResponseDto(1L,"45167897","CRED-021-7897",21L,"Osde",3L,"Plan 310 G", LocalDate.of(2024,01,01),true);
    private static String BASE_URL="/patientInsurance";

    private PatientInsurance createPatientInsurance(){
        HealthInsurance healthInsurance=new HealthInsurance();
        healthInsurance.setCompanyName("Osde");
        healthInsurance.setId(21L);

        CoveragePlan coveragePlan=new CoveragePlan();
        coveragePlan.setId(3L);
        coveragePlan.setHealthInsurance(healthInsurance);

        PatientInsurance patientInsurance=new PatientInsurance();
        patientInsurance.setId(1L);
        patientInsurance.setPatientDni("45167897");
        patientInsurance.setCredentialNumber("CRED-021-7897");
        patientInsurance.setCoveragePlan(coveragePlan);
        patientInsurance.setHealthInsurance(healthInsurance);
        patientInsurance.setCreatedAt(LocalDate.of(2024,01,01));
        patientInsurance.setIsActive(true);

        return patientInsurance;

    }

}
