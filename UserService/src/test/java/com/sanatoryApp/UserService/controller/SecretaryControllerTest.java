package com.sanatoryApp.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.UserService.config.SecurityConfig;
import com.sanatoryApp.UserService.controllers.SecretaryController;
import com.sanatoryApp.UserService.dto.Request.SecretaryCreateDto;
import com.sanatoryApp.UserService.dto.Request.SecretaryUpdateDto;
import com.sanatoryApp.UserService.dto.Response.SecretaryResponseDto;
import com.sanatoryApp.UserService.exception.DuplicateResourceException;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.service.ISecretaryService;
import com.sanatoryApp.shared_security.service.SecurityService;
import jakarta.ws.rs.ForbiddenException;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(SecretaryController.class)
@Import(SecurityConfig.class)
public class SecretaryControllerTest {


    @Autowired
    private MockMvc mvc;

    @MockBean
    private ISecretaryService secretaryService;

    @MockBean
    private SecurityService securityService;

    @Autowired
    private ObjectMapper objectMapper;

    //Helper Method
    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAllSecretariesTest() throws Exception {
        SecretaryResponseDto responseDto1 = new SecretaryResponseDto(1L, "34567897", "Antonia", "De la Vega", "antonia_delavega@gmail.com");
        SecretaryResponseDto responseDto2 = new SecretaryResponseDto(2L, "45876123", "Sol", "Lopez", "sol_lopez@gmail.com");

        when(secretaryService.findAll()).thenReturn(List.of(responseDto1, responseDto2));
        mvc.perform(get("/secretary")
                        .accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("antonia_delavega@gmail.com"))
                .andExpect(jsonPath("$[1].firstName").value("Sol"))
                .andExpect(jsonPath("$[1].dni").value("45876123"));
        verify(secretaryService).findAll();
        verifyNoMoreInteractions(secretaryService);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findSecretaryByIdTest() throws Exception {
        SecretaryResponseDto responseDto = new SecretaryResponseDto(1L, "34567897", "Antonia", "De la Vega", "antonia_delavega@gmail.com");

        when(secretaryService.findSecretaryById(1L)).thenReturn(responseDto);
        mvc.perform(get("/secretary/id/{id}", 1L)
                        .accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("antonia_delavega@gmail.com"));
        verify(secretaryService).findSecretaryById(anyLong());
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentSecretaryById() throws Exception {
        when(secretaryService.findSecretaryById(1L)).thenThrow(new ResourceNotFound("Secretary not found with id: 1"));
        mvc.perform(get("/secretary/id/{id}", 1L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Secretary not found with id: 1"));
        verify(secretaryService).findSecretaryById(anyLong());
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findSecretaryByDniTest() throws Exception {
        SecretaryResponseDto responseDto = new SecretaryResponseDto(1L, "34567897", "Antonia", "De la Vega", "antonia_delavega@gmail.com");
        when(secretaryService.findSecretaryByDni(anyString())).thenReturn(responseDto);
        mvc.perform(get("/secretary/dni/{dni}", "34567897")
                        .accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("antonia_delavega@gmail.com"));
        verify(secretaryService).findSecretaryByDni(anyString());
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentSecretaryByDni() throws Exception {

        when(secretaryService.findSecretaryByDni(anyString())).thenThrow(new ResourceNotFound("Secretary not found with dni: 34567897"));
        mvc.perform(get("/secretary/dni/{dni}", "34567897")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Secretary not found with dni: 34567897"));
        verify(secretaryService).findSecretaryByDni(anyString());
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findSecretaryByEmailTest() throws Exception {
        SecretaryResponseDto responseDto = new SecretaryResponseDto(1L, "34567897", "Antonia", "De la Vega", "antonia_delavega@gmail.com");
        when(secretaryService.findSecretaryByEmail(anyString())).thenReturn(responseDto);
        mvc.perform(get("/secretary/email/{email}", "antonia_delavega@gmail.com").accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Antonia"));
        verify(secretaryService).findSecretaryByEmail(anyString());
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentSecretaryByEmail() throws Exception {

        when(secretaryService.findSecretaryByEmail(anyString())).thenThrow(new ResourceNotFound("Secretary not found with email: antonia_delavega@gmail.com"));
        mvc.perform(get("/secretary/email/{email}", "antonia_delavega@gmail.com").accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Secretary not found with email: antonia_delavega@gmail.com"));
        verify(secretaryService).findSecretaryByEmail(anyString());
        verifyNoMoreInteractions(secretaryService);
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createSecretaryTest() throws Exception {

        SecretaryResponseDto responseDto = new SecretaryResponseDto(1L, "34567897", "Antonia", "De la Vega", "antonia_delavega@gmail.com");
        SecretaryCreateDto createDto = new SecretaryCreateDto("34567897", "Antonia", "De la Vega", "antonia_delavega@gmail.com");
        when(secretaryService.createSecretary(any(SecretaryCreateDto.class))).thenReturn(responseDto);
        when(securityService.isSecretaryOrPatient(anyLong())).thenReturn(true);
        mvc.perform(post("/secretary")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Antonia"));
        verify(secretaryService).createSecretary(any(SecretaryCreateDto.class));
        verifyNoMoreInteractions(secretaryService);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createSecretary_withDuplicateEmail() throws Exception {
        SecretaryCreateDto createDto = new SecretaryCreateDto("34567897", "Antonia", "De la Vega", "antonia_delavega@gmail.com");
        when(securityService.isSecretaryOrPatient(anyLong())).thenReturn(true);
        when(secretaryService.createSecretary(any(SecretaryCreateDto.class))).thenThrow(new DuplicateResourceException("Secretary already exists with email: antonia_delavega@gmail.com"));

        mvc.perform(post("/secretary")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Secretary already exists with email: antonia_delavega@gmail.com"));
        verify(secretaryService).createSecretary(any(SecretaryCreateDto.class));
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createSecretary_withDuplicateDni() throws Exception {
        SecretaryCreateDto createDto = new SecretaryCreateDto("34567897", "Antonia", "De la Vega", "antonia_delavega@gmail.com");
        when(securityService.isSecretaryOrPatient(anyLong())).thenReturn(true);
        when(secretaryService.createSecretary(any(SecretaryCreateDto.class))).thenThrow(new DuplicateResourceException("Secretary already exists with dni: 34567897"));

        mvc.perform(post("/secretary")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Secretary already exists with dni: 34567897"));
        verify(secretaryService).createSecretary(any(SecretaryCreateDto.class));
        verifyNoMoreInteractions(secretaryService);

    }

    /* =================== PUT/PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateSecretaryByIdTest() throws Exception {
        SecretaryUpdateDto updateDto = new SecretaryUpdateDto(null, "Antonia", null, null);
        SecretaryResponseDto responseDto = new SecretaryResponseDto(1L, "34567897", "Antonia", "De la Vega", "antonia_delavega@gmail.com");

        when(securityService.isSecretaryOrPatient(anyLong())).thenReturn(true);
        when(secretaryService.updateSecretaryById(1L, updateDto)).thenReturn(responseDto);
        mvc.perform(patch("/secretary/update/id/{id}", 1L).accept(APPLICATION_JSON).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Antonia"));
        verify(secretaryService).updateSecretaryById(anyLong(), any(SecretaryUpdateDto.class));
        verifyNoMoreInteractions(secretaryService);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateInexistentSecretaryById() throws Exception {
        SecretaryUpdateDto updateDto = new SecretaryUpdateDto(null, "Antonia", null, null);
        when(securityService.isSecretaryOrPatient(anyLong())).thenReturn(true);
        when(secretaryService.updateSecretaryById(1L, updateDto)).thenThrow(new ResourceNotFound("Secretary not found with id: 1"));
        mvc.perform(patch("/secretary/update/id/{id}", 1L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Secretary not found with id: 1"));

        verify(secretaryService).updateSecretaryById(anyLong(), any(SecretaryUpdateDto.class));
        verifyNoMoreInteractions(secretaryService);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateSecretaryById_withDuplicateEmail() throws Exception {
        SecretaryUpdateDto updateDto = new SecretaryUpdateDto(null, "Antonia", null, "antonia_m@gmail.com");
        when(securityService.isSecretaryOrPatient(anyLong())).thenReturn(true);
        when(secretaryService.updateSecretaryById(1L, updateDto)).thenThrow(new DuplicateResourceException("Secretary already exists with email: antonia_m@gmail.com"));
        mvc.perform(patch("/secretary/update/id/{id}", 1L).accept(APPLICATION_JSON).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Secretary already exists with email: antonia_m@gmail.com"));

        verify(secretaryService).updateSecretaryById(anyLong(), any(SecretaryUpdateDto.class));
        verifyNoMoreInteractions(secretaryService);

    }


    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateSecretaryByDniTest() throws Exception {

        SecretaryUpdateDto updateDto = new SecretaryUpdateDto(null, "Antonia", null, null);
        SecretaryResponseDto responseDto = new SecretaryResponseDto(1L, "34567897", "Antonia", "De la Vega", "antonia_delavega@gmail.com");
        when(securityService.isSecretaryOrPatient(anyLong())).thenReturn(true);
        when(secretaryService.updateSecretaryByDni(anyString(), any(SecretaryUpdateDto.class))).thenReturn(responseDto);
        mvc.perform(patch("/secretary/update/dni/{dni}", "34567897")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Antonia"));
        verify(secretaryService).updateSecretaryByDni(anyString(), any(SecretaryUpdateDto.class));
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateInexistentSecretaryByDni() throws Exception {

        SecretaryUpdateDto updateDto = new SecretaryUpdateDto(null, "Antonia", null, null);

        when(securityService.isSecretaryOrPatient(anyLong())).thenReturn(true);
        when(secretaryService.updateSecretaryByDni(anyString(), any(SecretaryUpdateDto.class))).thenThrow(new ResourceNotFound("Secretary not found with dni: 34567897"));
        mvc.perform(patch("/secretary/update/dni/{dni}", "34567897")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Secretary not found with dni: 34567897"));
        verify(secretaryService).updateSecretaryByDni(anyString(), any(SecretaryUpdateDto.class));
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateSecretaryByDni_withDuplicateEmail() throws Exception {
        SecretaryUpdateDto updateDto = new SecretaryUpdateDto(null, "Antonia", null, "antonia_m@gmail.com");

        when(securityService.isSecretaryOrPatient(anyLong())).thenReturn(true);
        when(secretaryService.updateSecretaryByDni(anyString(), any(SecretaryUpdateDto.class))).thenThrow(new DuplicateResourceException("Secretary already exists with email: antonia_m@gmail.com"));
        mvc.perform(patch("/secretary/update/dni/{dni}", "34567897")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Secretary already exists with email: antonia_m@gmail.com"));
        verify(secretaryService).updateSecretaryByDni(anyString(), any(SecretaryUpdateDto.class));
        verifyNoMoreInteractions(secretaryService);
    }



    @Test
    @WithMockUser(roles = "SECRETARY")
    void disableSecretaryByDniTest() throws Exception {
      mvc.perform(patch("/secretary/disable/{dni}","45678987")
                      .accept(APPLICATION_JSON)
                      .contentType(APPLICATION_JSON))
              .andExpect(status().isOk())
              .andExpect(content().string("Secretary with dni 45678987 successfully disabled."));
      verify(secretaryService).disableSecretaryByDni(anyString());
      verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void disableInexistentSecretaryByDni() throws Exception {
        doThrow(new ResourceNotFound("Secretary not found with dni: 45678987")).when(secretaryService).disableSecretaryByDni(anyString());
        mvc.perform(patch("/secretary/disable/{dni}","45678987")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Secretary not found with dni: 45678987"));

        verify(secretaryService).disableSecretaryByDni(anyString());
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void enableSecretaryByDniTest() throws Exception {

        mvc.perform(patch("/secretary/enable/{dni}","45678987")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Secretary with dni 45678987 successfully enabled."));
        verify(secretaryService).enableSecretaryByDni(anyString());
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void enableInexistentSecretaryByDni() throws Exception {
        doThrow(new ResourceNotFound("Secretary with dni 45678987 successfully enabled.")).when(secretaryService).enableSecretaryByDni(anyString());

        mvc.perform(patch("/secretary/enable/{dni}","45678987")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Secretary with dni 45678987 successfully enabled."));
        verify(secretaryService).enableSecretaryByDni(anyString());
        verifyNoMoreInteractions(secretaryService);
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteSecretaryByIdTest() throws Exception {
        mvc.perform(delete("/secretary/delete/id/{id}",1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Secretary with id 1 successfully deleted."));
        verify(secretaryService).deleteSecretaryById(anyLong());
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteInexistentSecretaryById() throws Exception {
       doThrow(new ResourceNotFound("Secretary not found with id: 1")).when(secretaryService).deleteSecretaryById(anyLong());
        mvc.perform(delete("/secretary/delete/id/{id}",1L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Secretary not found with id: 1"));
        verify(secretaryService).deleteSecretaryById(anyLong());
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteSecretaryByDniTest() throws Exception {

        mvc.perform(delete("/secretary/delete/dni/{dni}","37543123").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Secretary with dni 37543123 successfully deleted."));
        verify(secretaryService).deleteSecretaryByDni(anyString());
        verifyNoMoreInteractions(secretaryService);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteInexistentSecretaryByDni() throws Exception {
        doThrow(new ResourceNotFound("Secretary not found with dni: 37543123")).when(secretaryService).deleteSecretaryByDni(anyString());
        mvc.perform(delete("/secretary/delete/dni/{dni}","37543123").accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Secretary not found with dni: 37543123"));
        verify(secretaryService).deleteSecretaryByDni(anyString());
        verifyNoMoreInteractions(secretaryService);
    }

    /* =================== AUTHORIZATION TESTS =================== */

    @Test
    @WithMockUser(roles = "PATIENT")
    void findSecretaryById_asPatient_shouldBeForbidden() throws Exception {
        mvc.perform(get("/secretary/id/{id}",1L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(secretaryService);
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void createSecretary_asDoctor_shouldBeForbidden() throws Exception {
        SecretaryCreateDto createDto = new SecretaryCreateDto("34567897", "Antonia", "De la Vega", "antonia_delavega@gmail.com");

        mvc.perform(post("/secretary")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());
        verifyNoMoreInteractions(secretaryService);
    }

    @Test
    void findAllSecretaries_unauthenticated_shouldBeUnauthorized() throws Exception {
        mvc.perform(get("/secretary")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(secretaryService);
    }
}


