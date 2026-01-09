package com.sanatoryApp.UserService.service;

import com.sanatoryApp.UserService.dto.Request.DoctorCreateDto;
import com.sanatoryApp.UserService.dto.Request.DoctorUpdateDto;
import com.sanatoryApp.UserService.dto.Response.DoctorResponseDto;
import com.sanatoryApp.UserService.entity.Doctor;
import com.sanatoryApp.UserService.exception.DuplicateResourceException;
import com.sanatoryApp.UserService.exception.ResourceNotFound;
import com.sanatoryApp.UserService.repository.IDoctorRepository;
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
public class DoctorServiceTest {

    @Mock
    private IDoctorRepository doctorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void findAllTest() {
        //Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDni("12345678");
        doctor1.setFirstName("Carlos");
        doctor1.setLastName("Rodriguez");
        doctor1.setEmail("carlos.rodriguez@gmail.com");
        doctor1.setPhoneNumber("+5491123456789");
        doctor1.setPassword("encodedPassword1");

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setDni("23456789");
        doctor2.setFirstName("Ana");
        doctor2.setLastName("Martinez");
        doctor2.setEmail("ana.martinez@gmail.com");
        doctor2.setPhoneNumber("+5491123456790");
        doctor2.setPassword("encodedPassword2");

        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));

        //Act
        List<DoctorResponseDto> doctorDtoList = doctorService.findAll();

        //Assert & Verify
        assertEquals(2, doctorDtoList.size());
        assertEquals("Rodriguez", doctorDtoList.get(0).lastName());
        assertEquals("Ana", doctorDtoList.get(1).firstName());
        verify(doctorRepository).findAll();
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void createDoctorTest() {
        //Arrange
        DoctorCreateDto createDto = new DoctorCreateDto("Carlos", "Rodriguez", "carlos.rodriguez@gmail.com", "12345678", "+5491123456789");

        Doctor doctor = createDto.toEntity();
        doctor.setId(1L);

        //Mock
        when(doctorRepository.existsByEmail("carlos.rodriguez@gmail.com")).thenReturn(false);
        when(doctorRepository.existsByPhoneNumber("+5491123456789")).thenReturn(false);
        when(doctorRepository.existsByDni("12345678")).thenReturn(false);
        when(passwordEncoder.encode("12345678")).thenReturn("encodedPassword123");
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        //Act
        DoctorResponseDto doctorResponseDto = doctorService.createDoctor(createDto);

        //Assert & Verify
        assertEquals("Carlos", doctorResponseDto.firstName());
        assertEquals("12345678", doctorResponseDto.dni());
        verify(doctorRepository).existsByEmail(anyString());
        verify(doctorRepository).existsByPhoneNumber(anyString());
        verify(doctorRepository).existsByDni(anyString());
        verify(passwordEncoder).encode("12345678");
        verify(doctorRepository).save(any(Doctor.class));
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void updateDoctorByIdTest() {
        //Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDni("12345678");
        doctor1.setFirstName("Carlos");
        doctor1.setLastName("Rodriguez");
        doctor1.setEmail("carlos.rodriguez@gmail.com");
        doctor1.setPhoneNumber("+5491123456789");
        doctor1.setPassword("encodedPassword123");

        Doctor doctorUpdated = new Doctor();
        doctorUpdated.setId(1L);
        doctorUpdated.setDni("12345678");
        doctorUpdated.setFirstName("Carlos Alberto");
        doctorUpdated.setLastName("Rodriguez");
        doctorUpdated.setEmail("carlosalberto@gmail.com");
        doctorUpdated.setPhoneNumber("+5491123456789");
        doctorUpdated.setPassword("encodedPassword123");

        DoctorUpdateDto updateDto = new DoctorUpdateDto("Carlos Alberto", null, "carlosalberto@gmail.com", null, null);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));
        when(doctorRepository.existsByEmail("carlosalberto@gmail.com")).thenReturn(false);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctorUpdated);

        //Act
        DoctorResponseDto doctorResponseDto = doctorService.updateDoctorById(1L, updateDto);

        //Assert & Verify
        assertEquals("Carlos Alberto", doctorResponseDto.firstName());
        assertEquals("carlosalberto@gmail.com", doctorResponseDto.email());
        verify(doctorRepository).findById(anyLong());
        verify(doctorRepository).existsByEmail(anyString());
        verify(doctorRepository, never()).existsByPhoneNumber(anyString()); //It does not run because it is null in the dto
        verify(doctorRepository).save(any(Doctor.class));
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void findDoctorByIdTest() {
        //Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDni("12345678");
        doctor1.setFirstName("Carlos");
        doctor1.setLastName("Rodriguez");
        doctor1.setEmail("carlos.rodriguez@gmail.com");
        doctor1.setPhoneNumber("+5491123456789");
        doctor1.setPassword("encodedPassword1");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));

        //Act
        DoctorResponseDto doctorResponseDto = doctorService.findDoctorById(1L);

        //Assert & Verify
        assertEquals(1L, doctorResponseDto.id());
        assertEquals("carlos.rodriguez@gmail.com", doctorResponseDto.email());
        verify(doctorRepository).findById(1L);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void findDoctorByDniTest() {
        //Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDni("12345678");
        doctor1.setFirstName("Carlos");
        doctor1.setLastName("Rodriguez");
        doctor1.setEmail("carlos.rodriguez@gmail.com");
        doctor1.setPhoneNumber("+5491123456789");
        doctor1.setPassword("encodedPassword1");

        when(doctorRepository.findDoctorByDni("12345678")).thenReturn(Optional.of(doctor1));

        //Act
        DoctorResponseDto doctorResponseDto = doctorService.findDoctorByDni("12345678");

        //Assert & Verify
        assertEquals("12345678", doctorResponseDto.dni());
        verify(doctorRepository).findDoctorByDni("12345678");
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void deleteDoctorByIdTest() {
        //Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDni("12345678");
        doctor1.setFirstName("Carlos");
        doctor1.setLastName("Rodriguez");
        doctor1.setEmail("carlos.rodriguez@gmail.com");
        doctor1.setPhoneNumber("+5491123456789");
        doctor1.setPassword("encodedPassword123");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));

        //Act
        doctorService.deleteDoctorById(1L);

        //Assert & Verify
        verify(doctorRepository).findById(anyLong());
        verify(doctorRepository).delete(any(Doctor.class));
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void findByEmailTest() {
        //Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDni("12345678");
        doctor1.setFirstName("Carlos");
        doctor1.setLastName("Rodriguez");
        doctor1.setEmail("carlos.rodriguez@gmail.com");
        doctor1.setPhoneNumber("+5491123456789");
        doctor1.setPassword("encodedPassword1");

        when(doctorRepository.findByEmail("carlos.rodriguez@gmail.com")).thenReturn(Optional.of(doctor1));

        //Act
        DoctorResponseDto doctorResponseDto = doctorService.findByEmail("carlos.rodriguez@gmail.com");

        //Assert & Verify
        assertEquals("carlos.rodriguez@gmail.com", doctorResponseDto.email());
        verify(doctorRepository).findByEmail("carlos.rodriguez@gmail.com");
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void findDoctorByFirstNameTest() {
        //Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDni("12345678");
        doctor1.setFirstName("Carlos");
        doctor1.setLastName("Rodriguez");
        doctor1.setEmail("carlos.rodriguez@gmail.com");
        doctor1.setPhoneNumber("+5491123456789");
        doctor1.setPassword("encodedPassword1");

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setDni("23456789");
        doctor2.setFirstName("Carlos");
        doctor2.setLastName("Martinez");
        doctor2.setEmail("carlos.martinez@gmail.com");
        doctor2.setPhoneNumber("+5491123456790");
        doctor2.setPassword("encodedPassword2");

        when(doctorRepository.findDoctorByFirstNameIgnoreCase("Carlos")).thenReturn(List.of(doctor1, doctor2));

        //Act
        List<DoctorResponseDto> doctorDtoList = doctorService.findDoctorByFirstName("Carlos");

        //Assert & Verify
        assertEquals(2, doctorDtoList.size());
        assertEquals("Carlos", doctorDtoList.get(0).firstName());
        assertEquals("Carlos", doctorDtoList.get(1).firstName());
        verify(doctorRepository).findDoctorByFirstNameIgnoreCase("Carlos");
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void findDoctorByLastNameTest() {
        //Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDni("12345678");
        doctor1.setFirstName("Carlos");
        doctor1.setLastName("Rodriguez");
        doctor1.setEmail("carlos.rodriguez@gmail.com");
        doctor1.setPhoneNumber("+5491123456789");
        doctor1.setPassword("encodedPassword1");

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setDni("23456789");
        doctor2.setFirstName("Ana");
        doctor2.setLastName("Rodriguez");
        doctor2.setEmail("ana.rodriguez@gmail.com");
        doctor2.setPhoneNumber("+5491123456790");
        doctor2.setPassword("encodedPassword2");

        when(doctorRepository.findDoctorByLastNameIgnoreCase("Rodriguez")).thenReturn(List.of(doctor1, doctor2));

        //Act
        List<DoctorResponseDto> doctorDtoList = doctorService.findDoctorByLastName("Rodriguez");

        //Assert & Verify
        assertEquals(2, doctorDtoList.size());
        assertEquals("Rodriguez", doctorDtoList.get(0).lastName());
        assertEquals("Rodriguez", doctorDtoList.get(1).lastName());
        verify(doctorRepository).findDoctorByLastNameIgnoreCase("Rodriguez");
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void disableDoctorByDniTest() {
        when(doctorRepository.existsByDni("12345678")).thenReturn(true);
        doctorService.disableDoctorByDni("12345678");
        verify(doctorRepository).existsByDni(anyString());
        verify(doctorRepository).disableDoctorByDni(anyString());
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void enableDoctorByDniTest() {
        when(doctorRepository.existsByDni("12345678")).thenReturn(true);
        doctorService.enableDoctorByDni("12345678");
        verify(doctorRepository).existsByDni(anyString());
        verify(doctorRepository).enableDoctorByDni(anyString());
        verifyNoMoreInteractions(doctorRepository);
    }


    // ERROR CASES - DuplicateResourceException
    @Test
    void createDoctor_withDuplicateEmail() {
        DoctorCreateDto createDto = new DoctorCreateDto("Carlos", "Rodriguez", "carlos.rodriguez@gmail.com", "12345678", "+5491123456789");

        when(doctorRepository.existsByEmail("carlos.rodriguez@gmail.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> doctorService.createDoctor(createDto));
        assertEquals("Doctor already exists with email: carlos.rodriguez@gmail.com", exception.getMessage());
        verify(doctorRepository).existsByEmail(anyString());
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void createDoctor_withDuplicatePhoneNumber() {
        DoctorCreateDto createDto = new DoctorCreateDto("Carlos", "Rodriguez", "carlos.rodriguez@gmail.com", "12345678", "+5491123456789");

        when(doctorRepository.existsByEmail("carlos.rodriguez@gmail.com")).thenReturn(false);
        when(doctorRepository.existsByPhoneNumber("+5491123456789")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> doctorService.createDoctor(createDto));
        assertEquals("Doctor already exists with phone number: +5491123456789", exception.getMessage());
        verify(doctorRepository).existsByEmail(anyString());
        verify(doctorRepository).existsByPhoneNumber(anyString());
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void createDoctor_withDuplicateDni() {
        DoctorCreateDto createDto = new DoctorCreateDto("Carlos", "Rodriguez", "carlos.rodriguez@gmail.com", "12345678", "+5491123456789");

        when(doctorRepository.existsByEmail("carlos.rodriguez@gmail.com")).thenReturn(false);
        when(doctorRepository.existsByPhoneNumber("+5491123456789")).thenReturn(false);
        when(doctorRepository.existsByDni("12345678")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> doctorService.createDoctor(createDto));
        assertEquals("Doctor already exists with dni: 12345678", exception.getMessage());
        verify(doctorRepository).existsByEmail(anyString());
        verify(doctorRepository).existsByPhoneNumber(anyString());
        verify(doctorRepository).existsByDni(anyString());
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void updateDoctorById_withDuplicateEmail() {
        //Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDni("12345678");
        doctor1.setFirstName("Carlos");
        doctor1.setLastName("Rodriguez");
        doctor1.setEmail("carlos.rodriguez@gmail.com");
        doctor1.setPhoneNumber("+5491123456789");
        doctor1.setPassword("encodedPassword123");

        DoctorUpdateDto updateDto = new DoctorUpdateDto(null, null, "ana.martinez@gmail.com", null, null);

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));
        when(doctorRepository.existsByEmail("ana.martinez@gmail.com")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> doctorService.updateDoctorById(1L, updateDto));
        assertEquals("Doctor already exists with email: ana.martinez@gmail.com", exception.getMessage());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void updateDoctorById_withDuplicatePhoneNumber() {
        //Arrange
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setDni("12345678");
        doctor1.setFirstName("Carlos");
        doctor1.setLastName("Rodriguez");
        doctor1.setEmail("carlos.rodriguez@gmail.com");
        doctor1.setPhoneNumber("+5491123456789");
        doctor1.setPassword("encodedPassword123");

        DoctorUpdateDto updateDto = new DoctorUpdateDto(null, null, null, null, "+5491123456790");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor1));
        when(doctorRepository.existsByPhoneNumber("+5491123456790")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> doctorService.updateDoctorById(1L, updateDto));
        assertEquals("Doctor already exists with phone number: +5491123456790", exception.getMessage());
        verify(doctorRepository, never()).save(any(Doctor.class));
    }


    // ERROR CASES - ResourceNotFound
    @Test
    void findInexistentDoctorById() {
        when(doctorRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> doctorService.findDoctorById(10L));
        assertEquals("Doctor not found with id: 10", exception.getMessage());
        verify(doctorRepository).findById(10L);
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void findInexistentDoctorByDni() {
        when(doctorRepository.findDoctorByDni("12345678")).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> doctorService.findDoctorByDni("12345678"));
        assertEquals("Doctor not found with dni: 12345678", exception.getMessage());
        verify(doctorRepository).findDoctorByDni("12345678");
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void findInexistentDoctorByEmail() {
        when(doctorRepository.findByEmail("carlos.rodriguez@gmail.com")).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> doctorService.findByEmail("carlos.rodriguez@gmail.com"));
        assertEquals("Doctor not found with email: carlos.rodriguez@gmail.com", exception.getMessage());
        verify(doctorRepository).findByEmail("carlos.rodriguez@gmail.com");
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void updateInexistentDoctorById() {
        DoctorUpdateDto updateDto = new DoctorUpdateDto("Carlos Alberto", null, "carlosalberto@gmail.com", null, null);

        when(doctorRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> doctorService.updateDoctorById(10L, updateDto));
        assertEquals("Doctor not found with id: 10", exception.getMessage());
        verify(doctorRepository).findById(anyLong());
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void deleteInexistentDoctorById() {
        when(doctorRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> doctorService.deleteDoctorById(10L));
        assertEquals("Doctor not found with id: 10", exception.getMessage());
        verify(doctorRepository).findById(anyLong());
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void disableInexistentDoctorByDni() {
        when(doctorRepository.existsByDni("12345678")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> doctorService.disableDoctorByDni("12345678"));
        assertEquals("Doctor with DNI 12345678 not found", exception.getMessage());
        verify(doctorRepository).existsByDni(anyString());
        verifyNoMoreInteractions(doctorRepository);
    }

    @Test
    void enableInexistentDoctorByDni() {
        when(doctorRepository.existsByDni("12345678")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> doctorService.enableDoctorByDni("12345678"));
        assertEquals("Doctor with DNI 12345678 not found", exception.getMessage());
        verify(doctorRepository).existsByDni(anyString());
        verifyNoMoreInteractions(doctorRepository);
    }
}
