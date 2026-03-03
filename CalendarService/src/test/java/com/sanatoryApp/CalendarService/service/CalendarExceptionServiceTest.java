package com.sanatoryApp.CalendarService.service;

import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionUpdateDto;
import com.sanatoryApp.CalendarService.dto.Request.externalService.DoctorDto;
import com.sanatoryApp.CalendarService.dto.Response.CalendarExceptionResponseDto;
import com.sanatoryApp.CalendarService.entity.*;
import com.sanatoryApp.CalendarService.exception.BadRequest;
import com.sanatoryApp.CalendarService.exception.InvalidTimeRangeException;
import com.sanatoryApp.CalendarService.exception.ResourceNotFound;
import com.sanatoryApp.CalendarService.repository.ICalendarExceptionRepository;
import com.sanatoryApp.CalendarService.repository.UserServiceApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CalendarExceptionServiceTest {

    @Mock
    private ICalendarExceptionRepository calendarExceptionRepository;
    @Mock
    private IDoctorCalendarService doctorCalendarService;
    @Mock
    private UserServiceApi userServiceApi;

    @InjectMocks
    private CalendarExceptionService calendarExceptionService;


    /* =================== CREATE METHODS =================== */

    @Test
    void createCalendarException_withScopeGlobalAndFullDayTest() {
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(null, null, LocalDate.of(2026, 12, 25), null, null, null, ExceptionType.CUSTOM, "Christmas", ExceptionScope.GLOBAL);
        CalendarException calendarException = createGlobalCE();

        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(0L))).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(calendarException);


        CalendarExceptionResponseDto responseDto = calendarExceptionService.createCalendarException(createDto);
        assertEquals("Christmas", responseDto.reason());
        assertEquals(ExceptionScope.GLOBAL, responseDto.scope());
        assertEquals(true, responseDto.isFullDay());

        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(0L));
        verifyNoMoreInteractions(calendarExceptionRepository);

    }

    @Test
    void createCalendarException_withScopeSemiGlobalAndPartialDayTest() {
        DoctorCalendar dc = createDC();
        LocalDate startDate = LocalDate.of(2026, 04, 15);

        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(dc.getDoctorId(), null, startDate, null, LocalTime.of(8, 00, 00), LocalTime.of(15, 00, 00), ExceptionType.CONFERENCE, "Cardiology Conference", ExceptionScope.SEMI_GLOBAL);
        CalendarException calendarException = new CalendarException(10L, null, dc.getDoctorId(), ExceptionScope.SEMI_GLOBAL, startDate, null, LocalTime.of(8, 00, 00), LocalTime.of(15, 00, 00), ExceptionType.CONFERENCE, "Cardiology Conference", false, true, true);

        when(userServiceApi.getDoctorById(anyLong())).thenReturn(new DoctorDto(1L, "Martin", "Guzman", "mguzman@mail.com", "1234567890"));
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(anyLong(), isNull(), any(LocalDate.class), isNull(), eq(0L))).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(calendarException);

        CalendarExceptionResponseDto responseDto = calendarExceptionService.createCalendarException(createDto);
        assertEquals(ExceptionScope.SEMI_GLOBAL, responseDto.scope());
        assertEquals(false, responseDto.isFullDay());
        assertEquals(startDate, responseDto.startDate());
        assertEquals(null, responseDto.endDate());

        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(anyLong(), isNull(), any(LocalDate.class), isNull(), eq(0L));
        verifyNoMoreInteractions(calendarExceptionRepository);


    }

    @Test
    void createCalendarException_withScopeSpecificTest() {
        DoctorCalendar dc = createDC();
        LocalDate startDate = LocalDate.of(2026, 04, 15);

        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(dc.getDoctorId(), dc.getId(), startDate, null, LocalTime.of(8, 00, 00), LocalTime.of(15, 00, 00), ExceptionType.UNAVAILABLE, "office maintenance", ExceptionScope.SPECIFIC);
        CalendarException calendarException = new CalendarException(10L, null, dc.getDoctorId(), ExceptionScope.SPECIFIC, startDate, null, LocalTime.of(8, 00, 00), LocalTime.of(15, 00, 00), ExceptionType.CONFERENCE, "office maintenance", false, true, true);
        when(doctorCalendarService.getDoctorCalendarEntityById(anyLong())).thenReturn(dc);
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(anyLong(), anyLong(), any(LocalDate.class), isNull(), eq(0L))).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(calendarException);

        CalendarExceptionResponseDto responseDto = calendarExceptionService.createCalendarException(createDto);
        assertEquals(ExceptionScope.SPECIFIC, responseDto.scope());
        assertEquals(false, responseDto.isFullDay());
        assertEquals(startDate, responseDto.startDate());
        assertEquals(null, responseDto.endDate());
        assertEquals("office maintenance", responseDto.reason());

        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(anyLong(), anyLong(), any(LocalDate.class), isNull(), eq(0L));
        verifyNoMoreInteractions(calendarExceptionRepository);


    }

    @Test
    void createCalendarException_withMultiDayRangeTest() {
        DoctorCalendar dc = createDC();
        LocalDate startDate = LocalDate.of(2026, 04, 15);
        LocalDate endDate = LocalDate.of(2026, 04, 20);

        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(dc.getDoctorId(), null, startDate, endDate, null, null, ExceptionType.VACATION, null, ExceptionScope.SEMI_GLOBAL);
        CalendarException calendarException = new CalendarException(10L, null, dc.getDoctorId(), ExceptionScope.SEMI_GLOBAL, startDate, null, null, null, ExceptionType.VACATION, null, true, false, true);

        when(userServiceApi.getDoctorById(anyLong())).thenReturn(new DoctorDto(1L, "Martin", "Guzman", "mguzman@mail.com", "1234567890"));
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(dc.getDoctorId(), null, startDate, endDate, 0l)).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(calendarException);

        CalendarExceptionResponseDto responseDto = calendarExceptionService.createCalendarException(createDto);
        assertEquals(ExceptionType.VACATION, responseDto.exceptionType());
        assertEquals(null, responseDto.reason());
        assertEquals(ExceptionScope.SEMI_GLOBAL, responseDto.scope());
        assertEquals(false, responseDto.isSingleDay());

        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(anyLong(), isNull(), any(LocalDate.class), any(LocalDate.class), anyLong());
        verifyNoMoreInteractions(calendarExceptionRepository);


    }

    @Test
    void createCalendarException_withInvalidTimeRangeTest() {

        DoctorCalendar dc = createDC();
        LocalDate endDate = LocalDate.of(2026, 04, 15);
        LocalDate startDate = LocalDate.of(2026, 04, 20);
        LocalTime startTime = LocalTime.of(10, 00, 15);
        LocalTime endTime = LocalTime.of(8, 10, 00);


        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(dc.getDoctorId(), null, startDate, endDate, startTime, endTime, ExceptionType.VACATION, null, ExceptionScope.SEMI_GLOBAL);

        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> calendarExceptionService.createCalendarException(createDto));

        assertEquals("End time must be after start time.", exception.getMessage());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void createCalendarException_withInvalidDateRangeTest() {
        DoctorCalendar dc = createDC();
        LocalDate endDate = LocalDate.of(2026, 04, 15);
        LocalDate startDate = LocalDate.of(2026, 04, 20);

        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(dc.getDoctorId(), null, startDate, endDate, null, null, ExceptionType.VACATION, null, ExceptionScope.SEMI_GLOBAL);

        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> calendarExceptionService.createCalendarException(createDto));

        assertEquals("End Date must be equals o after Start Date", exception.getMessage());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void createCalendarException_withCustomTypeAndNoReasonTest() {
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(null, null, LocalDate.of(2026, 12, 25), null, null, null, ExceptionType.CUSTOM, " ", ExceptionScope.GLOBAL);
        CalendarException calendarException = createGlobalCE();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calendarExceptionService.createCalendarException(createDto));

        assertEquals("When exception type is CUSTOM, a reason must be provided", exception.getMessage());
        verifyNoMoreInteractions(calendarExceptionRepository);

    }

    @Test
    void createCalendarException_withConflictingExceptionTest() {
        /*Conflicts with date range 20-28 of December.
        25 of December already exists GLOBAL CE, "Christmas".*/
        LocalDate startDate = LocalDate.of(2026, 12, 20);
        LocalDate endDate = LocalDate.of(2026, 12, 28);
        CalendarException existingCE = createGlobalCE();

        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(1L, null, startDate, endDate, null, null, ExceptionType.VACATION, null, ExceptionScope.SEMI_GLOBAL);

        when(userServiceApi.getDoctorById(anyLong())).thenReturn(new DoctorDto(1L, "Martin", "Guzman", "mguzman@mail.com", "1234567890"));
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(1L, null, startDate, endDate, 0l)).thenReturn(List.of(existingCE));

        BadRequest exception = assertThrows(BadRequest.class, () -> calendarExceptionService.createCalendarException(createDto));

        assertTrue(exception.getMessage().contains("Schedule conflict detected"));
        assertTrue(exception.getMessage().contains("ID:" + existingCE.getId()));

        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void createCalendarException_withScopeGlobalAndDoctorIdProvidedTest() {
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(1L, null, LocalDate.of(2026, 12, 25), null, null, null, ExceptionType.HOLIDAY, "Christmas", ExceptionScope.GLOBAL);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calendarExceptionService.createCalendarException(createDto));

        assertEquals("GLOBAL exceptions must not have doctorId or doctorCalendarId. Both must be null.", exception.getMessage());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void createCalendarException_withScopeSemiGlobalAndNoDoctorIdTest() {
        LocalDate startDate = LocalDate.of(2026, 04, 15);
        LocalDate endDate = LocalDate.of(2026, 04, 20);

        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(null, null, startDate, endDate, null, null, ExceptionType.VACATION, null, ExceptionScope.SEMI_GLOBAL);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calendarExceptionService.createCalendarException(createDto));
        assertEquals("SEMI_GLOBAL exceptions require doctorId", exception.getMessage());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void createCalendarException_withScopeSpecificAndNoDoctorCalendarIdTest() {
        LocalDate startDate = LocalDate.of(2026, 04, 15);
        CalendarExceptionCreateDto createDto = new CalendarExceptionCreateDto(1L, null, startDate, null, LocalTime.of(8, 00, 00), LocalTime.of(15, 00, 00), ExceptionType.UNAVAILABLE, "office maintenance", ExceptionScope.SPECIFIC);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calendarExceptionService.createCalendarException(createDto));

        assertEquals("SPECIFIC exceptions require doctorCalendarId", exception.getMessage());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }


    /* =================== UPDATE METHODS =================== */

    @Test
    void updateCalendarExceptionTest() {
        CalendarException existingCE = createGlobalCE();
        LocalDate startDate = LocalDate.of(2026, 12, 24);
        LocalDate endDate = LocalDate.of(2026, 12, 25);
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setStartDate(JsonNullable.of(startDate));
        updateDto.setEndDate(JsonNullable.of(endDate));
        updateDto.setExceptionType(JsonNullable.of(ExceptionType.HOLIDAY));

        CalendarException updatedCE = new CalendarException(existingCE.getId(), null, null, ExceptionScope.GLOBAL, startDate, endDate, null, null, ExceptionType.HOLIDAY, "Christmas", true, false, true);


        when(calendarExceptionRepository.findByIdAndIsActiveTrue(anyLong())).thenReturn(Optional.of(existingCE));
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), any(LocalDate.class), anyLong())).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(updatedCE);

        CalendarExceptionResponseDto responseDto = calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto);
        assertEquals(endDate, responseDto.endDate());
        assertEquals(ExceptionType.HOLIDAY, responseDto.exceptionType());

        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(anyLong());
        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), any(LocalDate.class), anyLong());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withInexistentIdTest() {
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        when(calendarExceptionRepository.findByIdAndIsActiveTrue(99L)).thenThrow(new ResourceNotFound("Calendar exception with id 99 not found."));

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> calendarExceptionService.updateCalendarException(99L, updateDto));

        assertEquals("Calendar exception with id 99 not found.", exception.getMessage());
        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(anyLong());
        verifyNoMoreInteractions(calendarExceptionRepository);


    }

    @Test
    void updateCalendarException_withOnlyReasonFieldTest() {
        CalendarException existingCE = createGlobalCE();
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setReason(JsonNullable.of("updated christmas reason"));

        CalendarException updatedCE = new CalendarException(existingCE.getId(), null, null, ExceptionScope.GLOBAL, existingCE.getStartDate(), null, null, null, ExceptionType.CUSTOM, "updated christmas reason", true, true, true);

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()))).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(updatedCE);

        CalendarExceptionResponseDto responseDto = calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto);
        assertEquals("updated christmas reason", responseDto.reason());
        assertEquals(ExceptionScope.GLOBAL, responseDto.scope());
        assertEquals(ExceptionType.CUSTOM, responseDto.exceptionType());
        assertEquals(existingCE.getStartDate(), responseDto.startDate());

        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()));
        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withNullStartDateTest() {
        CalendarException existingCE = createGlobalCE();
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setStartDate(JsonNullable.of(null));

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto));

        assertEquals("startDate cannot be set to null", exception.getMessage());
        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withNullExceptionTypeTest() {
        CalendarException existingCE = createGlobalCE();
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setExceptionType(JsonNullable.of(null));

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto));

        assertEquals("exceptionType cannot be set to null", exception.getMessage());
        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withNullScopeTest() {
        CalendarException existingCE = createGlobalCE();
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setScope(JsonNullable.of(null));

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto));

        assertEquals("scope cannot be set to null", exception.getMessage());
        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withInvalidTimeRangeTest() {
        CalendarException existingCE = createGlobalCE();
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setStartTime(JsonNullable.of(LocalTime.of(18, 0)));
        updateDto.setEndTime(JsonNullable.of(LocalTime.of(10, 0)));

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));

        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto));

        assertEquals("End time must be after start time.", exception.getMessage());
        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withInvalidDateRangeTest() {
        CalendarException existingCE = createGlobalCE();
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setStartDate(JsonNullable.of(LocalDate.of(2026, 12, 28)));
        updateDto.setEndDate(JsonNullable.of(LocalDate.of(2026, 12, 20)));

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));

        InvalidTimeRangeException exception = assertThrows(InvalidTimeRangeException.class, () -> calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto));

        assertEquals("End Date must be equals o after Start Date", exception.getMessage());
        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withCustomTypeAndNoReasonTest() {
        CalendarException existingCE = createGlobalCE(); // CUSTOM type with reason="Christmas"
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setReason(JsonNullable.of(null)); // explicitly set reason to null

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto));

        assertEquals("When exception type is CUSTOM, a reason must be provided", exception.getMessage());
        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withScopeChangeToGlobalTest() {
        DoctorCalendar dc = createDC();
        CalendarException existingCE = createSpecificCE(dc);
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setScope(JsonNullable.of(ExceptionScope.GLOBAL));

        CalendarException updatedCE = new CalendarException(existingCE.getId(), null, null, ExceptionScope.GLOBAL, existingCE.getStartDate(), null, existingCE.getStartTime(), existingCE.getEndTime(), existingCE.getExceptionType(), existingCE.getReason(), false, true, true);

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()))).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(updatedCE);

        CalendarExceptionResponseDto responseDto = calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto);
        assertEquals(ExceptionScope.GLOBAL, responseDto.scope());
        assertNull(responseDto.doctorId());
        assertNull(responseDto.doctorCalendarId());

        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()));
        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withScopeChangeToSemiGlobalTest() {
        // NOTE: This test depends on validateDoctorId being moved to the service (see ValidateDtoFields static field issue)
        CalendarException existingCE = createGlobalCE();
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setScope(JsonNullable.of(ExceptionScope.SEMI_GLOBAL));
        updateDto.setDoctorId(JsonNullable.of(1L));

        CalendarException updatedCE = new CalendarException(existingCE.getId(), null, 1L, ExceptionScope.SEMI_GLOBAL, existingCE.getStartDate(), null, null, null, existingCE.getExceptionType(), existingCE.getReason(), true, true, true);

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));
        when(userServiceApi.getDoctorById(1L)).thenReturn(new DoctorDto(1L, "Martin", "Guzman", "mguzman@mail.com", "1234567890"));
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(eq(1L), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()))).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(updatedCE);

        CalendarExceptionResponseDto responseDto = calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto);
        assertEquals(ExceptionScope.SEMI_GLOBAL, responseDto.scope());
        assertEquals(1L, responseDto.doctorId());

        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(eq(1L), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()));
        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withScopeChangeToSpecificTest() {
        DoctorCalendar dc = createDC();
        CalendarException existingCE = createGlobalCE();
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setScope(JsonNullable.of(ExceptionScope.SPECIFIC));
        updateDto.setDoctorCalendarId(JsonNullable.of(dc.getId()));

        CalendarException updatedCE = new CalendarException(existingCE.getId(), dc, dc.getDoctorId(), ExceptionScope.SPECIFIC, existingCE.getStartDate(), null, null, null, existingCE.getExceptionType(), existingCE.getReason(), true, true, true);

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));
        when(doctorCalendarService.getDoctorCalendarEntityById(dc.getId())).thenReturn(dc);
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(eq(dc.getDoctorId()), eq(dc.getId()), any(LocalDate.class), isNull(), eq(existingCE.getId()))).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(updatedCE);

        CalendarExceptionResponseDto responseDto = calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto);
        assertEquals(ExceptionScope.SPECIFIC, responseDto.scope());
        assertEquals(dc.getDoctorId(), responseDto.doctorId());
        assertEquals(dc.getId(), responseDto.doctorCalendarId());

        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verify(doctorCalendarService).getDoctorCalendarEntityById(dc.getId());
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(eq(dc.getDoctorId()), eq(dc.getId()), any(LocalDate.class), isNull(), eq(existingCE.getId()));
        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withConflictingExceptionTest() {
        CalendarException existingCE = createGlobalCE();
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setStartDate(JsonNullable.of(LocalDate.of(2026, 12, 20)));
        updateDto.setEndDate(JsonNullable.of(LocalDate.of(2026, 12, 28)));

        CalendarException conflictCE = new CalendarException(99L, null, null, ExceptionScope.GLOBAL, LocalDate.of(2026, 12, 24), null, null, null, ExceptionType.HOLIDAY, null, true, true, true);

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), any(LocalDate.class), eq(existingCE.getId()))).thenReturn(List.of(conflictCE));

        BadRequest exception = assertThrows(BadRequest.class, () -> calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto));

        assertTrue(exception.getMessage().contains("Schedule conflict detected"));
        assertTrue(exception.getMessage().contains("ID:" + conflictCE.getId()));

        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), any(LocalDate.class), eq(existingCE.getId()));
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withMismatchedDoctorIdAndCalendarIdTest() {
        DoctorCalendar dc = createDC(); // doctorId=1L
        CalendarException existingCE = createSpecificCE(dc);
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setDoctorId(JsonNullable.of(99L)); // doesn't match dc.getDoctorId()=1L

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));
        when(doctorCalendarService.getDoctorCalendarEntityById(dc.getId())).thenReturn(dc);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto));

        assertEquals("The Doctor ID provided does not correspond to the Doctor Calendar ID.", exception.getMessage());
        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verify(doctorCalendarService).getDoctorCalendarEntityById(dc.getId());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withEndDateSetToNullTest() {
        CalendarException existingCE = new CalendarException(25L, null, null, ExceptionScope.GLOBAL, LocalDate.of(2026, 12, 20), LocalDate.of(2026, 12, 28), null, null, ExceptionType.HOLIDAY, null, true, false, true);
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setEndDate(JsonNullable.of(null));

        CalendarException updatedCE = new CalendarException(existingCE.getId(), null, null, ExceptionScope.GLOBAL, existingCE.getStartDate(), null, null, null, ExceptionType.HOLIDAY, null, true, true, true);

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()))).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(updatedCE);

        CalendarExceptionResponseDto responseDto = calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto);
        assertNull(responseDto.endDate());
        assertTrue(responseDto.isSingleDay());

        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()));
        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withTimesSetToNullForFullDayTest() {
        CalendarException existingCE = new CalendarException(25L, null, null, ExceptionScope.GLOBAL, LocalDate.of(2026, 12, 25), null, LocalTime.of(8, 0), LocalTime.of(15, 0), ExceptionType.HOLIDAY, null, false, true, true);
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto();
        updateDto.setStartTime(JsonNullable.of(null));
        updateDto.setEndTime(JsonNullable.of(null));

        CalendarException updatedCE = new CalendarException(existingCE.getId(), null, null, ExceptionScope.GLOBAL, existingCE.getStartDate(), null, null, null, ExceptionType.HOLIDAY, null, true, true, true);

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()))).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(updatedCE);

        CalendarExceptionResponseDto responseDto = calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto);
        assertTrue(responseDto.isFullDay());

        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()));
        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void updateCalendarException_withUndefinedFieldsKeepsExistingValuesTest() {
        CalendarException existingCE = createGlobalCE();
        CalendarExceptionUpdateDto updateDto = new CalendarExceptionUpdateDto(); // all fields undefined

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));
        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()))).thenReturn(List.of());
        when(calendarExceptionRepository.save(any(CalendarException.class))).thenReturn(existingCE);

        CalendarExceptionResponseDto responseDto = calendarExceptionService.updateCalendarException(existingCE.getId(), updateDto);
        assertEquals(existingCE.getStartDate(), responseDto.startDate());
        assertEquals(existingCE.getScope(), responseDto.scope());
        assertEquals(existingCE.getExceptionType(), responseDto.exceptionType());
        assertEquals(existingCE.getReason(), responseDto.reason());

        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(isNull(), isNull(), any(LocalDate.class), isNull(), eq(existingCE.getId()));
        verify(calendarExceptionRepository).save(any(CalendarException.class));
        verifyNoMoreInteractions(calendarExceptionRepository);
    }


    /* =================== GET METHODS =================== */

    @Test
    void findByIdAndIsActiveTest() {
        CalendarException existingCE = createGlobalCE();

        when(calendarExceptionRepository.findByIdAndIsActiveTrue(existingCE.getId())).thenReturn(Optional.of(existingCE));

        CalendarExceptionResponseDto responseDto = calendarExceptionService.findByIdAndIsActive(existingCE.getId());
        assertEquals(existingCE.getId(), responseDto.id());
        assertEquals(existingCE.getScope(), responseDto.scope());
        assertEquals(existingCE.getExceptionType(), responseDto.exceptionType());

        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(existingCE.getId());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findByIdAndIsActive_withInexistentIdTest() {
        when(calendarExceptionRepository.findByIdAndIsActiveTrue(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> calendarExceptionService.findByIdAndIsActive(99L));

        assertEquals("Calendar exception with id 99 not found.", exception.getMessage());
        verify(calendarExceptionRepository).findByIdAndIsActiveTrue(99L);
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findByDoctorIdTest() {
        CalendarException ce = createGlobalCE();

        when(calendarExceptionRepository.findByDoctorId(1L)).thenReturn(List.of(ce));

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findBydDoctorId(1L);
        assertEquals(1, result.size());
        assertEquals(ce.getId(), result.get(0).id());

        verify(calendarExceptionRepository).findByDoctorId(1L);
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findByDoctorId_withNoResultsTest() {
        when(calendarExceptionRepository.findByDoctorId(99L)).thenReturn(List.of());

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findBydDoctorId(99L);
        assertTrue(result.isEmpty());

        verify(calendarExceptionRepository).findByDoctorId(99L);
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findAllGlobalAndIsActiveTest() {
        CalendarException ce = createGlobalCE();

        when(calendarExceptionRepository.findAllGlobalAndIsActive()).thenReturn(List.of(ce));

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findAllGlobalAndIsActive();
        assertEquals(1, result.size());
        assertEquals(ExceptionScope.GLOBAL, result.get(0).scope());

        verify(calendarExceptionRepository).findAllGlobalAndIsActive();
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findAllGlobalAndIsActive_withNoResultsTest() {
        when(calendarExceptionRepository.findAllGlobalAndIsActive()).thenReturn(List.of());

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findAllGlobalAndIsActive();
        assertTrue(result.isEmpty());

        verify(calendarExceptionRepository).findAllGlobalAndIsActive();
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findSemiGlobalByDoctorIdAndIsActiveTest() {
        CalendarException ce = new CalendarException(30L, null, 1L, ExceptionScope.SEMI_GLOBAL, LocalDate.of(2026, 6, 15), null, LocalTime.of(8, 0), LocalTime.of(15, 0), ExceptionType.CONFERENCE, "conference", false, true, true);

        when(calendarExceptionRepository.findSemiGlobalByDoctorIdAndIsActive(1L)).thenReturn(List.of(ce));

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findSemiGlobalByDoctorIdAndIsActive(1L);
        assertEquals(1, result.size());
        assertEquals(ExceptionScope.SEMI_GLOBAL, result.get(0).scope());
        assertEquals(1L, result.get(0).doctorId());

        verify(calendarExceptionRepository).findSemiGlobalByDoctorIdAndIsActive(1L);
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findSemiGlobalByDoctorIdAndIsActive_withNoResultsTest() {
        when(calendarExceptionRepository.findSemiGlobalByDoctorIdAndIsActive(99L)).thenReturn(List.of());

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findSemiGlobalByDoctorIdAndIsActive(99L);
        assertTrue(result.isEmpty());

        verify(calendarExceptionRepository).findSemiGlobalByDoctorIdAndIsActive(99L);
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findSpecificByDoctorCalendarIdAndIsActiveTest() {
        DoctorCalendar dc = createDC();
        CalendarException ce = createSpecificCE(dc);

        when(calendarExceptionRepository.findSpecificByDoctorCalendarIdAndIsActive(dc.getId())).thenReturn(List.of(ce));

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findSpecificByDoctorCalendarIdAndIsActive(dc.getId());
        assertEquals(1, result.size());
        assertEquals(ExceptionScope.SPECIFIC, result.get(0).scope());
        assertEquals(dc.getId(), result.get(0).doctorCalendarId());

        verify(calendarExceptionRepository).findSpecificByDoctorCalendarIdAndIsActive(dc.getId());
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findSpecificByDoctorCalendarIdAndIsActive_withNoResultsTest() {
        when(calendarExceptionRepository.findSpecificByDoctorCalendarIdAndIsActive(99L)).thenReturn(List.of());

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findSpecificByDoctorCalendarIdAndIsActive(99L);
        assertTrue(result.isEmpty());

        verify(calendarExceptionRepository).findSpecificByDoctorCalendarIdAndIsActive(99L);
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findByDoctorIdAndDateAndHourTest() {
        CalendarException ce = createGlobalCE();
        LocalDate date = LocalDate.of(2026, 12, 25);

        when(calendarExceptionRepository.findByDoctorIdAndDate(1L, date)).thenReturn(List.of(ce));

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findByDoctorIdAndDateAndHour(1L, date);
        assertEquals(1, result.size());
        assertEquals(ce.getId(), result.get(0).id());

        verify(calendarExceptionRepository).findByDoctorIdAndDate(1L, date);
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findByDoctorIdAndDateAndHour_withNoResultsTest() {
        LocalDate date = LocalDate.of(2026, 1, 1);

        when(calendarExceptionRepository.findByDoctorIdAndDate(1L, date)).thenReturn(List.of());

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findByDoctorIdAndDateAndHour(1L, date);
        assertTrue(result.isEmpty());

        verify(calendarExceptionRepository).findByDoctorIdAndDate(1L, date);
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findExistingCalendarExceptionCoincidenceTest() {
        CalendarException ce = createGlobalCE();
        LocalDate startDate = LocalDate.of(2026, 12, 20);
        LocalDate endDate = LocalDate.of(2026, 12, 28);

        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(1L, null, startDate, endDate, 0L)).thenReturn(List.of(ce));

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findExistingCalendarExceptionCoincidence(1L, null, startDate, endDate, 0L);
        assertEquals(1, result.size());
        assertEquals(ce.getId(), result.get(0).id());

        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(1L, null, startDate, endDate, 0L);
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void findExistingCalendarExceptionCoincidence_withNoResultsTest() {
        LocalDate startDate = LocalDate.of(2026, 1, 1);
        LocalDate endDate = LocalDate.of(2026, 1, 5);

        when(calendarExceptionRepository.findExistingCalendarExceptionCoincidence(1L, null, startDate, endDate, 0L)).thenReturn(List.of());

        List<CalendarExceptionResponseDto> result = calendarExceptionService.findExistingCalendarExceptionCoincidence(1L, null, startDate, endDate, 0L);
        assertTrue(result.isEmpty());

        verify(calendarExceptionRepository).findExistingCalendarExceptionCoincidence(1L, null, startDate, endDate, 0L);
        verifyNoMoreInteractions(calendarExceptionRepository);
    }


    /* =================== DELETE METHODS =================== */

    @Test
    void deleteCalendarExceptionByIdTest() {
        CalendarException existingCE = createGlobalCE();

        when(calendarExceptionRepository.findById(existingCE.getId())).thenReturn(Optional.of(existingCE));

        calendarExceptionService.deleteCalendarExceptionById(existingCE.getId());

        verify(calendarExceptionRepository).findById(existingCE.getId());
        verify(calendarExceptionRepository).delete(existingCE);
        verifyNoMoreInteractions(calendarExceptionRepository);
    }

    @Test
    void deleteCalendarExceptionById_withInexistentIdTest() {
        when(calendarExceptionRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFound exception = assertThrows(ResourceNotFound.class, () -> calendarExceptionService.deleteCalendarExceptionById(99L));

        assertEquals("Calendar Exception not found with id:99", exception.getMessage());
        verify(calendarExceptionRepository).findById(99L);
        verifyNoMoreInteractions(calendarExceptionRepository);
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

    private static CalendarException createGlobalCE() {

        LocalDate startDate = LocalDate.of(2026, 12, 25);

        return new CalendarException(
                25L,
                null,
                null,
                ExceptionScope.GLOBAL,
                startDate,
                null,
                null,
                null,
                ExceptionType.CUSTOM,
                "Christmas",
                true,
                true,
                true
        );
    }

    private static CalendarException createSpecificCE(DoctorCalendar dc) {
        return new CalendarException(
                35L,
                dc,
                dc.getDoctorId(),
                ExceptionScope.SPECIFIC,
                LocalDate.of(2026, 7, 10),
                null,
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                ExceptionType.UNAVAILABLE,
                "maintenance",
                false,
                true,
                true
        );
    }


}
