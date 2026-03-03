package com.sanatoryApp.CalendarService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionUpdateDto;
import com.sanatoryApp.CalendarService.dto.Request.externalService.DoctorDto;
import com.sanatoryApp.CalendarService.dto.Response.CalendarExceptionResponseDto;
import com.sanatoryApp.CalendarService.entity.*;
import com.sanatoryApp.CalendarService.exception.BadRequest;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.repository.ICalendarExceptionRepository;
import com.sanatoryApp.CalendarService.repository.IDoctorCalendarRepository;
import com.sanatoryApp.CalendarService.repository.UserServiceApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import feign.FeignException;
import feign.Request;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class CalendarExceptionControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ICalendarExceptionRepository calendarExceptionRepository;

    @Autowired
    private IDoctorCalendarRepository doctorCalendarRepository;

    @MockBean
    private UserServiceApi userServiceApi;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/calendarException";

    @BeforeEach
    void cleanDB() {
        calendarExceptionRepository.deleteAll();
        doctorCalendarRepository.deleteAll();
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCalendarException_globalFullDayTest() throws Exception {
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(
                null, null, globalResponseDto.startDate(), null, null, null,
                globalResponseDto.exceptionType(), "Christmas", globalResponseDto.scope());

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.scope").value(globalResponseDto.scope().name()))
                .andExpect(jsonPath("$.exceptionType").value(globalResponseDto.exceptionType().name()))
                .andExpect(jsonPath("$.reason").value(globalResponseDto.reason()))
                .andExpect(jsonPath("$.isFullDay").value(globalResponseDto.isFullDay()))
                .andExpect(jsonPath("$.isSingleDay").value(globalResponseDto.isSingleDay()));

        CalendarException inDB = calendarExceptionRepository.findAll().stream()
                .filter(ce -> globalResponseDto.scope().equals(ce.getScope()))
                .filter(ce -> globalResponseDto.startDate().equals(ce.getStartDate()))
                .filter(ce -> globalResponseDto.reason().equals(ce.getReason()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Calendar Exception not persisted in the database."));

        assertThat(inDB.isFullDay()).isEqualTo(globalResponseDto.isFullDay());
        assertThat(inDB.isSingleDay()).isEqualTo(globalResponseDto.isSingleDay());
        assertThat(inDB.isActive()).isTrue();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCalendarException_semiGlobalTest() throws Exception {

        DoctorCalendar dc = createDC();
        LocalDate startDate = LocalDate.of(2026, 12, 20);
        LocalDate endDate=LocalDate.of(2026, 12, 28);

        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(dc.getDoctorId(), null, startDate, endDate, null, null, ExceptionType.VACATION, "Doctor's anual vacation", ExceptionScope.SEMI_GLOBAL);

        when(userServiceApi.getDoctorById(dc.getDoctorId())).thenReturn(doctorDto);

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.scope").value(semiGlobalResponseDto.scope().name()))
                .andExpect(jsonPath("$.exceptionType").value(semiGlobalResponseDto.exceptionType().name()))
                .andExpect(jsonPath("$.reason").value(semiGlobalResponseDto.reason()));
        CalendarException inBD=calendarExceptionRepository.findAll().stream()
                .filter(ce->semiGlobalResponseDto.doctorId().equals(ce.getDoctorId()))
                .filter(ce->semiGlobalResponseDto.scope().equals(ce.getScope()))
                .filter(ce->semiGlobalResponseDto.startDate().equals(ce.getStartDate()))
                .filter(ce->semiGlobalResponseDto.endDate().equals(ce.getEndDate()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Calendar Exception not persisted in the database."));

        assertThat(inBD.getExceptionType()).isEqualTo(semiGlobalResponseDto.exceptionType());
        assertThat(inBD.getDoctorId()).isEqualTo(semiGlobalResponseDto.doctorId());
        assertThat(inBD.getEndDate()).isEqualTo(semiGlobalResponseDto.endDate());

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    @Transactional
    void createCalendarException_specificPartialDayTestTest() throws Exception {
        DoctorCalendar dc=createDC();
        CalendarExceptionCreateDto createDto=new CalendarExceptionCreateDto(dc.getDoctorId(),dc.getId(),LocalDate.of(2026, 12, 30),null,LocalTime.of(8, 0),LocalTime.of(12, 0),ExceptionType.CUSTOM,"personal reason",ExceptionScope.SPECIFIC);


        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.doctorCalendarId").value(dc.getId()))
                .andExpect(jsonPath("$.scope").value(specificResponseDto.scope().name()))
                .andExpect(jsonPath("$.reason").value(specificResponseDto.reason()));

        CalendarException inBD=calendarExceptionRepository.findAll().stream()
                .filter(ce->specificResponseDto.doctorId().equals(ce.getDoctorId()))
                .filter(ce->dc.getId().equals(ce.getDoctorCalendar().getId()))
                .filter(ce->specificResponseDto.startDate().equals(ce.getStartDate()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Calendar Exception not persisted in the database."));

        assertThat(inBD.getExceptionType()).isEqualTo(specificResponseDto.exceptionType());
        assertThat(inBD.getDoctorCalendar().getDoctorId()).isEqualTo(specificResponseDto.doctorId());
        assertThat(inBD.isFullDay()).isFalse();

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCalendarException_shouldReturnBadRequestWhenConflictExistsTest() throws Exception {
        //GLOBAL CE 2026-12-25
        CalendarException existingCE=createGlobalCE();
        DoctorCalendar dc=createDC();
        CalendarExceptionCreateDto createDto=new CalendarExceptionCreateDto(dc.getDoctorId(),dc.getId(),LocalDate.of(2026, 12, 23),LocalDate.of(2026, 12, 30),null,null,ExceptionType.CUSTOM,"office maintenance",ExceptionScope.SPECIFIC);


        long countBD=calendarExceptionRepository.count();

        mvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto))
                .accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Schedule conflict detected. The following exception(s) already exist:")))
                .andExpect(jsonPath("$.message").value(containsString("Schedule conflict detected.")))
                .andExpect(jsonPath("$.message").value(containsString("GLOBAL")))
                .andExpect(jsonPath("$.message").value(containsString("HOLIDAY")))
                .andExpect(jsonPath("$.message").value(containsString("2026-12-25")));

        assertThat(calendarExceptionRepository.count()).isEqualTo(countBD);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCalendarException_shouldReturnBadRequestWhenInvalidTimeRangeTest() throws Exception {

        DoctorCalendar dc=createDC();
        CalendarExceptionCreateDto createDto=new CalendarExceptionCreateDto(dc.getDoctorId(),dc.getId(),LocalDate.of(2026, 12, 30),null,LocalTime.of(12, 0),LocalTime.of(8, 0),ExceptionType.CUSTOM,"personal reason",ExceptionScope.SPECIFIC);

        long countBD=calendarExceptionRepository.count();

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("End time must be after start time."));
        assertThat(calendarExceptionRepository.count()).isEqualTo(countBD);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCalendarException_shouldReturnBadRequestWhenInvalidDateRangeTest() throws Exception {
        DoctorCalendar dc = createDC();
        LocalDate endDate = LocalDate.of(2026, 12, 20);
        LocalDate startDate=LocalDate.of(2026, 12, 28);

        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(dc.getDoctorId(), null, startDate, endDate, null, null, ExceptionType.VACATION, "Doctor's anual vacation", ExceptionScope.SEMI_GLOBAL);

        long countBD=calendarExceptionRepository.count();

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("End Date must be equals o after Start Date"));
        assertThat(calendarExceptionRepository.count()).isEqualTo(countBD);

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCalendarException_shouldReturnBadRequestWhenScopeInconsistentTest() throws Exception {
        // GLOBAL scope con doctorId -> inconsistente
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(
                1L, null, LocalDate.of(2026, 12, 25), null,
                null, null, ExceptionType.HOLIDAY, null, ExceptionScope.GLOBAL);

        long countBefore = calendarExceptionRepository.count();

        mvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto))
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("GLOBAL exceptions must not have doctorId or doctorCalendarId. Both must be null."));

        assertThat(calendarExceptionRepository.count()).isEqualTo(countBefore);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCalendarException_shouldReturnNotFoundWhenDoctorDoesNotExistTest() throws Exception {
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(
                999L, null, LocalDate.of(2026, 12, 25), LocalDate.of(2026, 12, 31),
                null, null, ExceptionType.VACATION, "annual vacation", ExceptionScope.SEMI_GLOBAL);

        when(userServiceApi.getDoctorById(999L))
                .thenThrow(new FeignException.NotFound(
                        "Doctor not found",
                        mock(Request.class),
                        null,
                        null
                ));

        long countBefore = calendarExceptionRepository.count();

        mvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto))
                .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Doctor with id 999 not found."));

        assertThat(calendarExceptionRepository.count()).isEqualTo(countBefore);
    }

    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByIdTest() throws Exception {
        CalendarException ce = createGlobalCE();

        mvc.perform(get(BASE_URL + "/{id}", ce.getId())
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(ce.getId()))
                .andExpect(jsonPath("$.scope").value(ExceptionScope.GLOBAL.name()))
                .andExpect(jsonPath("$.exceptionType").value(ExceptionType.HOLIDAY.name()))
                .andExpect(jsonPath("$.startDate").value("2026-12-25"))
                .andExpect(jsonPath("$.reason").value("christmas"))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        mvc.perform(get(BASE_URL + "/{id}", 999L)
                .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Calendar exception with id 999 not found."));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdTest() throws Exception {
        createSemiGlobalCE();

        mvc.perform(get(BASE_URL + "/doctor/{doctorId}", 1L)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].doctorId").value(1L))
                .andExpect(jsonPath("$[0].scope").value(ExceptionScope.SEMI_GLOBAL.name()))
                .andExpect(jsonPath("$[0].startDate").value("2026-12-20"))
                .andExpect(jsonPath("$[0].endDate").value("2026-12-28"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorId_shouldReturnEmptyListTest() throws Exception {
        mvc.perform(get(BASE_URL + "/doctor/{doctorId}", 999L)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAllGlobalAndIsActiveTest() throws Exception {
        createGlobalCE();

        mvc.perform(get(BASE_URL + "/global")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].scope").value(ExceptionScope.GLOBAL.name()))
                .andExpect(jsonPath("$[0].exceptionType").value(ExceptionType.HOLIDAY.name()))
                .andExpect(jsonPath("$[0].startDate").value("2026-12-25"))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAllGlobalAndIsActive_shouldReturnEmptyListTest() throws Exception {
        mvc.perform(get(BASE_URL + "/global")
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findSemiGlobalByDoctorIdAndIsActiveTest() throws Exception {
        createSemiGlobalCE();

        mvc.perform(get(BASE_URL + "/semi-global/doctor/{doctorId}", 1L)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].scope").value(ExceptionScope.SEMI_GLOBAL.name()))
                .andExpect(jsonPath("$[0].doctorId").value(1L))
                .andExpect(jsonPath("$[0].startDate").value("2026-12-20"))
                .andExpect(jsonPath("$[0].endDate").value("2026-12-28"))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findSemiGlobalByDoctorIdAndIsActive_shouldReturnEmptyListTest() throws Exception {
        mvc.perform(get(BASE_URL + "/semi-global/doctor/{doctorId}", 999L)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    @Transactional
    void findSpecificByDoctorCalendarIdAndIsActiveTest() throws Exception {
        DoctorCalendar dc = createDC();
        createSpecificCE(dc);

        mvc.perform(get(BASE_URL + "/specific/doctorCalendar/{doctorCalendarId}", dc.getId())
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].scope").value(ExceptionScope.SPECIFIC.name()))
                .andExpect(jsonPath("$[0].doctorCalendarId").value(dc.getId()))
                .andExpect(jsonPath("$[0].doctorId").value(dc.getDoctorId()))
                .andExpect(jsonPath("$[0].startDate").value("2026-12-30"))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findSpecificByDoctorCalendarIdAndIsActive_shouldReturnEmptyListTest() throws Exception {
        mvc.perform(get(BASE_URL + "/specific/doctorCalendar/{doctorCalendarId}", 999L)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndDateTest() throws Exception {
        createSemiGlobalCE(); // doctorId=1, 2026-12-20 to 2026-12-28

        mvc.perform(get(BASE_URL + "/doctor/{doctorId}/date/{date}", 1L, LocalDate.of(2026, 12, 25))
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].doctorId").value(1L))
                .andExpect(jsonPath("$[0].scope").value(ExceptionScope.SEMI_GLOBAL.name()))
                .andExpect(jsonPath("$[0].startDate").value("2026-12-20"))
                .andExpect(jsonPath("$[0].endDate").value("2026-12-28"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndDate_shouldReturnEmptyListTest() throws Exception {
        mvc.perform(get(BASE_URL + "/doctor/{doctorId}/date/{date}", 1L, LocalDate.of(2026, 12, 25))
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    /* =================== PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateCalendarExceptionTest() throws Exception {
        CalendarException existingCE = createGlobalCE();

        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setReason(JsonNullable.of("Updated Christmas"));

        mvc.perform(patch(BASE_URL + "/{id}", existingCE.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(existingCE.getId()))
                .andExpect(jsonPath("$.scope").value(ExceptionScope.GLOBAL.name()))
                .andExpect(jsonPath("$.reason").value("updated christmas"));

        CalendarException updated = calendarExceptionRepository.findById(existingCE.getId()).orElseThrow();
        assertThat(updated.getReason()).isEqualTo("updated christmas");
        assertThat(updated.getExceptionType()).isEqualTo(ExceptionType.HOLIDAY);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateCalendarException_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setReason(JsonNullable.of("some reason"));

        mvc.perform(patch(BASE_URL + "/{id}", 999L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Calendar exception with id 999 not found."));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateCalendarException_shouldReturnBadRequestWhenConflictExistsTest() throws Exception {
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setStartDate(JsonNullable.of(LocalDate.of(2026,12,20)));
        updateDto.setEndDate(JsonNullable.of(LocalDate.of(2026,12,27)));

        //Conflict ce
        CalendarException existingCE1=createGlobalCE();

        //Updated ce
        DoctorCalendar doctorCalendar=createDC();
        CalendarException existingCE2=createSpecificCE(doctorCalendar);


        mvc.perform(patch(BASE_URL + "/{id}", existingCE2.getId())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Schedule conflict detected. The following exception(s) already exist:")))
                .andExpect(jsonPath("$.message").value(containsString("GLOBAL")))
                .andExpect(jsonPath("$.message").value(containsString("HOLIDAY")))
                .andExpect(jsonPath("$.message").value(containsString("2026-12-25")));

        CalendarException inBD=calendarExceptionRepository.findById(existingCE2.getId())
                .orElseThrow(()->new ResourceNotFound("Calendar exception with id "+existingCE2.getId()+" not found."));
        assertThat(inBD.getStartDate()).isEqualTo(existingCE2.getStartDate());

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateCalendarException_shouldReturnBadRequestWhenInvalidTimeRangeTest() throws Exception {
        CalendarException ce = createGlobalCE(); // full day, sin horas

        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setStartTime(JsonNullable.of(LocalTime.of(8, 0))); // startTime sin endTime

        mvc.perform(patch(BASE_URL + "/{id}", ce.getId())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(containsString("Both start time and end time must be provided")));
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteCalendarExceptionTest() throws Exception {
        CalendarException ce = createGlobalCE();
        Long id = ce.getId();
        assertThat(calendarExceptionRepository.existsById(id)).isTrue();

        mvc.perform(delete(BASE_URL + "/{id}", id)
                .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Calendar Exception with id: " + id + " successfully deleted."));

        assertThat(calendarExceptionRepository.existsById(id)).isFalse();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteCalendarException_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        mvc.perform(delete(BASE_URL + "/{id}", 999L)
                .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Calendar Exception not found with id:999"));
    }

    /* =================== AUTHORIZATION TESTS =================== */

    @Test
    void createCalendarException_shouldReturnForbiddenWhenNotAuthenticatedTest() throws Exception {
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(
                null, null, globalResponseDto.startDate(), null, null, null,
                globalResponseDto.exceptionType(), "Christmas", globalResponseDto.scope());

        Long countBD=calendarExceptionRepository.count();

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        assertThat(calendarExceptionRepository.count()).isEqualTo(countBD);

    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void createCalendarException_shouldReturnForbiddenWhenInsufficientRoleTest() throws Exception {
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(
                null, null, globalResponseDto.startDate(), null, null, null,
                globalResponseDto.exceptionType(), "Christmas", globalResponseDto.scope());

        Long countBD=calendarExceptionRepository.count();

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        assertThat(calendarExceptionRepository.count()).isEqualTo(countBD);
    }

    /* =================== HELPER METHODS =================== */

    private DoctorCalendar createDC() {
        DoctorCalendar dc = new DoctorCalendar();
        dc.setDoctorId(1L);
        dc.setName("Dr. Martin Guzman - Clinica del Sur");
        dc.setTimeZone(ZoneId.of("America/Argentina/Buenos_Aires"));
        dc.setActive(true);
        return doctorCalendarRepository.save(dc);
    }

    private CalendarException createGlobalCE() {
        CalendarException ce = new CalendarException();
        ce.setScope(ExceptionScope.GLOBAL);
        ce.setStartDate(LocalDate.of(2026, 12, 25));
        ce.setExceptionType(ExceptionType.HOLIDAY);
        ce.setReason("christmas");
        return calendarExceptionRepository.save(ce);
    }

    private static final CalendarExceptionResponseDto globalResponseDto =
            new CalendarExceptionResponseDto(
                    null, ExceptionScope.GLOBAL, null, null, null,
                    LocalDate.of(2026, 12, 25), null, null, null,
                    ExceptionType.HOLIDAY, "christmas",
                    true, true, true);


    private CalendarException createSemiGlobalCE() {
        CalendarException ce = new CalendarException();
        ce.setScope(ExceptionScope.SEMI_GLOBAL);
        ce.setDoctorId(1L);
        ce.setStartDate(LocalDate.of(2026, 12, 20));
        ce.setEndDate(LocalDate.of(2026, 12, 28));
        ce.setExceptionType(ExceptionType.VACATION);
        ce.setReason("Doctor's anual vacation");
        return calendarExceptionRepository.save(ce);
    }

    private static final CalendarExceptionResponseDto semiGlobalResponseDto =
            new CalendarExceptionResponseDto(
                    null, ExceptionScope.SEMI_GLOBAL, 1L, null, null,
                    LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 28), null, null,
                    ExceptionType.VACATION, "doctor's anual vacation",
                    true, true, false);


    private CalendarException createSpecificCE(DoctorCalendar dc) {
        CalendarException ce = new CalendarException();
        ce.setScope(ExceptionScope.SPECIFIC);
        ce.setDoctorCalendar(dc);
        ce.setDoctorId(dc.getDoctorId());
        ce.setStartDate(LocalDate.of(2026, 12, 30));
        ce.setStartTime(LocalTime.of(8, 0));
        ce.setEndTime(LocalTime.of(12, 0));
        ce.setExceptionType(ExceptionType.CUSTOM);
        ce.setReason("personal reason");
        return calendarExceptionRepository.save(ce);
    }

    private static final CalendarExceptionResponseDto specificResponseDto =
            new CalendarExceptionResponseDto(
                    null, ExceptionScope.SPECIFIC, 1L, null, "Dr. Martin Guzman - Clinica del Sur",
                    LocalDate.of(2026, 12, 30), null, LocalTime.of(8, 0), LocalTime.of(12, 0),
                    ExceptionType.CUSTOM, "personal reason",
                    true, false, true);

    private static final DoctorDto doctorDto = new DoctorDto(1L, "Martin", "Guzman", "martin_guzman@gmail.com", "543434874323");



}



