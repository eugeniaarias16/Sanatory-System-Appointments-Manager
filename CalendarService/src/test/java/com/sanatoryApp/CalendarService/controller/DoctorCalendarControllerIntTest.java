package com.sanatoryApp.CalendarService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarUpdateDto;
import com.sanatoryApp.CalendarService.dto.Request.externalService.DoctorDto;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import com.sanatoryApp.CalendarService.repository.IAvailabilityPatternRepository;
import com.sanatoryApp.CalendarService.repository.ICalendarExceptionRepository;
import com.sanatoryApp.CalendarService.repository.IDoctorCalendarRepository;
import com.sanatoryApp.CalendarService.repository.UserServiceApi;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class DoctorCalendarControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private IDoctorCalendarRepository doctorCalendarRepository;

    @Autowired
    private IAvailabilityPatternRepository availabilityPatternRepository;

    @Autowired
    private ICalendarExceptionRepository calendarExceptionRepository;

    @MockBean
    private UserServiceApi userServiceApi;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/doctorCalendar";

    @BeforeEach
    void cleanDB() {
        availabilityPatternRepository.deleteAll();
        calendarExceptionRepository.deleteAll();
        doctorCalendarRepository.deleteAll();
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctorCalendarTest() throws Exception {
        DoctorDto doctorDto = new DoctorDto(1L, "Martin", "Guzman", "martin_guzman@gmail.com", "543434874323");
        DoctorCalendarCreateDto createDto = new DoctorCalendarCreateDto(
                1L, "Dr. Martin Guzman - Clinica del Sur", "America/Argentina/Buenos_Aires"
        );

        when(userServiceApi.getDoctorById(1L)).thenReturn(doctorDto);

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("dr. martin guzman - clinica del sur"))
                .andExpect(jsonPath("$.doctorFirstName").value("Martin"))
                .andExpect(jsonPath("$.doctorLastName").value("Guzman"))
                .andExpect(jsonPath("$.timeZone").value("America/Argentina/Buenos_Aires"))
                .andExpect(jsonPath("$.isActive").value(true));

        DoctorCalendar inDB = doctorCalendarRepository.findAll().stream()
                .filter(dc -> "dr. martin guzman - clinica del sur".equals(dc.getName()))
                .filter(dc -> 1L == dc.getDoctorId())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Doctor Calendar not persisted in the database."));

        assertThat(inDB.getName()).isEqualTo("dr. martin guzman - clinica del sur");
        assertThat(inDB.getDoctorId()).isEqualTo(1L);
        assertThat(inDB.isActive()).isTrue();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctorCalendar_withDuplicateNameTest() throws Exception {
        DoctorDto doctorDto = new DoctorDto(1L, "Martin", "Guzman", "martin_guzman@gmail.com", "543434874323");
        createDoctorCalendar();

        DoctorCalendarCreateDto createDto = new DoctorCalendarCreateDto(
                1L, "Dr. Martin Guzman - Clinica del Sur", "America/Argentina/Buenos_Aires"
        );

        when(userServiceApi.getDoctorById(1L)).thenReturn(doctorDto);

        long bdBefore = doctorCalendarRepository.count();

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Doctor with id 1 already has an active calendar with name: Dr. Martin Guzman - Clinica del Sur"));

        assertThat(doctorCalendarRepository.count()).isEqualTo(bdBefore);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctorCalendar_withInexistentDoctorTest() throws Exception {
        DoctorCalendarCreateDto createDto = new DoctorCalendarCreateDto(
                999L, "Test Calendar", "America/Argentina/Buenos_Aires"
        );

        when(userServiceApi.getDoctorById(999L))
                .thenThrow(new FeignException.NotFound(
                        "Doctor not found",
                        mock(Request.class),
                        null,
                        null
                ));

        long bdBefore = doctorCalendarRepository.count();

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor with id: 999 not found"));

        assertThat(doctorCalendarRepository.count()).isEqualTo(bdBefore);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctorCalendar_withInvalidTimeZoneTest() throws Exception {
        DoctorDto doctorDto = new DoctorDto(1L, "Martin", "Guzman", "martin_guzman@gmail.com", "543434874323");
        DoctorCalendarCreateDto createDto = new DoctorCalendarCreateDto(
                1L, "Test Calendar", "Invalid/TimeZone"
        );

        when(userServiceApi.getDoctorById(1L)).thenReturn(doctorDto);

        long bdBefore = doctorCalendarRepository.count();

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid time zone: Invalid/TimeZone. Use zones like 'America/Argentina/Buenos_Aires', 'UTC', etc."));

        assertThat(doctorCalendarRepository.count()).isEqualTo(bdBefore);
    }

    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findDoctorCalendarByIdTest() throws Exception {
        DoctorCalendar doctorCalendar = createDoctorCalendar();

        mvc.perform(get(BASE_URL + "/{id}", doctorCalendar.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(doctorCalendar.getId()))
                .andExpect(jsonPath("$.name").value("dr. martin guzman - clinica del sur"))
                .andExpect(jsonPath("$.doctorId").value(1L));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentDoctorCalendarByIdTest() throws Exception {
        mvc.perform(get(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor Calendar not found with id 999"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndIsActiveTrueTest() throws Exception {
        DoctorCalendar doctorCalendar = createDoctorCalendar();

        mvc.perform(get(BASE_URL + "/active/doctor/{doctorId}", 1L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].doctorId").value(1L))
                .andExpect(jsonPath("$[0].isActive").value(true))
                .andExpect(jsonPath("$[0].name").value("dr. martin guzman - clinica del sur"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdTest() throws Exception {
        DoctorCalendar activeCalendar = createDoctorCalendar();
        DoctorCalendar inactiveCalendar = createInactiveDoctorCalendar();

        mvc.perform(get(BASE_URL + "/doctor/{doctorId}", 1L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].doctorId").value(1L))
                .andExpect(jsonPath("$[1].doctorId").value(1L));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndNameTest() throws Exception {
        DoctorCalendar doctorCalendar = createDoctorCalendar();

        mvc.perform(get(BASE_URL + "/active/search")
                        .param("doctorId", "1")
                        .param("name", "Dr. Martin Guzman - Clinica del Sur")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("dr. martin guzman - clinica del sur"))
                .andExpect(jsonPath("$.doctorId").value(1L));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findInexistentByDoctorIdAndNameTest() throws Exception {
        mvc.perform(get(BASE_URL + "/active/search")
                        .param("doctorId", "999")
                        .param("name", "Inexistent Calendar")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor Calendar not found with doctor id: 999 and name: inexistent calendar"));
    }

    /* =================== PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateDoctorCalendarTest() throws Exception {
        DoctorDto doctorDto = new DoctorDto(1L, "Martin", "Guzman", "martin_guzman@gmail.com", "543434874323");
        DoctorCalendar doctorCalendar = createDoctorCalendar();

        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(
                null, "Updated Calendar Name", null
        );

        when(userServiceApi.getDoctorById(1L)).thenReturn(doctorDto);

        mvc.perform(patch(BASE_URL + "/{id}", doctorCalendar.getId())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("updated calendar name"));

        DoctorCalendar updated = doctorCalendarRepository.findById(doctorCalendar.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("updated calendar name");
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateDoctorCalendar_withNewDoctorIdTest() throws Exception {
        DoctorDto newDoctorDto = new DoctorDto(2L, "Carlos", "Perez", "carlos@gmail.com", "543434874999");
        DoctorCalendar doctorCalendar = createDoctorCalendar();

        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(
                2L, null, null
        );

        when(userServiceApi.getDoctorById(2L)).thenReturn(newDoctorDto);

        mvc.perform(patch(BASE_URL + "/{id}", doctorCalendar.getId())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.doctorId").value(2L));

        DoctorCalendar updated = doctorCalendarRepository.findById(doctorCalendar.getId()).orElseThrow();
        assertThat(updated.getDoctorId()).isEqualTo(2L);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateDoctorCalendar_withInexistentIdTest() throws Exception {
        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(
                null, "Updated Name", null
        );

        mvc.perform(patch(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor Calendar not found with id 999"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateDoctorCalendar_withDuplicateNameTest() throws Exception {
        DoctorCalendar calendar1 = createDoctorCalendar();
        DoctorCalendar calendar2 = createAnotherDoctorCalendar();

        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(
                null, "Another Calendar", null
        );

        mvc.perform(patch(BASE_URL + "/{id}", calendar1.getId())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Doctor already has another active calendar with name: another calendar"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateDoctorCalendar_withInvalidTimeZoneTest() throws Exception {
        DoctorCalendar doctorCalendar = createDoctorCalendar();

        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(
                null, null, "Invalid/TimeZone"
        );

        mvc.perform(patch(BASE_URL + "/{id}", doctorCalendar.getId())
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid time zone: Invalid/TimeZone. Use zones like 'America/Argentina/Buenos_Aires', 'UTC', etc."));
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteDoctorCalendarTest() throws Exception {
        DoctorCalendar doctorCalendar = createDoctorCalendar();

        Long id = doctorCalendar.getId();
        assertThat(doctorCalendar.isActive()).isTrue();

        mvc.perform(delete(BASE_URL + "/{id}", id)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());

        DoctorCalendar deactivated = doctorCalendarRepository.findById(id).orElseThrow();
        assertThat(deactivated.isActive()).isFalse();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteInexistentDoctorCalendarTest() throws Exception {
        mvc.perform(delete(BASE_URL + "/{id}", 999L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor Calendar not found with id 999"));
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
    }

    /* =================== HELPER METHODS =================== */

    private DoctorCalendar createDoctorCalendar() {
        DoctorCalendar doctorCalendar = new DoctorCalendar();
        doctorCalendar.setDoctorId(1L);
        doctorCalendar.setName("dr. martin guzman - clinica del sur");
        doctorCalendar.setTimeZone(ZoneId.of("America/Argentina/Buenos_Aires"));
        doctorCalendar.setActive(true);
        return doctorCalendarRepository.save(doctorCalendar);
    }

    private DoctorCalendar createInactiveDoctorCalendar() {
        DoctorCalendar doctorCalendar = new DoctorCalendar();
        doctorCalendar.setDoctorId(1L);
        doctorCalendar.setName("inactive calendar");
        doctorCalendar.setTimeZone(ZoneId.of("America/Argentina/Buenos_Aires"));
        doctorCalendar.setActive(false);
        return doctorCalendarRepository.save(doctorCalendar);
    }

    private DoctorCalendar createAnotherDoctorCalendar() {
        DoctorCalendar doctorCalendar = new DoctorCalendar();
        doctorCalendar.setDoctorId(1L);
        doctorCalendar.setName("another calendar");
        doctorCalendar.setTimeZone(ZoneId.of("America/Argentina/Buenos_Aires"));
        doctorCalendar.setActive(true);
        return doctorCalendarRepository.save(doctorCalendar);
    }
}
