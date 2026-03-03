package com.sanatoryApp.CalendarService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.CalendarService.config.SecurityConfig;
import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.AvailabilityPatternResponseDto;
import com.sanatoryApp.CalendarService.exception.InvalidTimeRangeException;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.service.IAvailabilityPatternService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvailabilityPatternController.class)
@Import(SecurityConfig.class)
public class AvailabilityPatternControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IAvailabilityPatternService availabilityPatternService;

    private static final String BASE_URL = "/availabilityPattern";

    // ==================== GET TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAvailabilityPatternByIdTest() throws Exception {
        // Arrange
        when(availabilityPatternService.findAvailabilityPatternById(anyLong()))
                .thenReturn(expectedResponseDto);

        // Act & Assert
        mvc.perform(get(BASE_URL + "/id/{id}", 3L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.doctorCalendarId").value(10L))
                .andExpect(jsonPath("$.doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.startTime").value("08:00:00"))
                .andExpect(jsonPath("$.endTime").value("17:00:00"));

        verify(availabilityPatternService).findAvailabilityPatternById(anyLong());
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAvailabilityPatternById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        // Arrange
        when(availabilityPatternService.findAvailabilityPatternById(anyLong()))
                .thenThrow(new ResourceNotFound("Availability Pattern not found with id: 99"));

        // Act & Assert
        mvc.perform(get(BASE_URL + "/id/{id}", 99L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Availability Pattern not found with id: 99"));

        verify(availabilityPatternService).findAvailabilityPatternById(anyLong());
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAvailabilityPatternByDoctorCalendarIdTest() throws Exception {
        // Arrange
        when(availabilityPatternService.findAvailabilityPatternByDoctorCalendarId(anyLong()))
                .thenReturn(List.of(expectedResponseDto));

        // Act & Assert
        mvc.perform(get(BASE_URL + "/doctorCalendar/{id}", 10L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].doctorCalendarId").value(10L))
                .andExpect(jsonPath("$[0].doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"));

        verify(availabilityPatternService).findAvailabilityPatternByDoctorCalendarId(anyLong());
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAvailabilityPatternByDoctorCalendarId_shouldReturnNotFoundWhenCalendarIdDoesNotExistTest() throws Exception {
        // Arrange
        when(availabilityPatternService.findAvailabilityPatternByDoctorCalendarId(anyLong()))
                .thenThrow(new ResourceNotFound("Availability Pattern not found with doctor calendar id: 99"));

        // Act & Assert
        mvc.perform(get(BASE_URL + "/doctorCalendar/{id}", 99L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Availability Pattern not found with doctor calendar id: 99"));

        verify(availabilityPatternService).findAvailabilityPatternByDoctorCalendarId(anyLong());
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrueTest() throws Exception {
        // Arrange
        when(availabilityPatternService.findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(anyLong(), any(DayOfWeek.class)))
                .thenReturn(List.of(expectedResponseDto));

        // Act & Assert
        mvc.perform(get(BASE_URL + "/search/calendarAndDay")
                        .param("calendarId", "10")
                        .param("dayOfWeek", "MONDAY")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].doctorCalendarId").value(10L))
                .andExpect(jsonPath("$[0].doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"))
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"));

        verify(availabilityPatternService).findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(anyLong(), any(DayOfWeek.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue_shouldReturnEmptyListTest() throws Exception {
        // Arrange
        when(availabilityPatternService.findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(anyLong(), any(DayOfWeek.class)))
                .thenReturn(List.of());

        // Act & Assert
        mvc.perform(get(BASE_URL + "/search/calendarAndDay")
                        .param("calendarId", "10")
                        .param("dayOfWeek", "TUESDAY")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(availabilityPatternService).findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(anyLong(), any(DayOfWeek.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdTest() throws Exception {
        // Arrange
        when(availabilityPatternService.findByDoctorId(anyLong()))
                .thenReturn(List.of(expectedResponseDto));

        // Act & Assert
        mvc.perform(get(BASE_URL + "/doctor/{doctorId}", 1L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].doctorCalendarId").value(10L))
                .andExpect(jsonPath("$[0].doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"));

        verify(availabilityPatternService).findByDoctorId(anyLong());
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorId_shouldReturnEmptyListTest() throws Exception {
        // Arrange
        when(availabilityPatternService.findByDoctorId(anyLong()))
                .thenReturn(List.of());

        // Act & Assert
        mvc.perform(get(BASE_URL + "/doctor/{doctorId}", 99L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(availabilityPatternService).findByDoctorId(anyLong());
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndDayTest() throws Exception {
        // Arrange
        when(availabilityPatternService.findByDoctorIdAndDay(anyLong(), any(DayOfWeek.class)))
                .thenReturn(List.of(expectedResponseDto));

        // Act & Assert
        mvc.perform(get(BASE_URL + "/doctor/{doctorId}/day/{dayOfWeek}", 1L, "MONDAY")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].doctorCalendarId").value(10L))
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"));

        verify(availabilityPatternService).findByDoctorIdAndDay(anyLong(), any(DayOfWeek.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndDay_shouldReturnEmptyListTest() throws Exception {
        // Arrange
        when(availabilityPatternService.findByDoctorIdAndDay(anyLong(), any(DayOfWeek.class)))
                .thenReturn(List.of());

        // Act & Assert
        mvc.perform(get(BASE_URL + "/doctor/{doctorId}/day/{dayOfWeek}", 1L, "SUNDAY")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(availabilityPatternService).findByDoctorIdAndDay(anyLong(), any(DayOfWeek.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    // ==================== POST TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAvailabilityPatternTest() throws Exception {
        // Arrange
        when(availabilityPatternService.createAvailabilityPattern(any(AvailabilityPatternCreateDto.class)))
                .thenReturn(expectedResponseDto);

        // Act & Assert
        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.doctorCalendarId").value(10L))
                .andExpect(jsonPath("$.doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.startTime").value("08:00:00"))
                .andExpect(jsonPath("$.endTime").value("17:00:00"));

        verify(availabilityPatternService).createAvailabilityPattern(any(AvailabilityPatternCreateDto.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAvailabilityPattern_shouldReturnNotFoundWhenDoctorCalendarDoesNotExistTest() throws Exception {
        // Arrange
        when(availabilityPatternService.createAvailabilityPattern(any(AvailabilityPatternCreateDto.class)))
                .thenThrow(new ResourceNotFound("Doctor Calendar not found with doctor calendar id: 99"));

        // Act & Assert
        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Doctor Calendar not found with doctor calendar id: 99"));

        verify(availabilityPatternService).createAvailabilityPattern(any(AvailabilityPatternCreateDto.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAvailabilityPattern_shouldReturnBadRequestWhenTimeRangeIsInvalidTest() throws Exception {
        // Arrange - Start time (17:00) is AFTER end time (08:00) - invalid
        AvailabilityPatternCreateDto badCreateDto = new AvailabilityPatternCreateDto(
                10L,
                DayOfWeek.MONDAY,
                LocalTime.of(17, 0),
                LocalTime.of(8, 0)
        );

        when(availabilityPatternService.createAvailabilityPattern(any(AvailabilityPatternCreateDto.class)))
                .thenThrow(new InvalidTimeRangeException("End time must be after start time."));

        // Act & Assert - InvalidTimeRangeException returns 400 BAD_REQUEST (GlobalExceptionHandler line 40)
        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badCreateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("End time must be after start time."));

        verify(availabilityPatternService).createAvailabilityPattern(any(AvailabilityPatternCreateDto.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAvailabilityPattern_shouldReturnBadRequestWhenOverlappingPatternExistsTest() throws Exception {
        // Arrange
        when(availabilityPatternService.createAvailabilityPattern(any(AvailabilityPatternCreateDto.class)))
                .thenThrow(new IllegalArgumentException(
                        "Schedule conflict: The new time slot overlaps with an existing schedule. " +
                                "The doctor cannot have overlapping schedules across any of their calendars."
                ));

        // Act & Assert
        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(
                        "Schedule conflict: The new time slot overlaps with an existing schedule. " +
                                "The doctor cannot have overlapping schedules across any of their calendars."
                ));

        verify(availabilityPatternService).createAvailabilityPattern(any(AvailabilityPatternCreateDto.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    // ==================== PATCH TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAvailabilityPatternByIdTest() throws Exception {
        // Arrange
        AvailabilityPatternResponseDto updatedResponse = new AvailabilityPatternResponseDto(
                3L, 10L, "Dr. Martin Guzman- Clinica del Sur",
                DayOfWeek.MONDAY, LocalTime.of(9, 15), LocalTime.of(19, 0), true
        );

        when(availabilityPatternService.updateAvailabilityPatternById(anyLong(), any(AvailabilityPatternUpdateDto.class)))
                .thenReturn(updatedResponse);

        // Act & Assert
        mvc.perform(patch(BASE_URL + "/update/{id}", 3L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.doctorCalendarId").value(10L))
                .andExpect(jsonPath("$.doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"))
                .andExpect(jsonPath("$.startTime").value("09:15:00"))
                .andExpect(jsonPath("$.endTime").value("19:00:00"));

        verify(availabilityPatternService).updateAvailabilityPatternById(anyLong(), any(AvailabilityPatternUpdateDto.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAvailabilityPatternById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        // Arrange
        when(availabilityPatternService.updateAvailabilityPatternById(anyLong(), any(AvailabilityPatternUpdateDto.class)))
                .thenThrow(new ResourceNotFound("Availability Pattern not found with id: 99"));

        // Act & Assert
        mvc.perform(patch(BASE_URL + "/update/{id}", 99L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Availability Pattern not found with id: 99"));

        verify(availabilityPatternService).updateAvailabilityPatternById(anyLong(), any(AvailabilityPatternUpdateDto.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAvailabilityPatternById_shouldReturnBadRequestWhenTimeRangeIsInvalidTest() throws Exception {
        // Arrange - Invalid time range (end before start)
        AvailabilityPatternUpdateDto badUpdateDto = new AvailabilityPatternUpdateDto(
                LocalTime.of(18, 0),
                LocalTime.of(9, 0)
        );

        when(availabilityPatternService.updateAvailabilityPatternById(anyLong(), any(AvailabilityPatternUpdateDto.class)))
                .thenThrow(new InvalidTimeRangeException("End time must be after start time."));

        // Act & Assert - InvalidTimeRangeException returns 400 BAD_REQUEST (GlobalExceptionHandler line 40)
        mvc.perform(patch(BASE_URL + "/update/{id}", 3L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUpdateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("End time must be after start time."));

        verify(availabilityPatternService).updateAvailabilityPatternById(anyLong(), any(AvailabilityPatternUpdateDto.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAvailabilityPatternById_shouldReturnBadRequestWhenOverlappingPatternExistsTest() throws Exception {
        // Arrange
        when(availabilityPatternService.updateAvailabilityPatternById(anyLong(), any(AvailabilityPatternUpdateDto.class)))
                .thenThrow(new IllegalArgumentException("Schedule conflict: The updated time slot overlaps with an existing schedule."));

        // Act & Assert
        mvc.perform(patch(BASE_URL + "/update/{id}", 3L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Schedule conflict: The updated time slot overlaps with an existing schedule."));

        verify(availabilityPatternService).updateAvailabilityPatternById(anyLong(), any(AvailabilityPatternUpdateDto.class));
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeleteAvailabilityPatternByIdTest() throws Exception {
        // Arrange - softDelete is void, no need to mock return value
        doNothing().when(availabilityPatternService).softDeleteAvailabilityPatternById(anyLong());

        // Act & Assert
        mvc.perform(patch(BASE_URL + "/softDelete/{id}", 3L))
                .andExpect(status().isOk())
                .andExpect(content().string("Availability Pattern with id 3 successfully deactivated."));

        verify(availabilityPatternService).softDeleteAvailabilityPatternById(anyLong());
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeleteAvailabilityPatternById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        // Arrange
        doThrow(new ResourceNotFound("Availability Pattern not found with id: 99"))
                .when(availabilityPatternService).softDeleteAvailabilityPatternById(anyLong());

        // Act & Assert
        mvc.perform(patch(BASE_URL + "/softDelete/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Availability Pattern not found with id: 99"));

        verify(availabilityPatternService).softDeleteAvailabilityPatternById(anyLong());
        verifyNoMoreInteractions(availabilityPatternService);
    }

    // ==================== DELETE TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteAvailabilityPatternByIdTest() throws Exception {
        // Arrange - delete is void, no need to mock return value
        doNothing().when(availabilityPatternService).deleteAvailabilityPatternById(anyLong());

        // Act & Assert
        mvc.perform(delete(BASE_URL + "/delete/{id}", 3L))
                .andExpect(status().isOk())
                .andExpect(content().string("Availability Pattern with id 3 successfully deleted."));

        verify(availabilityPatternService).deleteAvailabilityPatternById(anyLong());
        verifyNoMoreInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteAvailabilityPatternById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        // Arrange
        doThrow(new ResourceNotFound("Availability Pattern not found with id: 99"))
                .when(availabilityPatternService).deleteAvailabilityPatternById(anyLong());

        // Act & Assert
        mvc.perform(delete(BASE_URL + "/delete/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Availability Pattern not found with id: 99"));

        verify(availabilityPatternService).deleteAvailabilityPatternById(anyLong());
        verifyNoMoreInteractions(availabilityPatternService);
    }

    // ==================== AUTHORIZATION TESTS ====================

    @Test
    void findAvailabilityPatternById_shouldReturnForbiddenWhenNotAuthenticatedTest() throws Exception {
        // Arrange - No @WithMockUser annotation, so user is not authenticated

        // Act & Assert
        mvc.perform(get(BASE_URL + "/id/{id}", 3L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(availabilityPatternService);
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void findAvailabilityPatternById_shouldReturnForbiddenWhenInsufficientRoleTest() throws Exception {
        // Arrange - User has PATIENT role, but endpoint requires SECRETARY role

        // Act & Assert
        mvc.perform(get(BASE_URL + "/id/{id}", 3L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(availabilityPatternService);
    }

    // ==================== HELPER DATA ====================

    private static final AvailabilityPatternResponseDto expectedResponseDto =
            new AvailabilityPatternResponseDto(
                    3L,
                    10L,
                    "Dr. Martin Guzman- Clinica del Sur",
                    DayOfWeek.MONDAY,
                    LocalTime.of(8, 0),
                    LocalTime.of(17, 0),
                    true
            );

    private static final AvailabilityPatternCreateDto createDto =
            new AvailabilityPatternCreateDto(
                    10L,
                    DayOfWeek.MONDAY,
                    LocalTime.of(8, 0),
                    LocalTime.of(17, 0)
            );

    private static final AvailabilityPatternUpdateDto updateDto =
            new AvailabilityPatternUpdateDto(
                    LocalTime.of(9, 15),
                    LocalTime.of(19, 0)
            );
}
