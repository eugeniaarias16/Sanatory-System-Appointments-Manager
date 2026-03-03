package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.DoctorCalendarUpdateDto;
import com.sanatoryApp.CalendarService.dto.Request.externalService.DoctorDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarCreateResponseDto;
import com.sanatoryApp.CalendarService.dto.Response.DoctorCalendarResponseDto;
import com.sanatoryApp.CalendarService.entity.AvailabilityPattern;
import com.sanatoryApp.CalendarService.entity.CalendarException;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.repository.IDoctorCalendarRepository;
import com.sanatoryApp.CalendarService.repository.UserServiceApi;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DoctorCalendarServiceTest {

    @Mock
    private IDoctorCalendarRepository doctorCalendarRepository;

    @Mock
    private UserServiceApi userServiceApi;

    @InjectMocks
    private DoctorCalendarService doctorCalendarService;

    /* =================== GET METHODS =================== */

    @Test
    void findDoctorCalendarByIdTest() {
        // ARRANGE
        DoctorCalendar doctorCalendar = createDC();
        when(doctorCalendarRepository.findByIdAndActiveTrue(10L)).thenReturn(Optional.of(doctorCalendar));

        // ACT
        DoctorCalendarResponseDto responseDto = doctorCalendarService.findDoctorCalendarById(10L);

        // ASSERT
        assertEquals(expectedDoctorCalendar, responseDto);
        verify(doctorCalendarRepository).findByIdAndActiveTrue(10L);
        verifyNoMoreInteractions(doctorCalendarRepository);
    }

    @Test
    void findInexistentDoctorCalendarByIdTest() {
        //Arrange
        when(doctorCalendarRepository.findByIdAndActiveTrue(999L)).thenReturn(Optional.empty());
        //Act
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->doctorCalendarService.findDoctorCalendarById(999L));
        //Assert
        assertEquals("Doctor Calendar not found with id 999",exception.getMessage());
        verify(doctorCalendarRepository).findByIdAndActiveTrue(999L);
        verifyNoMoreInteractions(doctorCalendarRepository);



    }

    @Test
    void findByDoctorIdAndIsActiveTrueTest() {
        //Arrange
        DoctorCalendar doctorCalendar=createDC();
        when(doctorCalendarRepository.findByDoctorIdAndIsActiveTrue(1L)).thenReturn(List.of(doctorCalendar));
        //Act
        List<DoctorCalendarResponseDto> responseDto=doctorCalendarService.findByDoctorIdAndIsActiveTrue(1L);
        //Assert & Verify
        assertEquals(expectedDoctorCalendar,responseDto.get(0));
        verify(doctorCalendarRepository).findByDoctorIdAndIsActiveTrue(anyLong());
        verifyNoMoreInteractions(doctorCalendarRepository);
    }

    @Test
    void findByDoctorIdTest() {
        DoctorCalendar doctorCalendar=createDC();

        when(doctorCalendarRepository.findByDoctorId(1L)).thenReturn(List.of(doctorCalendar));

        List<DoctorCalendarResponseDto>responseDtoList=doctorCalendarService.findByDoctorId(1L);

        assertEquals(expectedDoctorCalendar,responseDtoList.get(0));
        verify(doctorCalendarRepository).findByDoctorId(anyLong());
        verifyNoMoreInteractions(doctorCalendarRepository);
    }

    @Test
    void findByDoctorIdAndNameTest() {
        // Arrange
        DoctorCalendar doctorCalendar = createDC();
        String normalizedName = "dr. martin guzman- clinica del sur";

        when(doctorCalendarRepository.findByDoctorIdAndName(1L, normalizedName))
                .thenReturn(Optional.of(doctorCalendar));

        // Act
        DoctorCalendarResponseDto responseDto = doctorCalendarService.findByDoctorIdAndName(1L, "Dr. Martin Guzman- Clinica del Sur");

        // Assert
        assertEquals(expectedDoctorCalendar, responseDto);
        verify(doctorCalendarRepository).findByDoctorIdAndName(1L, normalizedName);
        verifyNoMoreInteractions(doctorCalendarRepository);
    }

    @Test
    void findInexistentDoctorCalendarByDoctorIdAndNameTest() {
        // Arrange
        String normalizedName = "inexistent calendar";
        when(doctorCalendarRepository.findByDoctorIdAndName(999L, normalizedName))
                .thenReturn(Optional.empty());

        // Act
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> doctorCalendarService.findByDoctorIdAndName(999L, "Inexistent Calendar")
        );

        // Assert
        assertEquals("Doctor Calendar not found with doctor id: 999 and name: " + normalizedName, exception.getMessage());
        verify(doctorCalendarRepository).findByDoctorIdAndName(999L, normalizedName);
        verifyNoMoreInteractions(doctorCalendarRepository);
    }

    /* =================== POST METHODS =================== */

    @Test
    void createDoctorCalendarTest() {
        DoctorCalendarCreateDto createDto=new DoctorCalendarCreateDto(1L,"Dr. Martin Guzman- Clinica del Sur","America/Argentina/Buenos_Aires");
        DoctorCalendar doctorCalendar=createDC();


        /* The service transforms the name to lowercase before calling the repository, so the stub must use
        that same transformed value for Mockito to find an exact match. */
        String transformedName=createDto.name().toLowerCase().trim();

        when(userServiceApi.getDoctorById(1L)).thenReturn(doctorDto);
        when(doctorCalendarRepository.existsByDoctorIdAndNameAndIsActiveTrue(createDto.doctorId(), transformedName)).thenReturn(false);
        when(doctorCalendarRepository.save(any(DoctorCalendar.class))).thenReturn(doctorCalendar);
        DoctorCalendarCreateResponseDto responseDto=doctorCalendarService.createDoctorCalendar(createDto);

        assertEquals("Martin",responseDto.doctorFirstName());
        assertEquals("Guzman",responseDto.doctorLastName());
        assertEquals("Dr. Martin Guzman- Clinica del Sur",responseDto.name());

        verify(doctorCalendarRepository).existsByDoctorIdAndNameAndIsActiveTrue(anyLong(),anyString());
        verify(doctorCalendarRepository).save(any(DoctorCalendar.class));
        verifyNoMoreInteractions(doctorCalendarRepository);

    }

    @Test
    void createDoctorCalendar_withDuplicateNameForSameDoctorTest() {
        DoctorCalendarCreateDto createDto=new DoctorCalendarCreateDto(1L,"Dr. Martin Guzman- Clinica del Sur","America/Argentina/Buenos_Aires");


        String transformedName=createDto.name().toLowerCase().trim();


        when(userServiceApi.getDoctorById(1L)).thenReturn(doctorDto);
        when(doctorCalendarRepository.existsByDoctorIdAndNameAndIsActiveTrue(1L,transformedName)).thenReturn(true);
        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,()->doctorCalendarService.createDoctorCalendar(createDto));
        assertEquals("Doctor with id " + createDto.doctorId() + " already has an active calendar with name: " + createDto.name(),exception.getMessage());

        verify(doctorCalendarRepository).existsByDoctorIdAndNameAndIsActiveTrue(anyLong(),anyString());
        verifyNoMoreInteractions(doctorCalendarRepository);

    }

    @Test
    void createDoctorCalendar_withInexistentDoctorTest() {
        // ARRANGE - Using a non-existent ID (999L)
        DoctorCalendarCreateDto createDto = new DoctorCalendarCreateDto(
                999L,
                "Dr. Martin Guzman- Clinica del Sur",
                "America/Argentina/Buenos_Aires"
        );


        when(userServiceApi.getDoctorById(999L))
                .thenThrow(new FeignException.NotFound(
                        "Doctor with id: 999 not found",
                        mock(Request.class),
                        null,
                        null
                ));

        // ACT & ASSERT
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> doctorCalendarService.createDoctorCalendar(createDto)
        );

        // Verificar mensaje correcto
        assertEquals("Doctor with id: 999 not found", exception.getMessage());

        // Verificar interacciones
        verify(userServiceApi).getDoctorById(999L);
        verifyNoMoreInteractions(doctorCalendarRepository);
        verifyNoMoreInteractions(userServiceApi);
    }

    @Test
    void createDoctorCalendar_withInvalidTimeZoneTest() {
        DoctorCalendarCreateDto createDto=new DoctorCalendarCreateDto(1L,"Dr. Martin Guzman- Clinica del Sur","Europa/Argentina/Chicago");


        String transformedName=createDto.name().toLowerCase().trim();
        when(userServiceApi.getDoctorById(1L)).thenReturn(doctorDto);
        when(doctorCalendarRepository.existsByDoctorIdAndNameAndIsActiveTrue(1L,transformedName)).thenReturn(false);

        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,()->doctorCalendarService.createDoctorCalendar(createDto));
        assertEquals("Invalid time zone: Europa/Argentina/Chicago. Use zones like 'America/Argentina/Buenos_Aires', 'UTC', etc.",exception.getMessage());


        verify(doctorCalendarRepository).existsByDoctorIdAndNameAndIsActiveTrue(createDto.doctorId(),transformedName);
        verifyNoMoreInteractions(doctorCalendarRepository);
    }

    /* =================== PATCH METHODS =================== */

    @Test
    void updateDoctorCalendarTest() {
        DoctorCalendar existingDC=createDC();
        DoctorCalendarUpdateDto updateDto=new DoctorCalendarUpdateDto(1L,"Dr. Martin Guzman- Clinica del Norte",null);
        String transformedName=updateDto.name().toLowerCase().trim();

        DoctorCalendar updatedDoctorCalendar=createDC();
        updatedDoctorCalendar.setName("dr. martin guzman- clinica del norte");

        when(doctorCalendarRepository.findByIdAndActiveTrue(10L)).thenReturn(Optional.of(existingDC));
        when(userServiceApi.getDoctorById(1L)).thenReturn(doctorDto);
        when(doctorCalendarRepository.existsByDoctorIdAndNameExcludingId(1L, transformedName, 10L)).thenReturn(false);
        when(doctorCalendarRepository.save(any(DoctorCalendar.class))).thenReturn(updatedDoctorCalendar);

        DoctorCalendarResponseDto responseDto=doctorCalendarService.updateDoctorCalendar(10L,updateDto);
        assertEquals("dr. martin guzman- clinica del norte",responseDto.name());
        verify(userServiceApi).getDoctorById(1L);
        verify(doctorCalendarRepository).existsByDoctorIdAndNameExcludingId(1L, transformedName, 10L);
        verify(doctorCalendarRepository).save(any(DoctorCalendar.class));
        verifyNoMoreInteractions(doctorCalendarRepository);

    }

    @Test
    void updateDoctorCalendar_withInexistentIdTest() {
        // Arrange
        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(null, "New Name", null);
        when(doctorCalendarRepository.findByIdAndActiveTrue(999L)).thenReturn(Optional.empty());

        // Act
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> doctorCalendarService.updateDoctorCalendar(999L, updateDto)
        );

        // Assert
        assertEquals("Doctor Calendar not found with id 999", exception.getMessage());
        verify(doctorCalendarRepository).findByIdAndActiveTrue(999L);
        verifyNoMoreInteractions(doctorCalendarRepository);
        verifyNoInteractions(userServiceApi);
    }

    @Test
    void updateDoctorCalendar_withNewDoctorIdTest() {
        // Arrange
        DoctorCalendar existingDC = createDC();
        DoctorDto newDoctorDto = new DoctorDto(2L, "Carlos", "Perez", "carlos@gmail.com", "543434874999");
        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(2L, null, null);

        DoctorCalendar updatedDoctorCalendar = createDC();
        updatedDoctorCalendar.setDoctorId(2L);

        when(doctorCalendarRepository.findByIdAndActiveTrue(10L)).thenReturn(Optional.of(existingDC));
        when(userServiceApi.getDoctorById(2L)).thenReturn(newDoctorDto);
        when(doctorCalendarRepository.save(any(DoctorCalendar.class))).thenReturn(updatedDoctorCalendar);

        // Act
        DoctorCalendarResponseDto responseDto = doctorCalendarService.updateDoctorCalendar(10L, updateDto);

        // Assert
        assertEquals(2L, responseDto.doctorId());
        verify(doctorCalendarRepository).findByIdAndActiveTrue(10L);
        verify(userServiceApi).getDoctorById(2L);
        verify(doctorCalendarRepository).save(any(DoctorCalendar.class));
        verifyNoMoreInteractions(doctorCalendarRepository);
    }

    @Test
    void updateDoctorCalendar_withInexistentNewDoctorIdTest() {
        // Arrange
        DoctorCalendar existingDC = createDC();
        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(999L, null, null);

        when(doctorCalendarRepository.findByIdAndActiveTrue(10L)).thenReturn(Optional.of(existingDC));
        when(userServiceApi.getDoctorById(999L))
                .thenThrow(new FeignException.NotFound(
                        "Doctor not found",
                        mock(Request.class),
                        null,
                        null
                ));

        // Act
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> doctorCalendarService.updateDoctorCalendar(10L, updateDto)
        );

        // Assert
        assertEquals("Doctor with id: 999 not found", exception.getMessage());
        verify(doctorCalendarRepository).findByIdAndActiveTrue(10L);
        verify(userServiceApi).getDoctorById(999L);
        verifyNoMoreInteractions(doctorCalendarRepository);
    }

    @Test
    void updateDoctorCalendar_withDuplicateNameTest() {
        // Arrange
        DoctorCalendar existingDC = createDC();
        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(null, "Duplicate Calendar Name", null);
        String transformedName = "duplicate calendar name";

        when(doctorCalendarRepository.findByIdAndActiveTrue(10L)).thenReturn(Optional.of(existingDC));
        when(doctorCalendarRepository.existsByDoctorIdAndNameExcludingId(1L, transformedName, 10L)).thenReturn(true);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorCalendarService.updateDoctorCalendar(10L, updateDto)
        );

        // Assert
        assertEquals("Doctor already has another active calendar with name: " + transformedName, exception.getMessage());
        verify(doctorCalendarRepository).findByIdAndActiveTrue(10L);
        verify(doctorCalendarRepository).existsByDoctorIdAndNameExcludingId(1L, transformedName, 10L);
        verifyNoMoreInteractions(doctorCalendarRepository);
    }

    @Test
    void updateDoctorCalendar_withInvalidTimeZoneTest() {
        // Arrange
        DoctorCalendar existingDC = createDC();
        DoctorCalendarUpdateDto updateDto = new DoctorCalendarUpdateDto(null, null, "Invalid/TimeZone");

        when(doctorCalendarRepository.findByIdAndActiveTrue(10L)).thenReturn(Optional.of(existingDC));

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> doctorCalendarService.updateDoctorCalendar(10L, updateDto)
        );

        // Assert
        assertEquals("Invalid time zone: Invalid/TimeZone. Use zones like 'America/Argentina/Buenos_Aires', 'UTC', etc.", exception.getMessage());
        verify(doctorCalendarRepository).findByIdAndActiveTrue(10L);
        verifyNoMoreInteractions(doctorCalendarRepository);
    }

    /* =================== DELETE METHODS =================== */

    @Test
    void deleteDoctorCalendarTest() {
        // Arrange
        DoctorCalendar existingDC = createDC();
        DoctorCalendar deactivatedDC = createDC();
        deactivatedDC.setActive(false);

        when(doctorCalendarRepository.findByIdAndActiveTrue(10L)).thenReturn(Optional.of(existingDC));
        when(doctorCalendarRepository.save(any(DoctorCalendar.class))).thenReturn(deactivatedDC);

        // Act
        doctorCalendarService.deleteDoctorCalendar(10L);

        // Assert
        verify(doctorCalendarRepository).findByIdAndActiveTrue(10L);
        verify(doctorCalendarRepository).save(any(DoctorCalendar.class));
        verifyNoMoreInteractions(doctorCalendarRepository);
    }

    @Test
    void deleteInexistentDoctorCalendarTest() {
        // Arrange
        when(doctorCalendarRepository.findByIdAndActiveTrue(999L)).thenReturn(Optional.empty());

        // Act
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> doctorCalendarService.deleteDoctorCalendar(999L)
        );

        // Assert
        assertEquals("Doctor Calendar not found with id 999", exception.getMessage());
        verify(doctorCalendarRepository).findByIdAndActiveTrue(999L);
        verifyNoMoreInteractions(doctorCalendarRepository);
    }


    /* =================== HELPER METHODS =================== */


   private static DoctorCalendar createDC() {
       List<CalendarException> calendarExceptions = new ArrayList<>();
       List<AvailabilityPattern> availabilityPatterns = new ArrayList<>();
       ZoneId timeZone = ZoneId.of("America/Argentina/Buenos_Aires");

       return new DoctorCalendar(
           10L,                                      // id
           1L,                                       // doctorId
           "Dr. Martin Guzman- Clinica del Sur",    // name
           true,                                     // isActive
           timeZone,                                 // ZoneId
           availabilityPatterns,                     // List<AvailabilityPattern>
           calendarExceptions                        // List<CalendarException>
       );
   }
    private static DoctorCalendarResponseDto expectedDoctorCalendar =new DoctorCalendarResponseDto(10L,1L,"Dr. Martin Guzman- Clinica del Sur",true,"America/Argentina/Buenos_Aires");

    private static  DoctorDto doctorDto=new DoctorDto(1L,"Martin","Guzman","martin_guzman@gmail.com","543434874323");
   

}
