package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.AvailabilityPatternUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.AvailabilityPatternResponseDto;
import com.sanatoryApp.CalendarService.entity.AvailabilityPattern;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import com.sanatoryApp.CalendarService.exception.InvalidTimeRangeException;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.repository.IAvailabilityPatternRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvailabilityPatternServiceTest {
    @Mock
    private IAvailabilityPatternRepository availabilityPatternRepository;

    @Mock
    private IDoctorCalendarService doctorCalendarService;

    @InjectMocks
    private AvailabilityPatternService availabilityPatternService;

    /* =================== GET METHODS =================== */

    @Test
    void findAvailabilityPatternByIdTest() {
        // Arrange
        AvailabilityPattern availabilityPattern = createAP();
        when(availabilityPatternRepository.findById(3L)).thenReturn(Optional.of(availabilityPattern));

        // Act
        AvailabilityPatternResponseDto responseDto = availabilityPatternService.findAvailabilityPatternById(3L);

        // Assert
        assertEquals(expectedAP, responseDto);
        verify(availabilityPatternRepository).findById(3L);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void findInexistentAvailabilityPatternByIdTest() {
        // Arrange
        when(availabilityPatternRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> availabilityPatternService.findAvailabilityPatternById(99L)
        );

        // Assert
        assertEquals("Availability Pattern not found with id: 99", exception.getMessage());
        verify(availabilityPatternRepository).findById(99L);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void findAvailabilityPatternByDoctorCalendarIdTest() {
        // Arrange
        AvailabilityPattern availabilityPattern = createAP();
        when(availabilityPatternRepository.findByDoctorCalendarId(10L))
                .thenReturn(List.of(availabilityPattern));

        // Act
        List<AvailabilityPatternResponseDto> responseDto =
                availabilityPatternService.findAvailabilityPatternByDoctorCalendarId(10L);

        // Assert
        assertEquals(1, responseDto.size());
        assertEquals(expectedAP.doctorCalendarName(), responseDto.get(0).doctorCalendarName());
        assertEquals(expectedAP.dayOfWeek(), responseDto.get(0).dayOfWeek());
        verify(availabilityPatternRepository).findByDoctorCalendarId(10L);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void findInexistentAvailabilityPatternByDoctorCalendarIdTest() {
        // Arrange
        when(availabilityPatternRepository.findByDoctorCalendarId(99L))
                .thenReturn(Collections.emptyList());

        // Act
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> availabilityPatternService.findAvailabilityPatternByDoctorCalendarId(99L)
        );

        // Assert
        assertEquals("Availability Pattern not found with doctor calendar id: 99", exception.getMessage());
        verify(availabilityPatternRepository).findByDoctorCalendarId(99L);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrueTest() {
        // Arrange
        AvailabilityPattern availabilityPattern = createAP();
        when(availabilityPatternRepository.findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(10L, DayOfWeek.MONDAY))
                .thenReturn(List.of(availabilityPattern));

        // Act
        List<AvailabilityPatternResponseDto> responseDto =
                availabilityPatternService.findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(10L, DayOfWeek.MONDAY);

        // Assert
        assertEquals(1, responseDto.size());
        assertEquals(expectedAP.dayOfWeek(), responseDto.get(0).dayOfWeek());
        assertEquals(expectedAP.isActive(), responseDto.get(0).isActive());
        verify(availabilityPatternRepository).findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(10L, DayOfWeek.MONDAY);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue_withNoResultsTest() {
        // Arrange
        when(availabilityPatternRepository.findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(10L, DayOfWeek.SUNDAY))
                .thenReturn(Collections.emptyList());

        // Act
        List<AvailabilityPatternResponseDto> responseDto =
                availabilityPatternService.findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(10L, DayOfWeek.SUNDAY);

        // Assert
        assertTrue(responseDto.isEmpty());
        verify(availabilityPatternRepository).findByDoctorCalendarIdAndDayOfWeekAndIsActiveTrue(10L, DayOfWeek.SUNDAY);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void findByDoctorIdTest() {
        // Arrange
        AvailabilityPattern availabilityPattern = createAP();
        when(availabilityPatternRepository.findByDoctorId(1L))
                .thenReturn(List.of(availabilityPattern));

        // Act
        List<AvailabilityPatternResponseDto> responseDto =
                availabilityPatternService.findByDoctorId(1L);

        // Assert
        assertEquals(1, responseDto.size());
        assertEquals(expectedAP.doctorCalendarId(), responseDto.get(0).doctorCalendarId());
        verify(availabilityPatternRepository).findByDoctorId(1L);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void findByDoctorId_withNoResultsTest() {
        // Arrange
        when(availabilityPatternRepository.findByDoctorId(999L))
                .thenReturn(Collections.emptyList());

        // Act
        List<AvailabilityPatternResponseDto> responseDto =
                availabilityPatternService.findByDoctorId(999L);

        // Assert
        assertTrue(responseDto.isEmpty());
        verify(availabilityPatternRepository).findByDoctorId(999L);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void findByDoctorIdAndDayTest() {
        // Arrange
        AvailabilityPattern availabilityPattern = createAP();
        when(availabilityPatternRepository.findByDoctorIdAndDay(1L, DayOfWeek.MONDAY))
                .thenReturn(List.of(availabilityPattern));

        // Act
        List<AvailabilityPatternResponseDto> responseDto =
                availabilityPatternService.findByDoctorIdAndDay(1L, DayOfWeek.MONDAY);

        // Assert
        assertEquals(1, responseDto.size());
        assertEquals(expectedAP.dayOfWeek(), responseDto.get(0).dayOfWeek());
        verify(availabilityPatternRepository).findByDoctorIdAndDay(1L, DayOfWeek.MONDAY);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void findByDoctorIdAndDay_withNoResultsTest() {
        // Arrange
        when(availabilityPatternRepository.findByDoctorIdAndDay(1L, DayOfWeek.SUNDAY))
                .thenReturn(Collections.emptyList());

        // Act
        List<AvailabilityPatternResponseDto> responseDto =
                availabilityPatternService.findByDoctorIdAndDay(1L, DayOfWeek.SUNDAY);

        // Assert
        assertTrue(responseDto.isEmpty());
        verify(availabilityPatternRepository).findByDoctorIdAndDay(1L, DayOfWeek.SUNDAY);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    /* =================== POST METHODS =================== */

    @Test
    void createAvailabilityPatternTest() {
        // Arrange
        AvailabilityPattern availabilityPattern = createAP();
        AvailabilityPatternCreateDto createDto = new AvailabilityPatternCreateDto(
                10L,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(17, 0)
        );

        when(doctorCalendarService.getDoctorCalendarEntityById(10L))
                .thenReturn(availabilityPattern.getDoctorCalendar());
        when(availabilityPatternRepository.hasOverlappingPattern(
                10L,
                DayOfWeek.MONDAY,
                availabilityPattern.getStartTime(),
                availabilityPattern.getEndTime(),
                0L
        )).thenReturn(false);
        when(availabilityPatternRepository.save(any(AvailabilityPattern.class)))
                .thenReturn(availabilityPattern);

        // Act
        AvailabilityPatternResponseDto responseDto =
                availabilityPatternService.createAvailabilityPattern(createDto);

        // Assert
        assertEquals(expectedAP, responseDto);
        verify(doctorCalendarService).getDoctorCalendarEntityById(10L);
        verify(availabilityPatternRepository).hasOverlappingPattern(
                anyLong(),
                any(DayOfWeek.class),
                any(LocalTime.class),
                any(LocalTime.class),
                anyLong()
        );
        verify(availabilityPatternRepository).save(any(AvailabilityPattern.class));
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void createAvailabilityPattern_withInexistentDoctorCalendarTest() {
        // Arrange
        AvailabilityPatternCreateDto createDto = new AvailabilityPatternCreateDto(
                99L,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(17, 0)
        );

        when(doctorCalendarService.getDoctorCalendarEntityById(99L))
                .thenThrow(new ResourceNotFound("Doctor Calendar not found with id 99"));

        // Act
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> availabilityPatternService.createAvailabilityPattern(createDto)
        );

        // Assert
        assertEquals("Doctor Calendar not found with id 99", exception.getMessage());
        verify(doctorCalendarService).getDoctorCalendarEntityById(99L);
        verifyNoInteractions(availabilityPatternRepository);
    }

    @Test
    void createAvailabilityPattern_withInvalidTimeRangeTest() {
        // Arrange - Start time (17:00) is AFTER end time (8:00) - invalid
        AvailabilityPatternCreateDto createDto = new AvailabilityPatternCreateDto(
                10L,
                DayOfWeek.MONDAY,
                LocalTime.of(17, 0),
                LocalTime.of(8, 0)
        );

        // Act
        InvalidTimeRangeException exception = assertThrows(
                InvalidTimeRangeException.class,
                () -> availabilityPatternService.createAvailabilityPattern(createDto)
        );

        // Assert
        assertEquals("End time must be after start time.", exception.getMessage());
        verifyNoInteractions(availabilityPatternRepository);
        verifyNoInteractions(doctorCalendarService);
    }

    @Test
    void createAvailabilityPattern_withOverlappingPatternTest() {
        // Arrange
        AvailabilityPattern availabilityPattern = createAP();
        AvailabilityPatternCreateDto createDto = new AvailabilityPatternCreateDto(
                10L,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(17, 0)
        );

        when(doctorCalendarService.getDoctorCalendarEntityById(10L))
                .thenReturn(availabilityPattern.getDoctorCalendar());
        when(availabilityPatternRepository.hasOverlappingPattern(
                10L,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                0L
        )).thenReturn(true);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> availabilityPatternService.createAvailabilityPattern(createDto)
        );

        // Assert
        assertEquals(
                "Schedule conflict: The new time slot overlaps with an existing schedule. " +
                        "The doctor cannot have overlapping schedules across any of their calendars.",
                exception.getMessage()
        );
        verify(doctorCalendarService).getDoctorCalendarEntityById(10L);
        verify(availabilityPatternRepository).hasOverlappingPattern(
                10L,
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(17, 0),
                0L
        );
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    /* =================== PATCH METHODS =================== */

    @Test
    void updateAvailabilityPatternByIdTest() {
        // Arrange
        AvailabilityPattern existingAP = createAP();
        AvailabilityPatternUpdateDto updateDto = new AvailabilityPatternUpdateDto(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
        );

        AvailabilityPattern updatedAP = createAP();
        updatedAP.setStartTime(LocalTime.of(9, 0));
        updatedAP.setEndTime(LocalTime.of(18, 0));

        AvailabilityPatternResponseDto expectedResponse = new AvailabilityPatternResponseDto(
                3L,
                10L,
                "Dr. Martin Guzman- Clinica del Sur",
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                true
        );

        when(availabilityPatternRepository.findById(3L)).thenReturn(Optional.of(existingAP));
        when(availabilityPatternRepository.hasOverlappingPattern(
                anyLong(),
                any(DayOfWeek.class),
                any(LocalTime.class),
                any(LocalTime.class),
                anyLong()
        )).thenReturn(false);
        when(availabilityPatternRepository.save(any(AvailabilityPattern.class))).thenReturn(updatedAP);

        // Act
        AvailabilityPatternResponseDto responseDto =
                availabilityPatternService.updateAvailabilityPatternById(3L, updateDto);

        // Assert
        assertEquals(expectedResponse, responseDto);
        verify(availabilityPatternRepository).findById(3L);
        verify(availabilityPatternRepository).hasOverlappingPattern(
                anyLong(),
                any(DayOfWeek.class),
                any(LocalTime.class),
                any(LocalTime.class),
                anyLong()
        );
        verify(availabilityPatternRepository).save(any(AvailabilityPattern.class));
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void updateAvailabilityPatternById_withInexistentIdTest() {
        // Arrange
        AvailabilityPatternUpdateDto updateDto = new AvailabilityPatternUpdateDto(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityPatternRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> availabilityPatternService.updateAvailabilityPatternById(99L, updateDto)
        );

        // Assert
        assertEquals("Availability Pattern not found with id: 99", exception.getMessage());
        verify(availabilityPatternRepository).findById(99L);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void updateAvailabilityPatternById_withInvalidTimeRangeTest() {
        // Arrange
        AvailabilityPattern existingAP = createAP();
        AvailabilityPatternUpdateDto updateDto = new AvailabilityPatternUpdateDto(
                LocalTime.of(18, 0),
                LocalTime.of(9, 0)
        );

        when(availabilityPatternRepository.findById(3L)).thenReturn(Optional.of(existingAP));

        // Act
        InvalidTimeRangeException exception = assertThrows(
                InvalidTimeRangeException.class,
                () -> availabilityPatternService.updateAvailabilityPatternById(3L, updateDto)
        );

        // Assert
        assertEquals("End time must be after start time.", exception.getMessage());
        verify(availabilityPatternRepository).findById(3L);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void updateAvailabilityPatternById_withOverlappingPatternTest() {
        // Arrange
        AvailabilityPattern existingAP = createAP();
        AvailabilityPatternUpdateDto updateDto = new AvailabilityPatternUpdateDto(
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
        );

        when(availabilityPatternRepository.findById(3L)).thenReturn(Optional.of(existingAP));
        when(availabilityPatternRepository.hasOverlappingPattern(
                10L,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                3L
        )).thenReturn(true);

        // Act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> availabilityPatternService.updateAvailabilityPatternById(3L, updateDto)
        );

        // Assert
        assertEquals("Schedule conflict: The updated time slot overlaps with an existing schedule.", exception.getMessage());
        verify(availabilityPatternRepository).findById(3L);
        verify(availabilityPatternRepository).hasOverlappingPattern(
                10L,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(18, 0),
                3L
        );
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    /* =================== DELETE METHODS =================== */

    @Test
    void deleteAvailabilityPatternByIdTest() {
        // Arrange
        AvailabilityPattern availabilityPattern = createAP();
        when(availabilityPatternRepository.findById(3L)).thenReturn(Optional.of(availabilityPattern));
        doNothing().when(availabilityPatternRepository).delete(availabilityPattern);

        // Act
        availabilityPatternService.deleteAvailabilityPatternById(3L);

        // Assert
        verify(availabilityPatternRepository).findById(3L);
        verify(availabilityPatternRepository).delete(availabilityPattern);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void deleteInexistentAvailabilityPatternByIdTest() {
        // Arrange
        when(availabilityPatternRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> availabilityPatternService.deleteAvailabilityPatternById(99L)
        );

        // Assert
        assertEquals("Availability Pattern not found with id: 99", exception.getMessage());
        verify(availabilityPatternRepository).findById(99L);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void softDeleteAvailabilityPatternByIdTest() {
        // Arrange
        AvailabilityPattern availabilityPattern = createAP();
        AvailabilityPattern deactivatedAP = createAP();
        deactivatedAP.setActive(false);

        when(availabilityPatternRepository.findById(3L)).thenReturn(Optional.of(availabilityPattern));
        when(availabilityPatternRepository.save(any(AvailabilityPattern.class))).thenReturn(deactivatedAP);

        // Act
        availabilityPatternService.softDeleteAvailabilityPatternById(3L);

        // Assert
        verify(availabilityPatternRepository).findById(3L);
        verify(availabilityPatternRepository).save(any(AvailabilityPattern.class));
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    @Test
    void softDeleteInexistentAvailabilityPatternByIdTest() {
        // Arrange
        when(availabilityPatternRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        ResourceNotFound exception = assertThrows(
                ResourceNotFound.class,
                () -> availabilityPatternService.softDeleteAvailabilityPatternById(99L)
        );

        // Assert
        assertEquals("Availability Pattern not found with id: 99", exception.getMessage());
        verify(availabilityPatternRepository).findById(99L);
        verifyNoMoreInteractions(availabilityPatternRepository);
    }

    /* =================== HELPER METHODS =================== */

    private static AvailabilityPattern createAP() {
        ZoneId timeZone = ZoneId.of("America/Argentina/Buenos_Aires");

        // Doctor Calendar
        DoctorCalendar doctorCalendar = new DoctorCalendar();
        doctorCalendar.setId(10L);
        doctorCalendar.setDoctorId(1L);
        doctorCalendar.setTimeZone(timeZone);
        doctorCalendar.setName("Dr. Martin Guzman- Clinica del Sur");
        doctorCalendar.setActive(true);

        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(17, 0);

        return new AvailabilityPattern(
                3L,                  // id
                doctorCalendar,      // doctorCalendar
                DayOfWeek.MONDAY,    // dayOfWeek
                startTime,           // startTime
                endTime,             // endTime
                true                 // isActive
        );
    }

    private static final AvailabilityPatternResponseDto expectedAP = new AvailabilityPatternResponseDto(
            3L,
            10L,
            "Dr. Martin Guzman- Clinica del Sur",
            DayOfWeek.MONDAY,
            LocalTime.of(8, 0),
            LocalTime.of(17, 0),
            true
    );
}
