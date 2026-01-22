package com.sanatoryApp.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.UserService.dto.Request.DoctorCreateDto;
import com.sanatoryApp.UserService.dto.Request.DoctorUpdateDto;
import com.sanatoryApp.UserService.entity.Doctor;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.repository.IDoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class DoctorControllerIntTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private IDoctorRepository repository;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/doctor";

    //clean BD
    @BeforeEach
    void cleanDB() {
        repository.deleteAll();
    }

    /* =================== POST ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctorTest() throws Exception {
        DoctorCreateDto createDto = new DoctorCreateDto("Ana", "Lopez", "ana_lopez@gmail.com", "20111222", "+544321567");

        mvc.perform(post(BASE_URL).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDto)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.dni").value("20111222"));

        Doctor inBD = repository.findAll().stream()
                .filter(d -> "Ana".equals(d.getFirstName()))
                .filter(d -> "20111222".equals(d.getDni()))
                .filter(d -> "ana_lopez@gmail.com".equals(d.getEmail()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Doctor not persisted in the database."));

        assertThat(inBD.getDni()).isEqualTo("20111222");
        assertThat(inBD.getEmail()).isEqualTo("ana_lopez@gmail.com");
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createDoctor_withDuplicateEmailTest() throws Exception {
        //Create Doctor
        Doctor existingDoctor=createDoctor();
        String email="ana_lopez@gmail.com";
        assertThat(existingDoctor.getEmail()).isEqualTo(email);

        long before=repository.count();

        DoctorCreateDto createDto = new DoctorCreateDto("Ana" , "Lopez", email, "20111223", "+544321563");

        mvc.perform(post(BASE_URL).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Doctor already exists with email: "+email));

        //verify that the DB hasn't change
        assertThat(repository.count()).isEqualTo(before);
    }
    /* =================== PUT/PATCH ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateDoctorByIdTest() throws Exception {

        //Creating Doctor in BD
        Doctor existingDoctor = createDoctor();
        //verifying if exists
        assertThat(repository.existsByDni("20111222")).isTrue();
        assertThat(repository.existsByEmail("ana_lopez@gmail.com")).isTrue();

        DoctorUpdateDto updateDto = new DoctorUpdateDto("Susana", "Rodriguez", "susana_rodriguez@gmail.com", null, null);

        //Act
        mvc.perform(put(BASE_URL + "/update/{doctorId}", existingDoctor.getId()).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.phoneNumber").value("+544321567"))
                .andExpect(jsonPath("$.firstName").value("Susana"))
                .andExpect(jsonPath("$.dni").value("20111222"));

        Doctor inBD = repository.findById(existingDoctor.getId())
                .orElseThrow(() -> new ResourceNotFound("Doctor not found with id: " + existingDoctor.getId()));
        assertThat(inBD.getEmail()).isEqualTo("susana_rodriguez@gmail.com");
        assertThat(inBD.getFirstName()).isEqualTo("Susana");
        assertThat(inBD.getLastName()).isEqualTo("Rodriguez");
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void updateDoctorById_forbiddenByPreAuthorizeTest() throws Exception {
        //Creating Doctor in BD
        Doctor existingDoctor = createDoctor();
        //verifying if exists
        String dni="20111222";
        assertThat(repository.existsByDni(dni)).isTrue();

        String email="ana_lopez@gmail.com";
        assertThat(existingDoctor.getEmail()).isEqualTo(email);

        DoctorUpdateDto updateDto = new DoctorUpdateDto(null, null, "ana@gmail.com",null , null);
        mvc.perform(put(BASE_URL+"/update/{doctorId}", existingDoctor.getId())
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
        Doctor inBD=repository.findDoctorByDni(dni)
                        .orElseThrow(()->new ResourceNotFound("Doctor not found with dni: "+dni));
        assertThat(inBD.getEmail()).isEqualTo(email);
        assertThat(inBD.getLastName()).isEqualTo(existingDoctor.getLastName());
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateInexistentDoctorById() throws Exception{
        DoctorUpdateDto updateDto = new DoctorUpdateDto(null, null, "ana@gmail.com",null , null);
        mvc.perform(put(BASE_URL+"/update/{doctorId}", 1L).accept(APPLICATION_JSON).contentType(APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor not found with id: 1"));
        assertThat(repository.existsById(1L)).isFalse();
    }
    /* =================== DELETE ENDPOINTS =================== */

    @Test
    @WithMockUser(roles = "SECRETARY")
    void disableDoctorByDniTest() throws Exception {
        //Creating Doctor in BD
        Doctor existingDoctor = createDoctor();
        existingDoctor.setEnabled(true);
        repository.save(existingDoctor);
        //verifying if exists
        assertThat(repository.existsByDni("20111222")).isTrue();
        //verifying if is enabled
        assertThat(existingDoctor.isEnabled()).isTrue();


        mvc.perform(patch(BASE_URL + "/disable/{dni}", existingDoctor.getDni()).accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Doctor with dni " + existingDoctor.getDni() + " successfully disabled."));

        Doctor inBD = repository.findDoctorByDni(existingDoctor.getDni())
                .orElseThrow(() -> new ResourceNotFound("Doctor not found with dni: " + existingDoctor.getDni()));

        assertThat(inBD.isEnabled()).isFalse();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void disableInexistentDoctorByDniTest() throws Exception {
        String dni="20111222";
        mvc.perform(patch(BASE_URL + "/disable/{dni}", dni).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Doctor with DNI " + dni + " not found"));

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void enableDoctorByDniTest() throws Exception {
        //Creating Doctor in BD
        Doctor existingDoctor = createDoctor();
        //disable doctor
        existingDoctor.setEnabled(false);
        repository.save(existingDoctor);
        //verifying if exists
        assertThat(repository.existsByDni("20111222")).isTrue();
        //verifying if is disabled
        assertThat(existingDoctor.isEnabled()).isFalse();

        mvc.perform(patch(BASE_URL + "/enable/{dni}", existingDoctor.getDni()).accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Doctor with dni " + existingDoctor.getDni() + " successfully enabled."));

        Doctor inBD = repository.findDoctorByDni(existingDoctor.getDni())
                .orElseThrow(() -> new ResourceNotFound("Doctor not found with dni: " + existingDoctor.getDni()));

        assertThat(inBD.isEnabled()).isTrue();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteDoctorByIdTest() throws Exception {
        //Creating Doctor in BD
        Doctor existingDoctor = createDoctor();
        //verifying if exists
        assertThat(repository.existsByDni("20111222")).isTrue();

        mvc.perform(delete(BASE_URL + "/{id}", existingDoctor.getId()).accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Doctor with id " + existingDoctor.getId() + "successfully deleted."));


        assertThat(repository.existsByDni(existingDoctor.getDni())).isFalse();
    }


    //helper creating doctor
    private Doctor createDoctor() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("Ana");
        doctor.setLastName("Lopez");
        doctor.setEmail("ana_lopez@gmail.com");
        doctor.setDni("20111222");
        doctor.setPhoneNumber("+544321567");
        doctor.setPassword("encoded-password");

        return repository.save(doctor);
    }

}
