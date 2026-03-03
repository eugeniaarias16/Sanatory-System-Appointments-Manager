package com.sanatoryApp.CalendarService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.CalendarService.config.SecurityConfig;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarCreateResponseDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarResponseDto;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.service.IDoctorCalendarService;
import com.sanatoryApp.shared_security.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorCalendarController.class)
@Import(SecurityConfig.class)
public class DoctorCalendarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IDoctorCalendarService doctorCalendarService;

    @MockBean
    private SecurityService securityService;

    private static final String BASE_URL = "/doctorCalendar";

    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findDoctorCalendarByIdTest() throws Exception {
        DoctorCalendarResponseDto responseDto = new DoctorCalendarResponseDto(
                1L, 1L, "Dr. Martin Guzman - Clinica del Sur", true, "America/Argentina/Buenos_Aires"
        );

        when(doctorCalendarService.findDoctorCalendarById(1L)).thenReturn(responseDto);

        mvc.perform(get(BASE_URL + "/{id}", 1L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.name").value("Dr. Martin Guzman - Clinica del Sur"))
                .andExpect(jsonPath("$.isActive").value(true));

        verify(doctorCalendarService).findDoctorCalendarById(1L);
        verifyNoMoreInteractions(doctorCalendarService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentDoctorCalendarByIdTest() throws Exception {
        when(doctorCalendarService.findDoctorCalendarById(999L))
                .thenThrow(new ResourceNotFound("Doctor Calendar not found with id 999"));

        mvc.perform(get(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor Calendar not found with id 999"));

        verify(doctorCalendarService).findDoctorCalendarById(999L);
        verifyNoMoreInteractions(doctorCalendarService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndIsActiveTrueTest() throws Exception {
        DoctorCalendarResponseDto dc1 = new DoctorCalendarResponseDto(
                1L, 1L, "Calendar 1", true, "America/Argentina/Buenos_Aires"
        );
        DoctorCalendarResponseDto dc2 = new DoctorCalendarResponseDto(
                2L, 1L, "Calendar 2", true, "America/Argentina/Buenos_Aires"
        );

        when(securityService.isSecretaryOrDoctor(1L)).thenReturn(true);
        when(doctorCalendarService.findByDoctorIdAndIsActiveTrue(1L)).thenReturn(List.of(dc1, dc2));

        mvc.perform(get(BASE_URL + "/active/doctor/{doctorId}", 1L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Calendar 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Calendar 2"));

        verify(doctorCalendarService).findByDoctorIdAndIsActiveTrue(1L);
        verifyNoMoreInteractions(doctorCalendarService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdTest() throws Exception {
        DoctorCalendarResponseDto dc1 = new DoctorCalendarResponseDto(
                1L, 1L, "Calendar 1", true, "America/Argentina/Buenos_Aires"
        );
        DoctorCalendarResponseDto dc2 = new DoctorCalendarResponseDto(
                2L, 1L, "Calendar 2", false, "America/Argentina/Buenos_Aires"
        );

        when(securityService.isSecretaryOrDoctor(1L)).thenReturn(true);
        when(doctorCalendarService.findByDoctorId(1L)).thenReturn(List.of(dc1, dc2));

        mvc.perform(get(BASE_URL + "/doctor/{doctorId}", 1L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[1].isActive").value(false));

        verify(doctorCalendarService).findByDoctorId(1L);
        verifyNoMoreInteractions(doctorCalendarService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndNameTest() throws Exception {
        DoctorCalendarResponseDto responseDto = new DoctorCalendarResponseDto(
                1L, 1L, "Calendar Test", true, "America/Argentina/Buenos_Aires"
        );

        when(securityService.isSecretaryOrDoctor(1L)).thenReturn(true);
        when(doctorCalendarService.findByDoctorIdAndName(1L, "Calendar Test")).thenReturn(responseDto);

        mvc.perform(get(BASE_URL + "/active/search")
                        .param("doctorId", "1")
                        .param("name", "Calendar Test")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Calendar Test"))
                .andExpect(jsonPath("$.doctorId").value(1));

        verify(doctorCalendarService).findByDoctorIdAndName(1L, "Calendar Test");
        verifyNoMoreInteractions(doctorCalendarService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentByDoctorIdAndNameTest() throws Exception {
        when(securityService.isSecretaryOrDoctor(1L)).thenReturn(true);
        when(doctorCalendarService.findByDoctorIdAndName(1L, "Inexistent"))
                .thenThrow(new ResourceNotFound("Doctor Calendar not found with doctor id: 1 and name: inexistent"));

        mvc.perform(get(BASE_URL + "/active/search")
                        .param("doctorId", "1")
                        .param("name", "Inexistent")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor Calendar not found with doctor id: 1 and name: inexistent"));

        verify(doctorCalendarService).findByDoctorIdAndName(1L, "Inexistent");
        verifyNoMoreInteractions(doctorCalendarService);
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctorCalendarTest() throws Exception {
        DoctorCalendarCreateDto createDto = new DoctorCalendarCreateDto(
                1L, "Dr. Martin Guzman - Clinica del Sur", "America/Argentina/Buenos_Aires"
        );

        DoctorCalendarCreateResponseDto responseDto = new DoctorCalendarCreateResponseDto(
                1L, "Dr. Martin Guzman - Clinica del Sur", 1L,
                "Martin", "Guzman", true, "America/Argentina/Buenos_Aires"
        );

        when(doctorCalendarService.createDoctorCalendar(any(DoctorCalendarCreateDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Dr. Martin Guzman - Clinica del Sur"))
                .andExpect(jsonPath("$.doctorFirstName").value("Martin"))
                .andExpect(jsonPath("$.doctorLastName").value("Guzman"));

        verify(doctorCalendarService).createDoctorCalendar(any(DoctorCalendarCreateDto.class));
        verifyNoMoreInteractions(doctorCalendarService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctorCalendar_withDuplicateNameTest() throws Exception {
        DoctorCalendarCreateDto createDto = new DoctorCalendarCreateDto(
                1L, "Duplicate Calendar", "America/Argentina/Buenos_Aires"
        );

        when(doctorCalendarService.createDoctorCalendar(any(DoctorCalendarCreateDto.class)))
                .thenThrow(new IllegalArgumentException("Doctor with id 1 already has an active calendar with name: Duplicate Calendar"));

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Doctor with id 1 already has an active calendar with name: Duplicate Calendar"));

        verify(doctorCalendarService).createDoctorCalendar(any(DoctorCalendarCreateDto.class));
        verifyNoMoreInteractions(doctorCalendarService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctorCalendar_withInexistentDoctorTest() throws Exception {
        DoctorCalendarCreateDto createDto = new DoctorCalendarCreateDto(
                999L, "Test Calendar", "America/Argentina/Buenos_Aires"
        );

        when(doctorCalendarService.createDoctorCalendar(any(DoctorCalendarCreateDto.class)))
                .thenThrow(new ResourceNotFound("Doctor with id: 999 not found"));

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor with id: 999 not found"));

        verify(doctorCalendarService).createDoctorCalendar(any(DoctorCalendarCreateDto.class));
        verifyNoMoreInteractions(doctorCalendarService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctorCalendar_withInvalidTimeZoneTest() throws Exception {
        DoctorCalendarCreateDto createDto = new DoctorCalendarCreateDto(
                1L, "Test Calendar", "Invalid/TimeZone"
        );

        when(doctorCalendarService.createDoctorCalendar(any(DoctorCalendarCreateDto.class)))
                .thenThrow(new IllegalArgumentException("Invalid time zone: Invalid/TimeZone. Use zones like 'America/Argentina/Buenos_Aires', 'UTC', etc."));

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid time zone: Invalid/TimeZone. Use zones like 'America/Argentina/Buenos_Aires', 'UTC', etc."));

        verify(doctorCalendarService).createDoctorCalendar(any(DoctorCalendarCreateDto.class));
        verifyNoMoreInteractions(doctorCalendarService);
    }

    /* =================== PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateDoctorCalendarTest() throws Exception {
        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(
                null, "Updated Calendar Name", null
        );

        DoctorCalendarResponseDto responseDto = new DoctorCalendarResponseDto(
                1L, 1L, "Updated Calendar Name", true, "America/Argentina/Buenos_Aires"
        );

        when(doctorCalendarService.updateDoctorCalendar(anyLong(), any(DoctorCalendarUpdateDto.class)))
                .thenReturn(responseDto);

        mvc.perform(patch(BASE_URL + "/{id}", 1L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Calendar Name"));

        verify(doctorCalendarService).updateDoctorCalendar(anyLong(), any(DoctorCalendarUpdateDto.class));
        verifyNoMoreInteractions(doctorCalendarService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateDoctorCalendar_withInexistentIdTest() throws Exception {
        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(
                null, "Updated Name", null
        );

        when(doctorCalendarService.updateDoctorCalendar(anyLong(), any(DoctorCalendarUpdateDto.class)))
                .thenThrow(new ResourceNotFound("Doctor Calendar not found with id 999"));

        mvc.perform(patch(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor Calendar not found with id 999"));

        verify(doctorCalendarService).updateDoctorCalendar(anyLong(), any(DoctorCalendarUpdateDto.class));
        verifyNoMoreInteractions(doctorCalendarService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateDoctorCalendar_withDuplicateNameTest() throws Exception {
        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(
                null, "Duplicate Name", null
        );

        when(doctorCalendarService.updateDoctorCalendar(anyLong(), any(DoctorCalendarUpdateDto.class)))
                .thenThrow(new IllegalArgumentException("Doctor already has another active calendar with name: duplicate name"));

        mvc.perform(patch(BASE_URL + "/{id}", 1L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Doctor already has another active calendar with name: duplicate name"));

        verify(doctorCalendarService).updateDoctorCalendar(anyLong(), any(DoctorCalendarUpdateDto.class));
        verifyNoMoreInteractions(doctorCalendarService);
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteDoctorCalendarTest() throws Exception {
        doNothing().when(doctorCalendarService).deleteDoctorCalendar(1L);

        mvc.perform(delete(BASE_URL + "/{id}", 1L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(doctorCalendarService).deleteDoctorCalendar(1L);
        verifyNoMoreInteractions(doctorCalendarService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteInexistentDoctorCalendarTest() throws Exception {
        doThrow(new ResourceNotFound("Doctor Calendar not found with id 999"))
                .when(doctorCalendarService).deleteDoctorCalendar(999L);

        mvc.perform(delete(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor Calendar not found with id 999"));

        verify(doctorCalendarService).deleteDoctorCalendar(999L);
        verifyNoMoreInteractions(doctorCalendarService);
    }

    /* =================== AUTHORIZATION TESTS =================== */

    @Test
    @WithMockUser(roles = "PATIENT")
    void createDoctorCalendar_asPatient_shouldBeForbidden() throws Exception {
        DoctorCalendarCreateDto createDto = new DoctorCalendarCreateDto(
                1L, "Test Calendar", "America/Argentina/Buenos_Aires"
        );

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(doctorCalendarService);
    }

    @Test
    void createDoctorCalendar_unauthenticated_shouldBeForbidden() throws Exception {
        DoctorCalendarCreateDto createDto = new DoctorCalendarCreateDto(
                1L, "Test Calendar", "America/Argentina/Buenos_Aires"
        );

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(doctorCalendarService);
    }
}
