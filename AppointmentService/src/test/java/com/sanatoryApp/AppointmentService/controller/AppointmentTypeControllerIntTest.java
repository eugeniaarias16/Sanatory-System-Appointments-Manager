package com.sanatoryApp.AppointmentService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeCreateDto;
import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeUpdateDto;
import com.sanatoryApp.AppointmentService.entity.AppointmentType;
import com.sanatoryApp.AppointmentService.repository.IAppointmentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase
public class AppointmentTypeControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private IAppointmentTypeRepository appointmentTypeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/appointmentTypes";

    @BeforeEach
    void cleanDB() {
        appointmentTypeRepository.deleteAll();
    }

    // ==================== GET TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeByIdTest() throws Exception {
        AppointmentType appointmentType = createAppointmentType();

        mvc.perform(get(BASE_URL + "/{id}", appointmentType.getId()).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Standard consultation"))
                .andExpect(jsonPath("$.name").value("General Consultation"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        mvc.perform(get(BASE_URL + "/{id}", 99L).accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Appointment Type not found with id 99"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeByNameTest() throws Exception {
        createAppointmentType();
        mvc.perform(get(BASE_URL + "/name/{name}", "General Consultation").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Standard consultation"))
                .andExpect(jsonPath("$.name").value("General Consultation"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeByName_shouldReturnNotFoundWhenNameDoesNotExistTest() throws Exception {
        mvc.perform(get(BASE_URL + "/name/{name}", "NonExistent").accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Appointment Type not found with name NonExistent"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeByLikeNameTest() throws Exception {
        createAppointmentType();
        mvc.perform(get(BASE_URL + "/search/name").param("name", "General").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("General Consultation"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeByLikeName_shouldReturnEmptyListWhenNoResultsFoundTest() throws Exception {
        mvc.perform(get(BASE_URL + "/search/name").param("name", "xyz").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeByLikeName_shouldReturnEmptyListWhenNameIsEmptyTest() throws Exception {
        mvc.perform(get(BASE_URL + "/search/name").param("name", "").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeByRangePriceTest() throws Exception {
        createAppointmentType();
        mvc.perform(get(BASE_URL + "/search/price-range")
                        .param("minPrice", "50")
                        .param("maxPrice", "200")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("General Consultation"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeByRangePrice_shouldReturnBadRequestWhenMinPriceIsNullTest() throws Exception {
        mvc.perform(get(BASE_URL + "/search/price-range")
                        .param("maxPrice", "200")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeByRangePrice_shouldReturnBadRequestWhenMaxPriceIsNullTest() throws Exception {
        mvc.perform(get(BASE_URL + "/search/price-range")
                        .param("minPrice", "50")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeByRangePrice_shouldReturnBadRequestWhenMinPriceLessThanOneTest() throws Exception {
        mvc.perform(get(BASE_URL + "/search/price-range")
                        .param("minPrice", "0")
                        .param("maxPrice", "100")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Min Price must be at least 1"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void findAppointmentTypeByRangePrice_shouldReturnBadRequestWhenMinGreaterThanMaxTest() throws Exception {
        mvc.perform(get(BASE_URL + "/search/price-range")
                        .param("minPrice", "200")
                        .param("maxPrice", "100")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Minimum price cannot be greater than maximum price. Min: 200, Max: 100"));
    }

    // ==================== POST TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAppointmentTypeTest() throws Exception {

        // Act & Assert
        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("General Consultation"));

        // Verify persistence in database
        AppointmentType inDB = appointmentTypeRepository.findAll().stream()
                .filter(ap -> "General Consultation".equals(ap.getName()))
                .filter(ap -> "Standard consultation".equals(ap.getDescription()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Appointment Type not persisted in the database."));

        assertThat(inDB.getDurationMin()).isEqualTo(15);
        assertThat(inDB.getBufferTimeMin()).isEqualTo(5);
        assertThat(inDB.getBasePrice()).isEqualByComparingTo(new BigDecimal("100"));
        assertThat(inDB.isActive()).isTrue();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAppointmentType_shouldReturnBadRequestWhenNameAlreadyExistsTest() throws Exception {
        // Arrange - persist entity with same name first
        createAppointmentType();
        long countBefore = appointmentTypeRepository.count();

        // Act & Assert
        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Appointment Type already exist with name General Consultation"));

        assertThat(appointmentTypeRepository.count()).isEqualTo(countBefore);
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAppointmentType_shouldReturnBadRequestWhenDurationMinIsInvalidTest() throws Exception {
        AppointmentTypeCreateDto invalidDto = new AppointmentTypeCreateDto(
                "General Consultation", "Standard consultation", 5, 5, new BigDecimal("100"));

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Duration must be at least 15 minutes"));

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAppointmentType_shouldReturnBadRequestWhenBufferTimeIsNegativeTest() throws Exception {
        AppointmentTypeCreateDto invalidDto = new AppointmentTypeCreateDto(
                "General Consultation", "Standard consultation", 15, -1, new BigDecimal("100"));

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Buffer Time must be positive."));

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createAppointmentType_shouldReturnBadRequestWhenBasePriceIsInvalidTest() throws Exception {
        AppointmentTypeCreateDto invalidDto = new AppointmentTypeCreateDto(
                "General Consultation", "Standard consultation", 15, 5, new BigDecimal("0"));

        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Price must be at least 1.00"));

    }

    // ==================== PATCH TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAppointmentTypeTest() throws Exception {
        // Arrange
        AppointmentType saved = createAppointmentType();
        AppointmentTypeUpdateDto updateDto = new AppointmentTypeUpdateDto("Updated Consultation", null, null, null, null);

        // Act & Assert
        mvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Updated Consultation"));

        // Verify persistence in database
        AppointmentType inDB = appointmentTypeRepository.findById(saved.getId())
                .orElseThrow(() -> new AssertionError("Appointment Type not found in the database."));
        assertThat(inDB.getName()).isEqualTo("Updated Consultation");
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAppointmentType_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        AppointmentTypeUpdateDto updateDto = new AppointmentTypeUpdateDto("Updated Consultation", null, null, null, null);

        mvc.perform(patch(BASE_URL + "/{id}", 99L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Appointment Type not found with id 99"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAppointmentType_shouldReturnBadRequestWhenNameAlreadyExistsTest() throws Exception {
        // Arrange - two entities with different names
        createAppointmentType(); // "General Consultation"
        AppointmentType second = createAppointmentTypeWithName("Specialist Consultation");

        // Try to update second with name already used by first
        AppointmentTypeUpdateDto updateDto = new AppointmentTypeUpdateDto("General Consultation", null, null, null, null);

        mvc.perform(patch(BASE_URL + "/{id}", second.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Appointment Type already exists with name: General Consultation"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAppointmentType_shouldReturnBadRequestWhenDurationMinIsInvalidTest() throws Exception {
        AppointmentType saved = createAppointmentType();
        AppointmentTypeUpdateDto invalidDto = new AppointmentTypeUpdateDto(null, null, 5, null, null);

        mvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Duration must be at least 15 minutes"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAppointmentType_shouldReturnBadRequestWhenBufferTimeIsNegativeTest() throws Exception {
        AppointmentType saved = createAppointmentType();
        AppointmentTypeUpdateDto invalidDto = new AppointmentTypeUpdateDto(null, null, null, -1, null);

        mvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Buffer Time must be positive."));

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAppointmentType_shouldReturnBadRequestWhenBasePriceIsInvalidTest() throws Exception {
        AppointmentType saved = createAppointmentType();
        AppointmentTypeUpdateDto invalidDto = new AppointmentTypeUpdateDto(null, null, null, null, new BigDecimal("0"));

        mvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Price must be at least 1.00"));
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateAppointmentType_shouldReturnOkWhenAllFieldsAreNullTest() throws Exception {
        AppointmentType saved = createAppointmentType();
        AppointmentTypeUpdateDto updateDto = new AppointmentTypeUpdateDto(null, null, null, null, null);

        mvc.perform(patch(BASE_URL + "/{id}", saved.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("General Consultation"))
                .andExpect(jsonPath("$.description").value("Standard consultation"));
    }

    // ==================== DELETE TESTS ====================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteAppointmentTypeByIdTest() throws Exception {
        AppointmentType saved = createAppointmentType();

        mvc.perform(delete(BASE_URL + "/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        // Verify soft delete in database
        AppointmentType inDB = appointmentTypeRepository.findById(saved.getId())
                .orElseThrow(() -> new AssertionError("Appointment Type not found in the database."));
        assertThat(inDB.isActive()).isFalse();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteAppointmentTypeById_shouldReturnNotFoundWhenIdDoesNotExistTest() throws Exception {
        mvc.perform(delete(BASE_URL + "/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Appointment Type not found with id 99"));
    }

    // ==================== AUTHORIZATION TESTS ====================

    @Test
    void findAppointmentTypeById_shouldReturnForbiddenWhenNotAuthenticatedTest() throws Exception {
        mvc.perform(get(BASE_URL + "/{id}", 1L).accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void createAppointmentType_shouldReturnForbiddenWhenNotAuthenticatedTest() throws Exception {
        mvc.perform(post(BASE_URL)
                        .accept(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateAppointmentType_shouldReturnForbiddenWhenNotAuthenticatedTest() throws Exception {
        AppointmentTypeUpdateDto updateDto = new AppointmentTypeUpdateDto("Updated Consultation", null, null, null, null);
        mvc.perform(patch(BASE_URL + "/{id}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAppointmentTypeById_shouldReturnForbiddenWhenNotAuthenticatedTest() throws Exception {
        mvc.perform(delete(BASE_URL + "/{id}", 1L))
                .andExpect(status().isForbidden());
    }

    // ==================== HELPER METHODS ====================

    private AppointmentType createAppointmentType() {
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setName("General Consultation");
        appointmentType.setDescription("Standard consultation");
        appointmentType.setDurationMin(15);
        appointmentType.setBufferTimeMin(5);
        appointmentType.setBasePrice(new BigDecimal(100));
        appointmentType.setActive(true);
        return appointmentTypeRepository.save(appointmentType);
    }

    private AppointmentType createAppointmentTypeWithName(String name) {
        AppointmentType appointmentType = new AppointmentType();
        appointmentType.setName(name);
        appointmentType.setDescription("Standard consultation");
        appointmentType.setDurationMin(15);
        appointmentType.setBufferTimeMin(5);
        appointmentType.setBasePrice(new BigDecimal(100));
        appointmentType.setActive(true);
        return appointmentTypeRepository.save(appointmentType);
    }

    private static final AppointmentTypeCreateDto createDto = new AppointmentTypeCreateDto(
            "General Consultation",
            "Standard consultation",
            15,
            5,
            new BigDecimal(100));
}
