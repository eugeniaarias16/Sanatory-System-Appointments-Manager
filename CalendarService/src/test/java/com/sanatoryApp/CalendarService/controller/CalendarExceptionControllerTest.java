package com.sanatoryApp.CalendarService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.CalendarService.config.JacksonConfig;
import com.sanatoryApp.CalendarService.config.SecurityConfig;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.CalendarExceptionResponseDto;
import com.sanatoryApp.CalendarService.entity.ExceptionScope;
import com.sanatoryApp.CalendarService.entity.ExceptionType;
import com.sanatoryApp.CalendarService.exception.BadRequest;
import com.sanatoryApp.CalendarService.exception.InvalidTimeRangeException;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.service.ICalendarExceptionService;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CalendarExceptionController.class)
@Import({SecurityConfig.class, JacksonConfig.class})
public class CalendarExceptionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICalendarExceptionService calendarExceptionService;

    private static final String BASE_URL = "/calendarException";

    /* =================== GET ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByIdTest() throws Exception {
        when(calendarExceptionService.findByIdAndIsActive(1L)).thenReturn(globalResponseDto);

        mvc.perform(get(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.scope").value("GLOBAL"))
                .andExpect(jsonPath("$.exceptionType").value("HOLIDAY"))
                .andExpect(jsonPath("$.reason").value("Christmas"))
                .andExpect(jsonPath("$.isFullDay").value(true))
                .andExpect(jsonPath("$.isSingleDay").value(true));

        verify(calendarExceptionService).findByIdAndIsActive(1L);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        when(calendarExceptionService.findByIdAndIsActive(99L))
                .thenThrow(new ResourceNotFound("Calendar exception with id 99 not found."));

        mvc.perform(get(BASE_URL + "/{id}", 99L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Calendar exception with id 99 not found."));

        verify(calendarExceptionService).findByIdAndIsActive(99L);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdTest() throws Exception {
        when(calendarExceptionService.findBydDoctorId(1L)).thenReturn(List.of(semiGlobalResponseDto));

        mvc.perform(get(BASE_URL + "/doctor/{doctorId}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].scope").value("SEMI_GLOBAL"))
                .andExpect(jsonPath("$[0].doctorId").value(1L));

        verify(calendarExceptionService).findBydDoctorId(1L);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorId_shouldReturnEmptyListTest() throws Exception {
        when(calendarExceptionService.findBydDoctorId(999L)).thenReturn(Collections.emptyList());

        mvc.perform(get(BASE_URL + "/doctor/{doctorId}", 999L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(calendarExceptionService).findBydDoctorId(999L);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAllGlobalAndIsActiveTest() throws Exception {
        when(calendarExceptionService.findAllGlobalAndIsActive()).thenReturn(List.of(globalResponseDto));

        mvc.perform(get(BASE_URL + "/global").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].scope").value("GLOBAL"));

        verify(calendarExceptionService).findAllGlobalAndIsActive();
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAllGlobalAndIsActive_shouldReturnEmptyListTest() throws Exception {
        when(calendarExceptionService.findAllGlobalAndIsActive()).thenReturn(Collections.emptyList());

        mvc.perform(get(BASE_URL + "/global").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(calendarExceptionService).findAllGlobalAndIsActive();
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findSemiGlobalByDoctorIdAndIsActiveTest() throws Exception {
        when(calendarExceptionService.findSemiGlobalByDoctorIdAndIsActive(1L))
                .thenReturn(List.of(semiGlobalResponseDto));

        mvc.perform(get(BASE_URL + "/semi-global/doctor/{doctorId}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].scope").value("SEMI_GLOBAL"))
                .andExpect(jsonPath("$[0].doctorId").value(1L));

        verify(calendarExceptionService).findSemiGlobalByDoctorIdAndIsActive(1L);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findSemiGlobalByDoctorIdAndIsActive_shouldReturnEmptyListTest() throws Exception {
        when(calendarExceptionService.findSemiGlobalByDoctorIdAndIsActive(999L))
                .thenReturn(Collections.emptyList());

        mvc.perform(get(BASE_URL + "/semi-global/doctor/{doctorId}", 999L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(calendarExceptionService).findSemiGlobalByDoctorIdAndIsActive(999L);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findSpecificByDoctorCalendarIdAndIsActiveTest() throws Exception {
        when(calendarExceptionService.findSpecificByDoctorCalendarIdAndIsActive(10L))
                .thenReturn(List.of(specificResponseDto));

        mvc.perform(get(BASE_URL + "/specific/doctorCalendar/{doctorCalendarId}", 10L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].scope").value("SPECIFIC"))
                .andExpect(jsonPath("$[0].doctorCalendarId").value(10L));

        verify(calendarExceptionService).findSpecificByDoctorCalendarIdAndIsActive(10L);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findSpecificByDoctorCalendarIdAndIsActive_shouldReturnEmptyListTest() throws Exception {
        when(calendarExceptionService.findSpecificByDoctorCalendarIdAndIsActive(999L))
                .thenReturn(Collections.emptyList());

        mvc.perform(get(BASE_URL + "/specific/doctorCalendar/{doctorCalendarId}", 999L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(calendarExceptionService).findSpecificByDoctorCalendarIdAndIsActive(999L);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndDateTest() throws Exception {
        LocalDate date = LocalDate.of(2026, 12, 25);
        when(calendarExceptionService.findByDoctorIdAndDateAndHour(1L, date))
                .thenReturn(List.of(globalResponseDto));

        mvc.perform(get(BASE_URL + "/doctor/{doctorId}/date/{date}", 1L, date).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].startDate").value("2026-12-25"));

        verify(calendarExceptionService).findByDoctorIdAndDateAndHour(1L, date);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndDate_shouldReturnEmptyListTest() throws Exception {
        LocalDate date = LocalDate.of(2026, 6, 15);
        when(calendarExceptionService.findByDoctorIdAndDateAndHour(1L, date))
                .thenReturn(Collections.emptyList());

        mvc.perform(get(BASE_URL + "/doctor/{doctorId}/date/{date}", 1L, date).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(calendarExceptionService).findByDoctorIdAndDateAndHour(1L, date);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCalendarExceptionTest() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 12, 20);
        LocalDate endDate = LocalDate.of(2026, 12, 28);
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(
                1L, null, startDate, endDate, null, null,
                ExceptionType.VACATION, null, ExceptionScope.SEMI_GLOBAL);
        CalendarExceptionResponseDto responseDto = new CalendarExceptionResponseDto(
                12L, ExceptionScope.SEMI_GLOBAL, 1L, null, null,
                startDate, endDate, null, null, ExceptionType.VACATION, null,
                true, true, false);

        when(calendarExceptionService.createCalendarException(any(CalendarExceptionCreateDto.class)))
                .thenReturn(responseDto);

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(12L))
                .andExpect(jsonPath("$.scope").value("SEMI_GLOBAL"))
                .andExpect(jsonPath("$.exceptionType").value("VACATION"))
                .andExpect(jsonPath("$.doctorId").value(1L));

        verify(calendarExceptionService).createCalendarException(any(CalendarExceptionCreateDto.class));
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCalendarException_shouldReturnBadRequestWhenConflictExistsTest() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 12, 20);
        LocalDate endDate = LocalDate.of(2026, 12, 28);
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(
                1L, null, startDate, endDate, null, null,
                ExceptionType.VACATION, null, ExceptionScope.SEMI_GLOBAL);

        String errorMsg = "Schedule conflict detected";
        when(calendarExceptionService.createCalendarException(any(CalendarExceptionCreateDto.class)))
                .thenThrow(new BadRequest(errorMsg));

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(errorMsg));

        verify(calendarExceptionService).createCalendarException(any(CalendarExceptionCreateDto.class));
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCalendarException_shouldReturnNotFoundWhenDoctorDoesNotExistTest() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 12, 20);
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(
                999L, null, startDate, null, null, null,
                ExceptionType.VACATION, null, ExceptionScope.SEMI_GLOBAL);

        when(calendarExceptionService.createCalendarException(any(CalendarExceptionCreateDto.class)))
                .thenThrow(new ResourceNotFound("Doctor with id 999 not found."));

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor with id 999 not found."));

        verify(calendarExceptionService).createCalendarException(any(CalendarExceptionCreateDto.class));
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createCalendarException_shouldReturnBadRequestWhenInvalidTimeRangeTest() throws Exception {
        LocalDate startDate = LocalDate.of(2026, 12, 20);
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(
                null, null, startDate, null, LocalTime.of(18, 0), LocalTime.of(9, 0),
                ExceptionType.HOLIDAY, null, ExceptionScope.GLOBAL);

        when(calendarExceptionService.createCalendarException(any(CalendarExceptionCreateDto.class)))
                .thenThrow(new InvalidTimeRangeException("End time must be after start time."));

        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("End time must be after start time."));

        verify(calendarExceptionService).createCalendarException(any(CalendarExceptionCreateDto.class));
        verifyNoMoreInteractions(calendarExceptionService);
    }

    /* =================== PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateCalendarExceptionTest() throws Exception {
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setReason(JsonNullable.of("updated reason"));

        CalendarExceptionResponseDto responseDto = new CalendarExceptionResponseDto(
                1L, ExceptionScope.GLOBAL, null, null, null,
                LocalDate.of(2026, 12, 25), null, null, null,
                ExceptionType.HOLIDAY, "updated reason",
                true, true, true);

        when(calendarExceptionService.updateCalendarException(anyLong(), any(CalendarExceptionUpdateDto.class)))
                .thenReturn(responseDto);

        mvc.perform(patch(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.reason").value("updated reason"));

        verify(calendarExceptionService).updateCalendarException(anyLong(), any(CalendarExceptionUpdateDto.class));
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateCalendarException_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setReason(JsonNullable.of("some reason"));

        when(calendarExceptionService.updateCalendarException(anyLong(), any(CalendarExceptionUpdateDto.class)))
                .thenThrow(new ResourceNotFound("Calendar exception with id 99 not found."));

        mvc.perform(patch(BASE_URL + "/{id}", 99L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Calendar exception with id 99 not found."));

        verify(calendarExceptionService).updateCalendarException(anyLong(), any(CalendarExceptionUpdateDto.class));
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateCalendarException_shouldReturnBadRequestWhenConflictExistsTest() throws Exception {
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setStartDate(JsonNullable.of(LocalDate.of(2026, 12, 20)));
        updateDto.setEndDate(JsonNullable.of(LocalDate.of(2026, 12, 28)));

        String errorMsg = "Schedule conflict detected";
        when(calendarExceptionService.updateCalendarException(anyLong(), any(CalendarExceptionUpdateDto.class)))
                .thenThrow(new BadRequest(errorMsg));

        mvc.perform(patch(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(errorMsg));

        verify(calendarExceptionService).updateCalendarException(anyLong(), any(CalendarExceptionUpdateDto.class));
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateCalendarException_shouldReturnBadRequestWhenInvalidTimeRangeTest() throws Exception {
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setStartTime(JsonNullable.of(LocalTime.of(18, 0)));
        updateDto.setEndTime(JsonNullable.of(LocalTime.of(9, 0)));

        when(calendarExceptionService.updateCalendarException(anyLong(), any(CalendarExceptionUpdateDto.class)))
                .thenThrow(new InvalidTimeRangeException("End time must be after start time."));

        mvc.perform(patch(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("End time must be after start time."));

        verify(calendarExceptionService).updateCalendarException(anyLong(), any(CalendarExceptionUpdateDto.class));
        verifyNoMoreInteractions(calendarExceptionService);
    }

    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteCalendarExceptionTest() throws Exception {
        doNothing().when(calendarExceptionService).deleteCalendarExceptionById(1L);

        mvc.perform(delete(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Calendar Exception with id: 1 successfully deleted."));

        verify(calendarExceptionService).deleteCalendarExceptionById(1L);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteCalendarException_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        doThrow(new ResourceNotFound("Calendar Exception not found with id:99"))
                .when(calendarExceptionService).deleteCalendarExceptionById(99L);

        mvc.perform(delete(BASE_URL + "/{id}", 99L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Calendar Exception not found with id:99"));

        verify(calendarExceptionService).deleteCalendarExceptionById(99L);
        verifyNoMoreInteractions(calendarExceptionService);
    }

    /* =================== AUTHORIZATION TESTS =================== */

    @Test
    void findById_shouldReturnForbiddenWhenNotAuthenticatedTest() throws Exception {
        mvc.perform(get(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(calendarExceptionService);
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void findById_shouldReturnForbiddenWhenInsufficientRoleTest() throws Exception {
        mvc.perform(get(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(calendarExceptionService);
    }

    /* =================== HELPER DATA =================== */

    private static final CalendarExceptionResponseDto globalResponseDto =
            new CalendarExceptionResponseDto(
                    1L, ExceptionScope.GLOBAL, null, null, null,
                    LocalDate.of(2026, 12, 25), null, null, null,
                    ExceptionType.HOLIDAY, "Christmas",
                    true, true, true);

    private static final CalendarExceptionResponseDto semiGlobalResponseDto =
            new CalendarExceptionResponseDto(
                    2L, ExceptionScope.SEMI_GLOBAL, 1L, null, null,
                    LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 28), null, null,
                    ExceptionType.VACATION, null,
                    true, true, false);

    private static final CalendarExceptionResponseDto specificResponseDto =
            new CalendarExceptionResponseDto(
                    3L, ExceptionScope.SPECIFIC, 1L, 10L, "Dr. Martin Guzman - Clinica del Sur",
                    LocalDate.of(2026, 12, 30), null, LocalTime.of(8, 0), LocalTime.of(12, 0),
                    ExceptionType.CUSTOM, "personal reason",
                    true, false, true);
}
