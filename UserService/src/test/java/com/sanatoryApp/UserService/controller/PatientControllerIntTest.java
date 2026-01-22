package com.sanatoryApp.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.UserService.dto.Request.PatientCreateDto;
import com.sanatoryApp.UserService.dto.Request.PatientUpdateDto;
import com.sanatoryApp.UserService.entity.Patient;
import com.sanatoryApp.UserService.repository.IPatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import java.nio.charset.StandardCharsets;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class PatientControllerIntTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private IPatientRepository repository;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL="/patient";

    // cleaning DB
    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }
    // =================== POST ENDPOINTS TESTS ===================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createPatientTest()throws Exception{
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Rodriguez", "34567897", "maria_rodriguez@gmail.com", "+545679832");


        MvcResult result=mvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDto)).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        Patient inBD=repository.findAll().stream()
                .filter(p->"Maria".equals(p.getFirstName()))
                .filter(p->"Rodriguez".equals(p.getLastName()))
                .filter(p->"34567897".equals(p.getDni()))
                .findFirst()
                .orElseThrow(()->new AssertionError("Patient not Found in BD"));

        assertThat(inBD.getId()).isNotNull();
        assertThat(inBD.getFirstName()).isEqualTo("Maria");
        assertThat(inBD.getEmail()).isEqualTo("maria_rodriguez@gmail.com");
    }

    // =================== PUT/PATCH ENDPOINTS TESTS ===================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updatePatientByIdTest()throws Exception{


        //Existent Patient
        Patient patient = new Patient();
        patient.setFirstName("Angela");
        patient.setLastName("Rodriguez");
        patient.setDni("34567897");
        patient.setEmail("maria_rodriguez@gmail.com");
        patient.setPhoneNumber("+545679832");
        patient.setPassword("encoded-pass");
        //Persist Patient
        Patient savedPatient=repository.save(patient);
        Long patientId=savedPatient.getId();

        //Updating information
        PatientUpdateDto updateDto = new PatientUpdateDto(null, "Melina", "Rodriguez", null, null);

        mvc.perform(put(BASE_URL+"/update/id/{patientId}",patientId).content(objectMapper.writeValueAsString(updateDto)).contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Melina"))
                .andExpect(jsonPath("$.lastName").value("Rodriguez"))
                .andExpect(jsonPath("$.email").value("maria_rodriguez@gmail.com"));


        Patient inBD=repository.findById(patientId)
                .orElseThrow(()->new AssertionError("Patient not Found with id "+patientId));

        assertThat(inBD.getFirstName()).isEqualTo("Melina");
        assertThat(inBD.getDni()).isEqualTo("34567897");
    }


    @Test
    @WithMockUser(roles = "SECRETARY")
    void updatePatientByDniTest()throws Exception{
        //Existent Patient
        Patient patient = new Patient();
        patient.setFirstName("Angela");
        patient.setLastName("Rodriguez");
        patient.setDni("34567897");
        patient.setEmail("maria_rodriguez@gmail.com");
        patient.setPhoneNumber("+545679832");
        patient.setPassword("encoded-pass");
        //Persist Patient
        Patient savedPatient=repository.save(patient);
        Long patientId=savedPatient.getId();

        //Updating information
        PatientUpdateDto updateDto = new PatientUpdateDto(null, "Melina", "Rodriguez", null, null);

        mvc.perform(put(BASE_URL+"/update/dni/{dni}",savedPatient.getDni()).content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON).characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Melina"))
                .andExpect(jsonPath("$.lastName").value("Rodriguez"))
                .andExpect(jsonPath("$.email").value("maria_rodriguez@gmail.com"));


        Patient inBD=repository.findById(patientId)
                .orElseThrow(()->new AssertionError("Patient not Found with id "+patientId));

        assertThat(inBD.getFirstName()).isEqualTo("Melina");
        assertThat(inBD.getDni()).isEqualTo("34567897");
    }



    @Test
    @WithMockUser(roles = "SECRETARY")
    void enablePatientByDniTest()throws Exception{
        //Existent Patient
        Patient patient = new Patient();
        patient.setFirstName("Angela");
        patient.setLastName("Rodriguez");
        patient.setDni("34567897");
        patient.setEmail("maria_rodriguez@gmail.com");
        patient.setPhoneNumber("+545679832");
        patient.setPassword("encoded-pass");
        patient.setEnabled(false); //disable patient
        //Persist Patient
        Patient savedPatient=repository.save(patient);

        //verify if saved patient is disabled
        assertThat(savedPatient.isEnabled()).isFalse();

        mvc.perform(patch(BASE_URL+"/enable/{dni}",savedPatient.getDni())
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient with dni "+savedPatient.getDni()+" successfully enabled."));


        Patient inBd=repository.findPatientByDni(savedPatient.getDni())
                .orElseThrow(()->new AssertionError("Patient not Foud with dni: "+savedPatient.getDni()));
        boolean isEnabled=inBd.isEnabled();

        //verify if patient is  enabled
        assertThat(isEnabled).isTrue();

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void disablePatientByDniTest()throws Exception{
        //Existent Patient
        Patient patient = new Patient();
        patient.setFirstName("Angela");
        patient.setLastName("Rodriguez");
        patient.setDni("34567897");
        patient.setEmail("maria_rodriguez@gmail.com");
        patient.setPhoneNumber("+545679832");
        patient.setPassword("encoded-pass");

        Patient savedPatient=repository.save(patient);
        assertThat(savedPatient.isEnabled()).isTrue();

        mvc.perform(patch(BASE_URL+"/disable/{dni}",34567897).accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient with dni "+savedPatient.getDni()+" successfully disabled."))
                .andReturn();

        Patient inBd=repository.findPatientByDni(savedPatient.getDni())
                .orElseThrow(()->new AssertionError("Patient not Foud with dni: "+savedPatient.getDni()));
        boolean isEnabled=inBd.isEnabled();
        assertThat(isEnabled).isFalse();

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deletePatientByIdTest()throws Exception{
        //Existent Patient
        Patient patient = new Patient();
        patient.setFirstName("Angela");
        patient.setLastName("Rodriguez");
        patient.setDni("34567897");
        patient.setEmail("maria_rodriguez@gmail.com");
        patient.setPhoneNumber("+545679832");
        patient.setPassword("encoded-pass");
        //Persist Patient
        Patient savedPatient=repository.save(patient);
        Long patientId=savedPatient.getId();

        mvc.perform(delete(BASE_URL+"/delete/id/{id}",patientId)
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient with id "+patientId+" successfully deleted."));

        //verify
        boolean existsInBD=repository.existsById(patientId);
        assertThat(existsInBD).isFalse();

    }
    // =================== DELETE ENDPOINTS TESTS ===================

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deletePatientByDniTest()throws Exception{
        //Existent Patient
        Patient patient = new Patient();
        patient.setFirstName("Angela");
        patient.setLastName("Rodriguez");
        patient.setDni("34567897");
        patient.setEmail("maria_rodriguez@gmail.com");
        patient.setPhoneNumber("+545679832");
        patient.setPassword("encoded-pass");
        //Persist Patient
        Patient savedPatient=repository.save(patient);


        mvc.perform(delete(BASE_URL+"/delete/dni/{dni}",patient.getDni())
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Patient with dni "+savedPatient.getDni()+" successfully deleted."));

        //verify
        boolean existsInBD=repository.existsByDni(patient.getDni());
        assertThat(existsInBD).isFalse();

    }

}
