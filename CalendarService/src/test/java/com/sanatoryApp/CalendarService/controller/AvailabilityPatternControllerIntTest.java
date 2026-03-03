package com.sanatoryApp.CalendarService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternUpdateDto;
import com.sanatoryApp.CalendarService.entity.AvailabilityPattern;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.repository.IAvailabilityPatternRepository;
import com.sanatoryApp.CalendarService.repository.IDoctorCalendarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class AvailabilityPatternControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private IAvailabilityPatternRepository availabilityPatternRepository;

    @Autowired
    private IDoctorCalendarRepository doctorCalendarRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/availabilityPattern";

    @BeforeEach
    void cleanDB() {
        availabilityPatternRepository.deleteAll();
        doctorCalendarRepository.deleteAll();
    }

    // ==================== POST TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAvailabilityPatternTest() throws Exception {
        // Arrange - Create and save DoctorCalendar in database
        DoctorCalendar dc = createDC();

        AvailabilityPatternCreateDto createDto = new AvailabilityPatternCreateDto(
                dc.getId(),
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(17, 0)
        );

        // Act & Assert
        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.doctorCalendarId").value(dc.getId()))
                .andExpect(jsonPath("$.doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.startTime").value("08:00:00"))
                .andExpect(jsonPath("$.endTime").value("17:00:00"));

        // Verify persistence in database
        AvailabilityPattern inDB = availabilityPatternRepository.findAll().stream()
                .filter(ap -> dc.getId().equals(ap.getDoctorCalendar().getId()))
                .filter(ap -> DayOfWeek.MONDAY.equals(ap.getDayOfWeek()))
                .filter(ap -> LocalTime.of(8, 0).equals(ap.getStartTime()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Availability Pattern not persisted in the database."));

        assertThat(inDB.getDoctorCalendar().getId()).isEqualTo(dc.getId());
        assertThat(inDB.getStartTime()).isEqualTo(LocalTime.of(8, 0));
        assertThat(inDB.getEndTime()).isEqualTo(LocalTime.of(17, 0));
        assertThat(inDB.isActive()).isTrue();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAvailabilityPattern_shouldReturnNotFoundWhenDoctorCalendarDoesNotExistTest() throws Exception {
        // Arrange
        AvailabilityPatternCreateDto createDto = new AvailabilityPatternCreateDto(
                99L,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(17, 0)
        );
        Long before=availabilityPatternRepository.count();

        // Act & Assert
        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Doctor Calendar not found with id 99"));

        assertThat(availabilityPatternRepository.count()).isEqualTo(before);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAvailabilityPattern_shouldReturnBadRequestWhenTimeRangeIsInvalidTest() throws Exception {
        // Arrange
        AvailabilityPatternCreateDto createDto = new AvailabilityPatternCreateDto(
                99L,
                DayOfWeek.MONDAY,
                LocalTime.of(18, 0),
                LocalTime.of(7, 0)
        );

        Long before=availabilityPatternRepository.count();

        // Act & Assert
        mvc.perform(post(BASE_URL)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto))
                .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("End time must be after start time."));

        assertThat(availabilityPatternRepository.count()).isEqualTo(before);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAvailabilityPattern_shouldReturnBadRequestWhenOverlappingPatternExistsTest() throws Exception {
        //Arrange
        DoctorCalendar doctorCalendar=createDC();

        AvailabilityPattern existingAP=createAP();
        AvailabilityPatternCreateDto createDto = new AvailabilityPatternCreateDto(
                doctorCalendar.getId(),
                DayOfWeek.MONDAY,
                LocalTime.of(7, 0),
                LocalTime.of(18, 0)
        );
        Long before=availabilityPatternRepository.count();

        // Act & Assert
        mvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Schedule conflict: The new time slot overlaps with an existing schedule. " +
                        "The doctor cannot have overlapping schedules across any of their calendars."));

        assertThat(availabilityPatternRepository.count()).isEqualTo(before);

    }

    // ==================== GET TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAvailabilityPatternByIdTest() throws Exception {
        // Arrange
        AvailabilityPattern existingAP = createAP();

        // Act & Assert
        mvc.perform(get(BASE_URL + "/id/{id}", existingAP.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(existingAP.getId()))
                .andExpect(jsonPath("$.doctorCalendarId").value(existingAP.getDoctorCalendar().getId()))
                .andExpect(jsonPath("$.doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"))
                .andExpect(jsonPath("$.dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$.startTime").value("08:00:00"))
                .andExpect(jsonPath("$.endTime").value("17:00:00"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAvailabilityPatternById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        // Act & Assert
        mvc.perform(get(BASE_URL + "/id/{id}", 99L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Availability Pattern not found with id: 99"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAvailabilityPatternByDoctorCalendarIdTest() throws Exception {
        // Arrange
        AvailabilityPattern existingAP = createAP();

        // Act & Assert
        mvc.perform(get(BASE_URL + "/doctorCalendar/{id}", existingAP.getDoctorCalendar().getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(existingAP.getId()))
                .andExpect(jsonPath("$[0].doctorCalendarId").value(existingAP.getDoctorCalendar().getId()))
                .andExpect(jsonPath("$[0].doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"))
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$[0].startTime").value("08:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("17:00:00"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAvailabilityPatternByDoctorCalendarId_shouldReturnNotFoundWhenCalendarIdDoesNotExistTest() throws Exception {
        // Act & Assert
        mvc.perform(get(BASE_URL + "/doctorCalendar/{id}", 99L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Availability Pattern not found with doctor calendar id: 99"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrueTest() throws Exception {
        // Arrange
        AvailabilityPattern existingAP = createAP();

        // Act & Assert
        mvc.perform(get(BASE_URL + "/search/calendarAndDay")
                        .param("calendarId", String.valueOf(existingAP.getDoctorCalendar().getId()))
                        .param("dayOfWeek", "MONDAY")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(existingAP.getId()))
                .andExpect(jsonPath("$[0].doctorCalendarId").value(existingAP.getDoctorCalendar().getId()))
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$[0].startTime").value("08:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("17:00:00"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue_shouldReturnEmptyListTest() throws Exception {
        // Arrange
        AvailabilityPattern existingAP = createAP(); // MONDAY pattern

        // Act & Assert - Search for TUESDAY (no patterns exist)
        mvc.perform(get(BASE_URL + "/search/calendarAndDay")
                        .param("calendarId", String.valueOf(existingAP.getDoctorCalendar().getId()))
                        .param("dayOfWeek", "TUESDAY")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdTest() throws Exception {
        // Arrange
        AvailabilityPattern existingAP = createAP();

        // Act & Assert
        mvc.perform(get(BASE_URL + "/doctor/{doctorId}", existingAP.getDoctorCalendar().getDoctorId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(existingAP.getId()))
                .andExpect(jsonPath("$[0].doctorCalendarId").value(existingAP.getDoctorCalendar().getId()))
                .andExpect(jsonPath("$[0].doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"))
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$[0].startTime").value("08:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("17:00:00"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorId_shouldReturnEmptyListTest() throws Exception {
        // Act & Assert - Search with non-existent doctorId
        mvc.perform(get(BASE_URL + "/doctor/{doctorId}", 99L)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndDayTest() throws Exception {
        // Arrange
        AvailabilityPattern existingAP = createAP();

        // Act & Assert
        mvc.perform(get(BASE_URL + "/doctor/{doctorId}/day/{dayOfWeek}",
                                existingAP.getDoctorCalendar().getDoctorId(),
                                "MONDAY")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(existingAP.getId()))
                .andExpect(jsonPath("$[0].doctorCalendarId").value(existingAP.getDoctorCalendar().getId()))
                .andExpect(jsonPath("$[0].doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"))
                .andExpect(jsonPath("$[0].dayOfWeek").value("MONDAY"))
                .andExpect(jsonPath("$[0].startTime").value("08:00:00"))
                .andExpect(jsonPath("$[0].endTime").value("17:00:00"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findByDoctorIdAndDay_shouldReturnEmptyListTest() throws Exception {
        // Arrange
        AvailabilityPattern existingAP = createAP(); // MONDAY pattern

        // Act & Assert - Search for TUESDAY (no patterns exist)
        mvc.perform(get(BASE_URL + "/doctor/{doctorId}/day/{dayOfWeek}",
                                existingAP.getDoctorCalendar().getDoctorId(),
                                "TUESDAY")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    // ==================== PATCH TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAvailabilityPatternByIdTest() throws Exception {
        // Arrange
        AvailabilityPattern existingAP = createAP();

        AvailabilityPatternUpdateDto updateDto = new AvailabilityPatternUpdateDto(
                LocalTime.of(9, 15),
                LocalTime.of(19, 0)
        );

        // Act & Assert
        mvc.perform(patch(BASE_URL + "/update/{id}", existingAP.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.doctorCalendarId").value(existingAP.getDoctorCalendar().getId()))
                .andExpect(jsonPath("$.doctorCalendarName").value("Dr. Martin Guzman- Clinica del Sur"))
                .andExpect(jsonPath("$.startTime").value("09:15:00"))
                .andExpect(jsonPath("$.endTime").value("19:00:00"));

        // Verify persistence in database
        AvailabilityPattern inDB = availabilityPatternRepository.findById(existingAP.getId())
                .orElseThrow(() -> new AssertionError("Availability Pattern not found in the database."));

        assertThat(inDB.getStartTime()).isEqualTo(LocalTime.of(9, 15));
        assertThat(inDB.getEndTime()).isEqualTo(LocalTime.of(19, 0));
        assertThat(inDB.isActive()).isTrue();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAvailabilityPatternById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {

        AvailabilityPatternUpdateDto updateDto = new AvailabilityPatternUpdateDto(
                LocalTime.of(9, 15),
                LocalTime.of(19, 0)
        );
        // Act & Assert
        mvc.perform(patch(BASE_URL + "/update/{id}", 99L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Availability Pattern not found with id: 99"));

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAvailabilityPatternById_shouldReturnBadRequestWhenTimeRangeIsInvalidTest() throws Exception {
        // Arrange
        AvailabilityPattern existingAP = createAP();

        AvailabilityPatternUpdateDto badUpdateDto = new AvailabilityPatternUpdateDto(
                LocalTime.of(18, 0),
                LocalTime.of(9, 0)
        );
        // Act & Assert - InvalidTimeRangeException returns 400 BAD_REQUEST (GlobalExceptionHandler line 40)
        mvc.perform(patch(BASE_URL + "/update/{id}", existingAP.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badUpdateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("End time must be after start time."));


    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAvailabilityPatternById_shouldReturnBadRequestWhenOverlappingPatternExistsTest() throws Exception {
        // Arrange- To test for overlap,there must be TWO different patterns for the same doctor on the same day.

        AvailabilityPattern existingAP1 = createAP();

        AvailabilityPattern AP2 =new AvailabilityPattern();
        AP2.setDoctorCalendar(existingAP1.getDoctorCalendar());
        AP2.setDayOfWeek(DayOfWeek.MONDAY);
        AP2.setStartTime(LocalTime.of(18,00));
        AP2.setEndTime(LocalTime.of(19,30));
        AP2.setActive(true);

        AvailabilityPattern existingAP2=availabilityPatternRepository.save(AP2);


        AvailabilityPatternUpdateDto updateDto = new AvailabilityPatternUpdateDto(
                LocalTime.of(10, 15),
                LocalTime.of(18, 0)
        );

        // Act & Assert
        mvc.perform(patch(BASE_URL + "/update/{id}", existingAP2.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Schedule conflict: The updated time slot overlaps with an existing schedule."));

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeleteAvailabilityPatternByIdTest() throws Exception {
        //Arrange
        AvailabilityPattern existingAP = createAP();
        assertThat(existingAP.isActive()).isTrue();

        // Act & Assert
        mvc.perform(patch(BASE_URL + "/softDelete/{id}", existingAP.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Availability Pattern with id "+existingAP.getId()+ " successfully deactivated."));

        AvailabilityPattern updatedAP=availabilityPatternRepository.findById(existingAP.getId())
                .orElseThrow(() -> new ResourceNotFound("Availability Pattern not found with id: " + existingAP.getId()));

        assertThat(updatedAP.isActive()).isFalse();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void softDeleteAvailabilityPatternById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {

        // Act & Assert
        mvc.perform(patch(BASE_URL + "/softDelete/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Availability Pattern not found with id: 99"));

    }

    // ==================== DELETE TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteAvailabilityPatternByIdTest() throws Exception {

        AvailabilityPattern existingAP=createAP();
        assertThat(availabilityPatternRepository.existsById(existingAP.getId())).isTrue(); //verify that already exists in BD

        mvc.perform(delete(BASE_URL + "/delete/{id}", existingAP.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Availability Pattern with id "+existingAP.getId()+" successfully deleted."));

        assertThat(availabilityPatternRepository.existsById(existingAP.getId())).isFalse(); //verify that it no longer exists in the BD

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteAvailabilityPatternById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        // Act & Assert
        mvc.perform(delete(BASE_URL + "/delete/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Availability Pattern not found with id: 99"));

        assertThat(availabilityPatternRepository.existsById(99L)).isFalse();

    }

    // ==================== AUTHORIZATION TESTS ====================

    @Test
    void findAvailabilityPatternById_shouldReturnForbiddenWhenNotAuthenticatedTest() throws Exception {

     AvailabilityPattern existingAP=createAP();
        mvc.perform(get(BASE_URL + "/id/{id}", existingAP.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void findAvailabilityPatternById_shouldReturnForbiddenWhenInsufficientRoleTest() throws Exception {
        AvailabilityPattern existingAP=createAP();
        mvc.perform(get(BASE_URL + "/id/{id}", existingAP.getId())
                        .accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    // ==================== HELPER METHODS ====================

    private DoctorCalendar createDC() {
        DoctorCalendar doctorCalendar = new DoctorCalendar();
        doctorCalendar.setDoctorId(1L);
        doctorCalendar.setName("Dr. Martin Guzman- Clinica del Sur");
        doctorCalendar.setTimeZone(ZoneId.of("America/Argentina/Buenos_Aires"));
        doctorCalendar.setActive(true);

        return doctorCalendarRepository.save(doctorCalendar);
    }

    private AvailabilityPattern createAP() {
        DoctorCalendar doctorCalendar = createDC();

        AvailabilityPattern availabilityPattern = new AvailabilityPattern();
        availabilityPattern.setDoctorCalendar(doctorCalendar);
        availabilityPattern.setStartTime(LocalTime.of(8, 0));
        availabilityPattern.setEndTime(LocalTime.of(17, 0));
        availabilityPattern.setDayOfWeek(DayOfWeek.MONDAY);
        availabilityPattern.setActive(true);

        return availabilityPatternRepository.save(availabilityPattern);
    }
}
