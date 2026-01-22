package com.sanatoryApp.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.UserService.config.SecurityConfig;
import com.sanatoryApp.UserService.controllers.PatientController;
import com.sanatoryApp.UserService.dto.Request.PatientCreateDto;
import com.sanatoryApp.UserService.dto.Request.PatientUpdateDto;
import com.sanatoryApp.UserService.dto.Response.PatientResponseDto;
import com.sanatoryApp.UserService.exception.DuplicateResourceException;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.service.IPatientService;
import com.sanatoryApp.shared_security.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.*;

@WebMvcTest(PatientController.class)
@Import(SecurityConfig.class)
public class PatientControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IPatientService patientService;

    @MockBean
    private SecurityService securityService;


    @Autowired
    private ObjectMapper objectMapper;



    // =================== GET ENDPOINTS TESTS ===================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAllPatientsTest() throws Exception {

        //Arrange
        PatientResponseDto patient1 = new PatientResponseDto(1L, "Maria", "Rodriguez", "34567897", "maria_rodriguez@gmail.com", "+545679832");
        PatientResponseDto patient2 = new PatientResponseDto(2L, "Marcelo", "De la Vera", "20654123", "marcelo43@gmail.com", "+545679832");

        when(patientService.findAll()).thenReturn(List.of(patient1, patient2));

        //Act+Assert
        mvc.perform(get("/patient")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Maria"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].dni").value("20654123"));
        verify(patientService).findAll();
        verifyNoMoreInteractions(patientService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientByIdTest() throws Exception {
        //Arrange
        PatientResponseDto patient1 = new PatientResponseDto(1L, "Maria", "Rodriguez", "34567897", "maria_rodriguez@gmail.com", "+545679832");
        when(securityService.isSecretaryOrPatient(anyLong())).thenReturn(true);
        when(patientService.findPatientById(1L)).thenReturn(patient1);
        //Act
        mvc.perform(get("/patient/id/{patientId}", 1L)
                        .accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Maria"))
                .andExpect(jsonPath("$.dni").value("34567897"));

        verify(patientService).findPatientById(anyLong());
        verifyNoMoreInteractions(patientService);


    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientByIdNotFoundTest() throws Exception {


        when(securityService.isSecretaryOrPatient(anyLong())).thenReturn(true);
        when(patientService.findPatientById(99L)).thenThrow(new ResourceNotFound("Patient not found found with id: 99"));

        mvc.perform(get("/patient/id/{patientId}", 99).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found found with id: 99"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientByDniTest() throws Exception {

        //Arrange
        PatientResponseDto patient1 = new PatientResponseDto(1L, "Maria", "Rodriguez", "34567897", "maria_rodriguez@gmail.com", "+545679832");

        when(patientService.findPatientByDni("34567897")).thenReturn(patient1);

        mvc.perform(get("/patient/dni/{dni}", "34567897")
                        .accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Maria"))
                .andExpect(jsonPath("$.id").value(1L));
        verify(patientService).findPatientByDni(anyString());
        verifyNoMoreInteractions(patientService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientByDniNotFoundTest() throws Exception {


        when(patientService.findPatientByDni("34567897")).thenThrow(new ResourceNotFound("Patient not found with dni: 34567897"));

        mvc.perform(get("/patient/dni/{dni}", "34567897")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found with dni: 34567897"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientByEmailTest() throws Exception {

        //Arrange
        PatientResponseDto patient1 = new PatientResponseDto(1L, "Maria", "Rodriguez", "34567897", "maria_rodriguez@gmail.com", "+545679832");

        when(patientService.findPatientByEmail("maria_rodriguez@gmail.com")).thenReturn(patient1);
        mvc.perform(get("/patient/email/{email}", "maria_rodriguez@gmail.com")
                        .accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Maria"))
                .andExpect(jsonPath("$.id").value(1L));
        verify(patientService).findPatientByEmail(anyString());
        verifyNoMoreInteractions(patientService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findPatientByEmailNotFoundTest() throws Exception {

        when(patientService.findPatientByEmail("maria_rodriguez@gmail.com")).thenThrow(new ResourceNotFound("Patient not found with email: maria_rodriguez@gmail.com"));

        mvc.perform(get("/patient/email/{email}", "maria_rodriguez@gmail.com")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found with email: maria_rodriguez@gmail.com"));

    }

    // =================== POST ENDPOINTS TESTS ===================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientTest() throws Exception {

        PatientCreateDto createDto = new PatientCreateDto("Maria", "Rodriguez", "34567897", "maria_rodriguez@gmail.com", "+545679832");
        PatientResponseDto patient1 = new PatientResponseDto(1L, "Maria", "Rodriguez", "34567897", "maria_rodriguez@gmail.com", "+545679832");

        when(patientService.createPatient(createDto)).thenReturn(patient1);

        mvc.perform(post("/patient")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content( objectMapper.writeValueAsString(createDto)))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Maria"))
                .andExpect(jsonPath("$.id").value(1L));

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientWithInvalidDataTest() throws Exception {

        PatientCreateDto createDto = new PatientCreateDto("Maria", "Rodriguez", " ", "maria_rodriguez@gmail.com", "+545679832");



        mvc.perform(post("/patient")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content( objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
        verifyNoMoreInteractions(patientService);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientWithDuplicateDniTest() throws Exception {

        PatientCreateDto createDto = new PatientCreateDto("Maria", "Rodriguez", "34567897", "maria_rodriguez@gmail.com", "+545679832");

        when(patientService.createPatient(any(PatientCreateDto.class))).thenThrow(new DuplicateResourceException("Patient already exists with dni: 34567897"));
        mvc.perform(post("/patient")
                        .accept(APPLICATION_JSON)
                        .content( objectMapper.writeValueAsString(createDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Patient already exists with dni: 34567897"));

        verify(patientService).createPatient(any(PatientCreateDto.class));
        verifyNoMoreInteractions(patientService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientWithDuplicateEmailTest() throws Exception {

        PatientCreateDto createDto = new PatientCreateDto("Maria", "Rodriguez", "34567897", "maria_rodriguez@gmail.com", "+545679832");

        when(patientService.createPatient(createDto)).thenThrow(new DuplicateResourceException("Patient already exists with email: maria_rodriguez@gmail.com"));
        mvc.perform(post("/patient")
                        .accept(APPLICATION_JSON)
                        .content( objectMapper.writeValueAsString(createDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Patient already exists with email: maria_rodriguez@gmail.com"));

        verify(patientService).createPatient(any(PatientCreateDto.class));
        verifyNoMoreInteractions(patientService);
    }

    // =================== PUT/PATCH ENDPOINTS TESTS ===================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updatePatientByIdTest() throws Exception {

        //Arrange
        PatientUpdateDto updateDto = new PatientUpdateDto(null, "Melina", "Rodriguez", null, null);
        PatientResponseDto patient1 = new PatientResponseDto(1L, "Melina", "Rodriguez", "34567897", "maria_rodriguez@gmail.com", "+545679832");

        when(securityService.isSecretaryOrPatient(1L)).thenReturn(true); //pass authorisation filter
        when(patientService.updatePatientById(eq(1L), any(PatientUpdateDto.class))).thenReturn(patient1);
        mvc.perform(put("/patient/update/id/{patientId}", 1L)
                        .accept(APPLICATION_JSON)
                        .content( objectMapper.writeValueAsString(updateDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Melina"));
        verify(patientService).updatePatientById(anyLong(), any(PatientUpdateDto.class));
        verifyNoMoreInteractions(patientService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updatePatientByIdNotFoundTest() throws Exception {

        //Arrange
        PatientUpdateDto updateDto = new PatientUpdateDto(null, "Melina", "Rodriguez", null, null);

        when(securityService.isSecretaryOrPatient(1L)).thenReturn(true); //pass authorisation filter
        when(patientService.updatePatientById(eq(1L), any(PatientUpdateDto.class))).thenThrow(new ResourceNotFound("Patient not found found with id: 1"));

        mvc.perform(put("/patient/update/id/{patientId}", 1L)
                        .accept(APPLICATION_JSON)
                        .content( objectMapper.writeValueAsString(updateDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found found with id: 1"));
        verify(patientService).updatePatientById(anyLong(), any(PatientUpdateDto.class));
        verifyNoMoreInteractions(patientService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updatePatientByDniTest() throws Exception {
        //Arrange
        PatientUpdateDto updateDto = new PatientUpdateDto(null, "Melina", "Rodriguez", null, null);
        PatientResponseDto patient1 = new PatientResponseDto(1L, "Melina", "Rodriguez", "34567897", "maria_rodriguez@gmail.com", "+545679832");

        when(patientService.updatePatientByDni(anyString(), any(PatientUpdateDto.class))).thenReturn(patient1);
        mvc.perform(put("/patient/update/dni/{dni}", "34567897")
                        .accept(APPLICATION_JSON)
                        .content( objectMapper.writeValueAsString(updateDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Melina"));
        verify(patientService).updatePatientByDni(anyString(), any(PatientUpdateDto.class));
        verifyNoMoreInteractions(patientService);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updatePatientByDniNotFoundTest() throws Exception {

        PatientUpdateDto updateDto = new PatientUpdateDto(null, "Melina", "Rodriguez", null, null);
        when(patientService.updatePatientByDni(anyString(), any(PatientUpdateDto.class))).thenThrow(new ResourceNotFound("Patient not found with dni: 34567897"));
        mvc.perform(put("/patient/update/dni/{dni}", "34567897")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content( objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found with dni: 34567897"));
        verify(patientService).updatePatientByDni(anyString(), any(PatientUpdateDto.class));
        verifyNoMoreInteractions(patientService);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void disablePatientByDniTest() throws Exception {


        // Act & Assert
        mvc.perform(patch("/patient/disable/{dni}", "45678987")  // ✅ URL correcta con /
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())  // ✅ verificar status 200
                .andExpect(content().string("Patient with dni 45678987 successfully disabled."));  // ✅ verificar mensaje

        // Verify
        verify(patientService).disablePatientByDni("45678987");
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void disablePatientByDniNotFoundTest() throws Exception {

        doThrow(new ResourceNotFound("Patient not found with dni: 45678987")).when(patientService).disablePatientByDni("45678987");
        mvc.perform(patch("/patient/disable/{dni}", "45678987")  // ✅ URL correcta con /
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found with dni: 45678987"));
        verify(patientService).disablePatientByDni(anyString());
        verifyNoMoreInteractions(patientService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void enablePatientByDniTest() throws Exception {

        mvc.perform(patch("/patient/enable/{dni}", "45678987")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient with dni " + "45678987" + " successfully enabled."));
        verify(patientService).enablePatientByDni(anyString());
        verifyNoMoreInteractions(patientService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void enablePatientByDniNotFoundTest() throws Exception {
        doThrow(new ResourceNotFound("Patient with dni " + "45678987" + " successfully disabled.")).when(patientService).enablePatientByDni(anyString());
        mvc.perform(patch("/patient/enable/{dni}", "45678987")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient with dni " + "45678987" + " successfully disabled."));
        verify(patientService).enablePatientByDni(anyString());
        verifyNoMoreInteractions(patientService);
    }

    // =================== DELETE ENDPOINTS TESTS ===================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deletePatientByIdTest() throws Exception {
        mvc.perform(delete("/patient/delete/id/{id}", 1L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient with id 1 successfully deleted."));

        verify(patientService).deletePatientById(anyLong());
        verifyNoMoreInteractions(patientService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deletePatientByIdNotFoundTest() throws Exception {

        doThrow(new ResourceNotFound("Patient not found found with id: 1")).when(patientService).deletePatientById(anyLong());
        mvc.perform(delete("/patient/delete/id/{id}",1L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found found with id: 1"));
        verify(patientService).deletePatientById(anyLong());
        verifyNoMoreInteractions(patientService);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deletePatientByDniTest() throws Exception {
        mvc.perform(delete("/patient/delete/dni/{dni}", 23456789)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient with dni 23456789 successfully deleted."));

        verify(patientService).deletePatientByDni(anyString());
        verifyNoMoreInteractions(patientService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deletePatientByDniNotFoundTest() throws Exception {

        doThrow(new ResourceNotFound("Patient not found found with dni: 23456789")).when(patientService).deletePatientById(anyLong());
        mvc.perform(delete("/patient/delete/id/{id}",1L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Patient not found found with dni: 23456789"));
        verify(patientService).deletePatientById(anyLong());
        verifyNoMoreInteractions(patientService);
    }
}
