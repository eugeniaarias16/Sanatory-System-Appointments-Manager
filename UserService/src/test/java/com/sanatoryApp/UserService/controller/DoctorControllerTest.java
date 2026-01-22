package com.sanatoryApp.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.UserService.config.SecurityConfig;
import com.sanatoryApp.UserService.controllers.DoctorController;
import com.sanatoryApp.UserService.dto.Request.DoctorCreateDto;
import com.sanatoryApp.UserService.dto.Request.DoctorUpdateDto;
import com.sanatoryApp.UserService.dto.Response.DoctorResponseDto;
import com.sanatoryApp.UserService.exception.DuplicateResourceException;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.service.IDoctorService;
import com.sanatoryApp.shared_security.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
@Import(SecurityConfig.class)
public class DoctorControllerTest {

    @Autowired private MockMvc mvc;

    @Autowired private ObjectMapper objectMapper;

    @MockBean private IDoctorService doctorService;

    @MockBean private SecurityService securityService;

    private String asJsonString(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAllDoctorsTest() throws Exception {
        DoctorResponseDto d1 = new DoctorResponseDto(1L,"Ana" , "Lopez", "ana_lopez@gmail.com", "20111222", "+544321567");
        DoctorResponseDto d2 = new DoctorResponseDto(2L, "Bruno", "Diaz", "bruno_diaz@gmail.com","30999888", "+544321567");

        when(doctorService.findAll()).thenReturn(List.of(d1, d2));

        mvc.perform(get("/doctor").accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].dni").value("20111222"))
                .andExpect(jsonPath("$[1].firstName").value("Bruno"));

        verify(doctorService).findAll();
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findDoctorByIdTest() throws Exception {
       DoctorResponseDto d1 = new DoctorResponseDto(1L,"Ana" , "Lopez", "ana_lopez@gmail.com", "20111222", "+544321567");

        when(securityService.isSecretaryOrDoctor(1L)).thenReturn(true);
        when(doctorService.findDoctorById(1L)).thenReturn(d1);

        mvc.perform(get("/doctor/{doctorId}", 1L).accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Ana"));

        verify(doctorService).findDoctorById(1L);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentDoctorByIdTest() throws Exception {
        when(securityService.isSecretaryOrDoctor(1L)).thenReturn(true);
        when(doctorService.findDoctorById(1L)).thenThrow(new ResourceNotFound("Doctor not found with id: 1"));

        mvc.perform(get("/doctor/{doctorId}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found with id: 1"));

        verify(doctorService).findDoctorById(1L);
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findDoctorByFirstNameTest() throws Exception {
       DoctorResponseDto d1 = new DoctorResponseDto(1L,"Ana" , "Lopez", "ana_lopez@gmail.com", "20111222", "+544321567");

        when(doctorService.findDoctorByFirstName("Ana")).thenReturn(List.of(d1));

        mvc.perform(get("/doctor/firstName/{firstName}", "Ana").accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Ana"))
                .andExpect(jsonPath("$[0].id").value(1));

        verify(doctorService).findDoctorByFirstName("Ana");
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findDoctorByLastNameTest() throws Exception {
       DoctorResponseDto d1 = new DoctorResponseDto(1L,"Ana" , "Lopez", "ana_lopez@gmail.com", "20111222", "+544321567");

        when(doctorService.findDoctorByLastName("Lopez")).thenReturn(List.of(d1));

        mvc.perform(get("/doctor/lastName/{lastName}", "Lopez").accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Lopez"));

        verify(doctorService).findDoctorByLastName("Lopez");
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findDoctorByDniTest() throws Exception {
       DoctorResponseDto d1 = new DoctorResponseDto(1L,"Ana" , "Lopez", "ana_lopez@gmail.com", "20111222", "+544321567");

        when(doctorService.findDoctorByDni("20111222")).thenReturn(d1);

        mvc.perform(get("/doctor/dni/{dni}", "20111222").accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("20111222"))
                .andExpect(jsonPath("$.email").value("ana_lopez@gmail.com"));

        verify(doctorService).findDoctorByDni("20111222");
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentDoctorByDniTest() throws Exception {
        when(doctorService.findDoctorByDni("20111222")).thenThrow(new ResourceNotFound("Doctor not found with dni: 20111222"));

        mvc.perform(get("/doctor/dni/{dni}", "20111222").accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found with dni: 20111222"));

        verify(doctorService).findDoctorByDni("20111222");
        verifyNoMoreInteractions(doctorService);
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctorTest() throws Exception {
        DoctorCreateDto createDto = new DoctorCreateDto("Ana" , "Lopez", "ana_lopez@gmail.com", "20111222", "+544321567");
        DoctorResponseDto response = new DoctorResponseDto(1L, "Ana" , "Lopez", "ana_lopez@gmail.com", "20111222", "+544321567");

        when(doctorService.createDoctor(any(DoctorCreateDto.class))).thenReturn(response);

        mvc.perform(post("/doctor")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(createDto)))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Ana"));

        verify(doctorService).createDoctor(any(DoctorCreateDto.class));
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctor_withDuplicateEmailTest() throws Exception {
        DoctorCreateDto createDto = new DoctorCreateDto("Ana" , "Lopez", "ana_lopez@gmail.com", "20111222", "+544321567");


        when(doctorService.createDoctor(any(DoctorCreateDto.class)))
                .thenThrow(new DuplicateResourceException("Doctor already exists with email: ana_lopez@gmail.com"));

        mvc.perform(post("/doctor/create")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Doctor already exists with email: ana_lopez@gmail.com"));

        verify(doctorService).createDoctor(any(DoctorCreateDto.class));
        verifyNoMoreInteractions(doctorService);
    }

    /* =================== PUT/PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateDoctorByIdTest() throws Exception {
        DoctorUpdateDto updateDto = new DoctorUpdateDto("Ana", "Lopez",null,  null,null);
        DoctorResponseDto response = new DoctorResponseDto(1L, "Ana" , "Lopez", "ana_lopez@gmail.com", "20111222", "+544321567");


        when(securityService.isSecretaryOrDoctor(1L)).thenReturn(true);
        when(doctorService.updateDoctorById(eq(1L), any(DoctorUpdateDto.class))).thenReturn(response);

        mvc.perform(put("/doctor/update/{doctorId}", 1L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(updateDto)))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Ana"));

        verify(doctorService).updateDoctorById(eq(1L), any(DoctorUpdateDto.class));
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void updateDoctorById_forbiddenByPreAuthorizeTest() throws Exception {
        DoctorUpdateDto updateDto = new DoctorUpdateDto("Ana", "Lopez", "ana@gmail.com","20111222" , "+545762134");



        when(securityService.isSecretaryOrDoctor(1L)).thenReturn(false);

        mvc.perform(put("/doctor/update/{doctorId}", 1L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(asJsonString(updateDto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(doctorService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void disableDoctorByDniTest() throws Exception {
        mvc.perform(patch("/doctor/disable/{dni}", "20111222").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Doctor with dni 20111222 successfully disabled."));

        verify(doctorService).disableDoctorByDni("20111222");
        verifyNoMoreInteractions(doctorService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void enableDoctorByDniTest() throws Exception {
        mvc.perform(patch("/doctor/enable/{dni}", "20111222").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Doctor with dni 20111222 successfully enabled."));

        verify(doctorService).enableDoctorByDni("20111222");
        verifyNoMoreInteractions(doctorService);
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteDoctorByIdTest() throws Exception {
        mvc.perform(delete("/doctor/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                // tu controller tiene: "Doctor with id "+id+"successfully deleted."
                .andExpect(content().string("Doctor with id 1 successfully deleted."));

        verify(doctorService).deleteDoctorById(1L);
        verifyNoMoreInteractions(doctorService);
    }

    /* =================== AUTHORIZATION TESTS =================== */

    @Test
    @WithMockUser(roles = "PATIENT")
    void findAllDoctors_asPatient_shouldBeForbidden() throws Exception {
        mvc.perform(get("/doctor").accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(doctorService);
    }

    @Test
    void findAllDoctors_unauthenticated_shouldBeForbidden() throws Exception {
        mvc.perform(get("/doctor").accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(doctorService);
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void findDoctorById_asDoctor_shouldBeAllowedWhenSelf() throws Exception {
        DoctorResponseDto response = new DoctorResponseDto(5L, "Ana" , "Lopez", "ana_lopez@gmail.com", "20111222", "+544321567");


        when(securityService.isSecretaryOrDoctor(5L)).thenReturn(true);
        when(doctorService.findDoctorById(5L)).thenReturn(response);

        mvc.perform(get("/doctor/{doctorId}", 5L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));

        verify(doctorService).findDoctorById(5L);
        verifyNoMoreInteractions(doctorService);
    }
}

