package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.PatientCreateDto;
import com.sanatoryApp.UserService.dto.Request.PatientUpdateDto;
import com.sanatoryApp.UserService.dto.Response.PatientResponseDto;
import com.sanatoryApp.UserService.entity.Patient;
import com.sanatoryApp.UserService.exception.DuplicateResourceException;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.repository.IPatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PatientServiceTest {

    @Mock
    private IPatientRepository patientRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientService patientService;

    @Test
    void findAllTest() {
        //Arrange
        Patient patient1 = new Patient();
        patient1.setId(1L);
        patient1.setFirstName("Maria");
        patient1.setLastName("Lopez");
        patient1.setDni("20121456");
        patient1.setEmail("mariaLopez@gmail.com");
        patient1.setPhoneNumber("+5491112345678");
        patient1.setPassword("encodedPassword1");

        Patient patient2 = new Patient();
        patient2.setId(2L);
        patient2.setFirstName("Pablo");
        patient2.setLastName("Gutierrez");
        patient2.setDni("30453890");
        patient2.setEmail("pablo.g1@gmail.com");
        patient2.setPhoneNumber("+5491112345628");
        patient2.setPassword("encodedPassword2");


        List<Patient> allPatients = List.of(patient1, patient2);
        when(patientRepository.findAll()).thenReturn(allPatients);

        //Act
        List<PatientResponseDto> patientResponseDtoList = patientService.findAll();

        //Assert
        assertEquals(2, patientResponseDtoList.size());
        assertEquals("Maria", patientResponseDtoList.get(0).firstName());
        assertEquals("Gutierrez", patientResponseDtoList.get(1).lastName());
        verify(patientRepository).findAll();


    }

    @Test
    void findPatientById() {
        //Arrange
        Patient patient1 = new Patient();
        patient1.setId(1L);
        patient1.setFirstName("Maria");
        patient1.setLastName("Lopez");
        patient1.setDni("20121456");
        patient1.setEmail("mariaLopez@gmail.com");
        patient1.setPhoneNumber("+5491112345678");
        patient1.setPassword("encodedPassword1");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient1));

        //Act
        PatientResponseDto patientResponseDto = patientService.findPatientById(1L);
        assertEquals(1L, patientResponseDto.id());
        assertEquals("mariaLopez@gmail.com", patientResponseDto.email());

        verify(patientRepository).findById(1L);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void updatePatientById() {
        //Arrange
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "20121456", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setPassword("encodedPassword1");
        patient.setId(1L);

        PatientUpdateDto updates = new PatientUpdateDto(null, "Mary", "Grey", "mary.grey@gmail.com", null);
        PatientCreateDto updatedDto = new PatientCreateDto("Mary", "Grey", "20121456", "mary.grey@gmail.com", "+5491112345678");

        Patient updatedPatient = updatedDto.toEntity();
        updatedPatient.setId(1L);
        updatedPatient.setPassword("encodedPassword1");

        //Mock
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        //Act
        PatientResponseDto patientResponseDto = patientService.updatePatientById(1L, updates);

        //Assert & Verify
        assertEquals("Mary", patientResponseDto.firstName());
        assertEquals("Grey", patientResponseDto.lastName());
        assertEquals("mary.grey@gmail.com", patientResponseDto.email());

        verify(patientRepository).findById(1L);
        verify(patientRepository).existsByEmail("mary.grey@gmail.com");
        verify(patientRepository).save(any(Patient.class));
        verify(patientRepository, never()).existsByPhoneNumber(anyString()); //In updates, phoneNumber is null, so the method is not called
        verify(patientRepository, never()).existsByDni(anyString()); //dni is null, so not called
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void updatePatientByDni() {
        //Arrange
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "20121456", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setPassword("encodedPassword1");
        patient.setId(1L);

        PatientUpdateDto updates = new PatientUpdateDto(null, "Mary", "Grey", "mary.grey@gmail.com", null);
        PatientCreateDto updatedDto = new PatientCreateDto("Mary", "Grey", "20121456", "mary.grey@gmail.com", "+5491112345678");

        Patient updatedPatient = updatedDto.toEntity();
        updatedPatient.setId(1L);
        updatedPatient.setPassword("encodedPassword1");

        //Mock
        when(patientRepository.findPatientByDni("20121456")).thenReturn(Optional.of(patient));
        when(patientRepository.save(any(Patient.class))).thenReturn(updatedPatient);

        //Act
        PatientResponseDto patientResponseDto = patientService.updatePatientByDni("20121456", updates);

        //Assert & Verify
        assertEquals("Mary", patientResponseDto.firstName());
        assertEquals("Grey", patientResponseDto.lastName());
        assertEquals("mary.grey@gmail.com", patientResponseDto.email());

        verify(patientRepository).findPatientByDni("20121456");
        verify(patientRepository).existsByEmail("mary.grey@gmail.com");
        verify(patientRepository).save(any(Patient.class));
        verify(patientRepository, never()).existsByPhoneNumber(anyString());
        verify(patientRepository, never()).existsByDni(anyString());
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void createPatient() {
        //Arrange
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "20121456", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setId(1L);


        //Mock
        when(patientRepository.existsByEmail("mariaLopez@gmail.com")).thenReturn(false);
        when(patientRepository.existsByPhoneNumber("+5491112345678")).thenReturn(false);
        when(patientRepository.existsByDni("20121456")).thenReturn(false);
        when(passwordEncoder.encode("20121456")).thenReturn("encodedPassword123");
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        //Act
        PatientResponseDto patientResponseDto = patientService.createPatient(createDto);

        //Assert & Verify
        assertEquals("Maria", patientResponseDto.firstName());
        assertEquals("Lopez", patientResponseDto.lastName());
        assertEquals("mariaLopez@gmail.com", patientResponseDto.email());

        verify(patientRepository).existsByEmail("mariaLopez@gmail.com");
        verify(patientRepository).existsByPhoneNumber("+5491112345678");
        verify(patientRepository).existsByDni("20121456");
        verify(passwordEncoder).encode("20121456");
        verify(patientRepository).save(any(Patient.class));
        verifyNoMoreInteractions(patientRepository);
        verifyNoMoreInteractions(passwordEncoder);
    }

    @Test
    void deletePatientById() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "20121456", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setId(1L);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        patientService.deletePatientById(1L);

        verify(patientRepository).findById(1L);
        verify(patientRepository).delete(patient);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void deletePatientByDni() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "20121456", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setId(1L);

        when(patientRepository.findPatientByDni("20121456")).thenReturn(Optional.of(patient));

        patientService.deletePatientByDni("20121456");

        verify(patientRepository).findPatientByDni("20121456");
        verify(patientRepository).delete(patient);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void findPatientByDni() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "20121456", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setId(1L);

        when(patientRepository.findPatientByDni("20121456")).thenReturn(Optional.of(patient));

        PatientResponseDto patientResponseDto = patientService.findPatientByDni("20121456");

        assertEquals("20121456", patientResponseDto.dni());
        verify(patientRepository).findPatientByDni("20121456");
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void findPatientByEmail() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "20121456", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setId(1L);

        when(patientRepository.findPatientByEmail("mariaLopez@gmail.com")).thenReturn(Optional.of(patient));

        PatientResponseDto patientResponseDto = patientService.findPatientByEmail("mariaLopez@gmail.com");

        assertEquals("mariaLopez@gmail.com", patientResponseDto.email());
        verify(patientRepository).findPatientByEmail("mariaLopez@gmail.com");
        verifyNoMoreInteractions(patientRepository);
    }


    /* ERROR CASES - ResourceNotFound */

    @Test
    void findInexistentPatientById() {
        when(patientRepository.findById(11L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> patientService.findPatientById(11L));
        assertEquals("Patient not found found with id: 11", exception.getMessage());
        verify(patientRepository).findById(11L);
    }

    @Test
    void findInexistentPatientByDni() {
        when(patientRepository.findPatientByDni("20121456")).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->patientService.findPatientByDni("20121456"));
        assertEquals("Patient not found with dni: 20121456",exception.getMessage());
        verify(patientRepository).findPatientByDni("20121456");
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void findInexistentPatientByEmail() {
        when(patientRepository.findPatientByEmail("mariaLopez@gmail.com")).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> patientService.findPatientByEmail("mariaLopez@gmail.com"));

        assertEquals("Patient not found with email: mariaLopez@gmail.com", exception.getMessage());
        verify(patientRepository).findPatientByEmail("mariaLopez@gmail.com");
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void updateInexistentPatientById() {
        PatientUpdateDto updates = new PatientUpdateDto(null, "Mary", "Grey", "mary.grey@gmail.com", null);

        when(patientRepository.findById(11L)).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->patientService.updatePatientById(11L,updates));
        assertEquals("Patient not found found with id: 11",exception.getMessage());
    }

    @Test
    void updateInexistentPatientByDni() {
        PatientUpdateDto updates = new PatientUpdateDto(null, "Mary", "Grey", "mary.grey@gmail.com", null);
        when(patientRepository.findPatientByDni("20121456")).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->patientService.updatePatientByDni("20121456",updates));
        assertEquals("Patient not found with dni: 20121456",exception.getMessage());
        verify(patientRepository).findPatientByDni("20121456");
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void deleteInexistentPatientById() {
        when(patientRepository.findById(11L)).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->patientService.deletePatientById(11L));
        assertEquals("Patient not found found with id: 11",exception.getMessage());
        verify(patientRepository).findById(11L);
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void deleteInexistentPatientByDni() {
        when(patientRepository.findPatientByDni("20121456")).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->patientService.deletePatientByDni("20121456"));
        assertEquals("Patient not found with dni: 20121456",exception.getMessage());
        verify(patientRepository).findPatientByDni("20121456");
        verifyNoMoreInteractions(patientRepository);
    }

    /* CASOS DE ERROR - DuplicateResourceException */
    @Test
    void createPatient_withDuplicateEmail() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "20121456", "mariaLopez@gmail.com", "+5491112345678");

        when(patientRepository.existsByEmail("mariaLopez@gmail.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> patientService.createPatient(createDto));

        assertEquals("Patient already exists with email: mariaLopez@gmail.com", exception.getMessage());
        verify(patientRepository).existsByEmail("mariaLopez@gmail.com");
        verify(patientRepository, never()).existsByPhoneNumber(anyString());
        verify(patientRepository, never()).existsByDni(anyString());
        verify(patientRepository, never()).save(any(Patient.class));
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void createPatient_withDuplicatePhoneNumber() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "20121456", "mariaLopez@gmail.com", "+5491112345678");

        when(patientRepository.existsByEmail("mariaLopez@gmail.com")).thenReturn(false);
        when(patientRepository.existsByPhoneNumber("+5491112345678")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> patientService.createPatient(createDto));

        assertEquals("Patient already exists with phone  number: +5491112345678", exception.getMessage());
        verify(patientRepository).existsByEmail("mariaLopez@gmail.com");
        verify(patientRepository).existsByPhoneNumber("+5491112345678");
        verify(patientRepository, never()).existsByDni(anyString());
        verify(patientRepository, never()).save(any(Patient.class));
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void createPatient_withDuplicateDni() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "20121456", "mariaLopez@gmail.com", "+5491112345678");

        when(patientRepository.existsByEmail("mariaLopez@gmail.com")).thenReturn(false);
        when(patientRepository.existsByPhoneNumber("+5491112345678")).thenReturn(false);
        when(patientRepository.existsByDni("20121456")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> patientService.createPatient(createDto));

        assertEquals("Patient already exit with dni: 20121456", exception.getMessage());
        verify(patientRepository).existsByEmail("mariaLopez@gmail.com");
        verify(patientRepository).existsByPhoneNumber("+5491112345678");
        verify(patientRepository).existsByDni("20121456");
        verify(patientRepository, never()).save(any(Patient.class));
        verifyNoMoreInteractions(patientRepository);
    }

    @Test
    void updatePatientById_withDuplicateEmail() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "34765432", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setPassword("encodedPassword1");
        patient.setId(1L);

        PatientUpdateDto updates = new PatientUpdateDto("20121456", "Mary", "Grey", "mary.grey@gmail.com", null);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByDni("20121456")).thenReturn(false);
        when(patientRepository.existsByEmail("mary.grey@gmail.com")).thenReturn(true);

        DuplicateResourceException exception=assertThrows(DuplicateResourceException.class,()->patientService.updatePatientById(1L,updates));
        assertEquals("Patient already exists with email: mary.grey@gmail.com",exception.getMessage());
        verify(patientRepository,never()).save(any(Patient.class));
    }

    @Test
    void updatePatientById_withDuplicatePhoneNumber() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "34765432", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setPassword("encodedPassword1");
        patient.setId(1L);

        PatientUpdateDto updates = new PatientUpdateDto("20121456", "Mary", "Grey", "mary.grey@gmail.com", "+5491112345679");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientRepository.existsByDni(anyString())).thenReturn(false);
        when(patientRepository.existsByPhoneNumber("+5491112345679")).thenReturn(true);

        DuplicateResourceException exception=assertThrows(DuplicateResourceException.class,()->patientService.updatePatientById(1L,updates));
        assertEquals("Patient already exists with phone number: +5491112345679",exception.getMessage());
        verify(patientRepository,never()).save(any(Patient.class));

    }

    @Test
    void updatePatientById_withDuplicateDni() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "34765432", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setPassword("encodedPassword1");
        patient.setId(1L);

        PatientUpdateDto updates = new PatientUpdateDto("20121456", "Mary", "Grey", "mary.grey@gmail.com", null);

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(patientRepository.existsByDni("20121456")).thenReturn(true);

        DuplicateResourceException exception=assertThrows(DuplicateResourceException.class,()->patientService.updatePatientById(1L,updates));
        assertEquals("Patient already exists with dni: 20121456",exception.getMessage());
        verify(patientRepository,never()).save(any(Patient.class));
    }

    @Test
    void updatePatientByDni_withDuplicateEmail() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "34765432", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setPassword("encodedPassword1");
        patient.setId(1L);

        PatientUpdateDto updates = new PatientUpdateDto("20121456", "Mary", "Grey", "mary.grey@gmail.com", null);

        when(patientRepository.findPatientByDni("34765432")).thenReturn(Optional.of(patient));
        when(patientRepository.existsByDni("20121456")).thenReturn(false);
        when(patientRepository.existsByEmail("mary.grey@gmail.com")).thenReturn(true);

        DuplicateResourceException exception=assertThrows(DuplicateResourceException.class,()->patientService.updatePatientByDni("34765432",updates));
        assertEquals("Patient already exists with email: mary.grey@gmail.com",exception.getMessage());
        verify(patientRepository,never()).save(any(Patient.class));
    }

    @Test
    void updatePatientByDni_withDuplicatePhoneNumber() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "34765432", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setPassword("encodedPassword1");
        patient.setId(1L);

        PatientUpdateDto updates = new PatientUpdateDto("20121456", "Mary", "Grey", "mary.grey@gmail.com", "+5491112345679");

        when(patientRepository.findPatientByDni("34765432")).thenReturn(Optional.of(patient));
        when(patientRepository.existsByEmail(anyString())).thenReturn(false);
        when(patientRepository.existsByDni(anyString())).thenReturn(false);
        when(patientRepository.existsByPhoneNumber("+5491112345679")).thenReturn(true);

        DuplicateResourceException exception=assertThrows(DuplicateResourceException.class,()->patientService.updatePatientByDni("34765432",updates));
        assertEquals("Patient already exists with phone number: +5491112345679",exception.getMessage());
        verify(patientRepository,never()).save(any(Patient.class));
    }

    @Test
    void updatePatientByDni_withDuplicateDni() {
        PatientCreateDto createDto = new PatientCreateDto("Maria", "Lopez", "34765432", "mariaLopez@gmail.com", "+5491112345678");
        Patient patient = createDto.toEntity();
        patient.setPassword("encodedPassword1");
        patient.setId(1L);

        PatientUpdateDto updates = new PatientUpdateDto("20121456", "Mary", "Grey", "mary.grey@gmail.com", null);

        when(patientRepository.findPatientByDni("34765432")).thenReturn(Optional.of(patient));
        when(patientRepository.existsByDni("20121456")).thenReturn(true);

        DuplicateResourceException exception=assertThrows(DuplicateResourceException.class,()->patientService.updatePatientByDni("34765432",updates));
        assertEquals("Patient already exists with dni: 20121456",exception.getMessage());
        verify(patientRepository,never()).save(any(Patient.class));
    }
}
