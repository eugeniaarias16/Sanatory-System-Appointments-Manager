package com.sanatoryApp.UserService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanatoryApp.UserService.dto.Request.SecretaryCreateDto;
import com.sanatoryApp.UserService.dto.Request.SecretaryUpdateDto;
import com.sanatoryApp.UserService.entity.Secretary;
import com.sanatoryApp.UserService.repository.ISecretaryRepository;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecretaryControllerIntTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ISecretaryRepository repository;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL="/secretary";

    // cleaning DB
    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void createSecretaryTest()throws Exception{
       //Arrange
        SecretaryCreateDto createDto=new SecretaryCreateDto("34525678","Carmen","Garcia","carmen_garcia@gmail.com");

        //Act
        MvcResult result=mvc.perform(post(BASE_URL).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(createDto)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Carmen"))
                .andExpect(jsonPath("$.dni").value("34525678"))
                .andReturn();
        //Assert
        Secretary inBD= repository.findAll().stream()
                .filter(s->"Carmen".equals(s.getFirstName()))
                .filter(s->"34525678".equals(s.getDni()))
                .filter(s->"carmen_garcia@gmail.com".equals(s.getEmail()))
                .findFirst()
                .orElseThrow(()-> new AssertionError("Secretary not persisted in the database."));
        assertThat(inBD.getFirstName()).isEqualTo("Carmen");
        assertThat(inBD.getDni()).isEqualTo("34525678");


    }
    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateSecretaryByIdTest()throws Exception{

        Secretary existingSecretary=new Secretary();
        existingSecretary.setFirstName("Carmen");
        existingSecretary.setLastName("Garcia");
        existingSecretary.setDni("34525678");
        existingSecretary.setEmail("carmen_garcia@gmail.com");
        existingSecretary.setPassword("encoded-password");

        Secretary savedSecretary=repository.save(existingSecretary);
        Long id=savedSecretary.getId();

        SecretaryUpdateDto updateDto = new SecretaryUpdateDto(null, "Antonia", null, "antonia_garcia@gmail.com");

        mvc.perform(patch(BASE_URL+"/update/id/{id}",id).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Antonia"))
                .andExpect(jsonPath("$.email").value("antonia_garcia@gmail.com"))
                .andExpect(jsonPath("$.lastName").value("Garcia"));


        Secretary inBD=repository.findById(id)
                .orElseThrow(()-> new AssertionError("Secretary not persisted in the database."));
        assertThat(inBD.getFirstName()).isEqualTo("Antonia");
        assertThat(inBD.getDni()).isEqualTo("34525678");
        assertThat(inBD.getEmail()).isEqualTo("antonia_garcia@gmail.com");


    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void updateSecretaryByDniTest()throws Exception{

        Secretary existingSecretary=new Secretary();
        existingSecretary.setFirstName("Carmen");
        existingSecretary.setLastName("Garcia");
        existingSecretary.setDni("34525678");
        existingSecretary.setEmail("carmen_garcia@gmail.com");
        existingSecretary.setPassword("encoded-password");

        Secretary savedSecretary=repository.save(existingSecretary);


        SecretaryUpdateDto updateDto = new SecretaryUpdateDto(null, "Antonia", null, "antonia_garcia@gmail.com");

        mvc.perform(patch(BASE_URL+"/update/dni/{dni}",savedSecretary.getDni()).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Antonia"))
                .andExpect(jsonPath("$.lastName").value("Garcia"))
                .andExpect(jsonPath("$.email").value("antonia_garcia@gmail.com"));

        Secretary inBD=repository.findSecretaryByDni(savedSecretary.getDni())
                .orElseThrow(()-> new AssertionError("Secretary not persisted in the database."));
        assertThat(inBD.getFirstName()).isEqualTo("Antonia");
        assertThat(inBD.getDni()).isEqualTo("34525678");
        assertThat(inBD.getEmail()).isEqualTo("antonia_garcia@gmail.com");

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void disableSecretaryByDniTest()throws Exception{

        Secretary existingSecretary=new Secretary();
        existingSecretary.setFirstName("Carmen");
        existingSecretary.setLastName("Garcia");
        existingSecretary.setDni("34525678");
        existingSecretary.setEmail("carmen_garcia@gmail.com");
        existingSecretary.setPassword("encoded-password");

        Secretary savedSecretary=repository.save(existingSecretary);
        assertThat(savedSecretary.isEnabled()).isTrue();

        mvc.perform(patch(BASE_URL+"/disable/{dni}",savedSecretary.getDni())
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Secretary with dni "+savedSecretary.getDni()+" successfully disabled."));

        Secretary inBD=repository.findSecretaryByDni(savedSecretary.getDni())
                .orElseThrow(()-> new AssertionError("Secretary not persisted in the database."));
        assertThat(inBD.isEnabled()).isFalse();
        assertThat(inBD.getDni()).isEqualTo("34525678");

    }
    @Test
    @WithMockUser(roles = "SECRETARY")
    void enableSecretaryByDniTest()throws Exception{

        Secretary existingSecretary=new Secretary();
        existingSecretary.setFirstName("Carmen");
        existingSecretary.setLastName("Garcia");
        existingSecretary.setDni("34525678");
        existingSecretary.setEmail("carmen_garcia@gmail.com");
        existingSecretary.setPassword("encoded-password");
        existingSecretary.setEnabled(false);

        Secretary savedSecretary=repository.save(existingSecretary);
        assertThat(savedSecretary.isEnabled()).isFalse();

        mvc.perform(patch(BASE_URL+"/enable/{dni}",savedSecretary.getDni())
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Secretary with dni "+savedSecretary.getDni()+" successfully enabled."));

        Secretary inBD=repository.findSecretaryByDni(savedSecretary.getDni())
                .orElseThrow(()-> new AssertionError("Secretary not persisted in the database."));
        assertThat(inBD.isEnabled()).isTrue();
        assertThat(inBD.getDni()).isEqualTo("34525678");

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteSecretaryByDniTest()throws Exception{

        Secretary existingSecretary=new Secretary();
        existingSecretary.setFirstName("Carmen");
        existingSecretary.setLastName("Garcia");
        existingSecretary.setDni("34525678");
        existingSecretary.setEmail("carmen_garcia@gmail.com");
        existingSecretary.setPassword("encoded-password");


        Secretary savedSecretary=repository.save(existingSecretary);
        assertThat(repository.existsByDni(savedSecretary.getDni())).isTrue();


        mvc.perform(delete(BASE_URL+"/delete/dni/{dni}",savedSecretary.getDni())
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Secretary with dni "+savedSecretary.getDni()+" successfully deleted."));


        assertThat(repository.existsByDni(savedSecretary.getDni())).isFalse();

    }

    @Test
    @WithMockUser(roles = "SECRETARY")
    void deleteSecretaryByIdTest()throws Exception{

        Secretary existingSecretary=new Secretary();
        existingSecretary.setFirstName("Carmen");
        existingSecretary.setLastName("Garcia");
        existingSecretary.setDni("34525678");
        existingSecretary.setEmail("carmen_garcia@gmail.com");
        existingSecretary.setPassword("encoded-password");


        Secretary savedSecretary=repository.save(existingSecretary);
        assertThat(repository.existsById(savedSecretary.getId())).isTrue();


        mvc.perform(delete(BASE_URL+"/delete/id/{id}",savedSecretary.getId())
                        .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().string("Secretary with id "+savedSecretary.getId()+" successfully deleted."));


        assertThat(repository.existsById(savedSecretary.getId())).isFalse();

    }
}
