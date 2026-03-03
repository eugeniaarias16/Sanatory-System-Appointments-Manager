package com.sanatoryApp.AppointmentService.service;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeCreateDto;
import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeUpdateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentTypeResponseDto;
import com.sanatoryApp.AppointmentService.entity.AppointmentType;
import com.sanatoryApp.AppointmentService.exception.ResourceNotFound;
import com.sanatoryApp.AppointmentService.repository.IAppointmentTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AppointmentTypeServiceTest {

    @Mock
    private IAppointmentTypeRepository appointmentTypeRepository;

    @InjectMocks
    private AppointmentTypeService appointmentTypeService;

    /* =================== GET METHODS =================== */

    @Test
    void findAppointmentTypeByIdTest() {

        when(appointmentTypeRepository.findByIdAndActive(1L)).thenReturn(Optional.of(createAppointmentType()));

        AppointmentTypeResponseDto responseDto=appointmentTypeService.findAppointmentTypeById(1L);
        assertEquals("General Consultation",responseDto.name());
        assertEquals(15,responseDto.durationMin());
    }

    @Test
    void findInexistentAppointmentTypeByIdTest() {

        when(appointmentTypeRepository.findByIdAndActive(99L)).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->appointmentTypeService.findAppointmentTypeById(99L));

        assertEquals("Appointment Type not found with id 99",exception.getMessage());
        verify(appointmentTypeRepository).findByIdAndActive(anyLong());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void findAppointmentTypeByNameTest() {

        when(appointmentTypeRepository.findByName("General Consultation")).thenReturn(Optional.of(createAppointmentType()));

        AppointmentTypeResponseDto responseDto=appointmentTypeService.findAppointmentTypeByName("General Consultation");

        assertEquals(1L,responseDto.id());
        assertEquals(5,responseDto.bufferTimeMin());
        verify(appointmentTypeRepository).findByName(anyString());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void findInexistentAppointmentTypeByNameTest() {

        when(appointmentTypeRepository.findByName("Unknown")).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->appointmentTypeService.findAppointmentTypeByName("Unknown"));

        assertEquals("Appointment Type not found with name Unknown",exception.getMessage());
        verify(appointmentTypeRepository).findByName(anyString());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void findAppointmentTypeByLikeNameTest() {

        when(appointmentTypeRepository.findByLikeName("General")).thenReturn(List.of(createAppointmentType()));

        List<AppointmentTypeResponseDto> result=appointmentTypeService.findAppointmentTypeByLikeName("General");

        assertEquals(1,result.size());
        assertEquals("General Consultation",result.get(0).name());
        verify(appointmentTypeRepository).findByLikeName(anyString());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void findAppointmentTypeByLikeName_withNoResultsTest() {

        when(appointmentTypeRepository.findByLikeName("xyz")).thenReturn(List.of());

        List<AppointmentTypeResponseDto> result=appointmentTypeService.findAppointmentTypeByLikeName("xyz");

        assertTrue(result.isEmpty());
        verify(appointmentTypeRepository).findByLikeName(anyString());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void findAppointmentTypeByLikeName_withEmptyNameTest() {

        List<AppointmentTypeResponseDto> result=appointmentTypeService.findAppointmentTypeByLikeName("");

        assertTrue(result.isEmpty());
        verifyNoInteractions(appointmentTypeRepository);

    }

    @Test
    void findAppointmentTypeByRangePriceTest() {

        BigDecimal min=new BigDecimal("50");
        BigDecimal max=new BigDecimal("200");
        when(appointmentTypeRepository.findByRangeBasePrice(min,max)).thenReturn(List.of(createAppointmentType()));

        List<AppointmentTypeResponseDto> result=appointmentTypeService.findAppointmentTypeByRangeBasePrice(min,max);

        assertEquals(1,result.size());
        verify(appointmentTypeRepository).findByRangeBasePrice(any(BigDecimal.class),any(BigDecimal.class));
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void findAppointmentTypeByRangePrice_withNullMinPriceTest() {

        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,
                ()->appointmentTypeService.findAppointmentTypeByRangeBasePrice(null,new BigDecimal("200")));

        assertEquals("Price range cannot contain null values",exception.getMessage());
        verifyNoInteractions(appointmentTypeRepository);

    }

    @Test
    void findAppointmentTypeByRangePrice_withNullMaxPriceTest() {

        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,
                ()->appointmentTypeService.findAppointmentTypeByRangeBasePrice(new BigDecimal("50"),null));

        assertEquals("Price range cannot contain null values",exception.getMessage());
        verifyNoInteractions(appointmentTypeRepository);

    }

    @Test
    void findAppointmentTypeByRangePrice_withMinLessThanOneTest() {

        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,
                ()->appointmentTypeService.findAppointmentTypeByRangeBasePrice(new BigDecimal("0"),new BigDecimal("200")));

        assertEquals("Min Price must be at least 1",exception.getMessage());
        verifyNoInteractions(appointmentTypeRepository);

    }

    @Test
    void findAppointmentTypeByRangePrice_withMinGreaterThanMaxTest() {

        BigDecimal min=new BigDecimal("200");
        BigDecimal max=new BigDecimal("50");
        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,
                ()->appointmentTypeService.findAppointmentTypeByRangeBasePrice(min,max));

        assertEquals("Minimum price cannot be greater than maximum price. Min: 200, Max: 50",exception.getMessage());
        verifyNoInteractions(appointmentTypeRepository);

    }

    /* =================== POST METHODS =================== */

    @Test
    void createAppointmentTypeTest() {
        AppointmentTypeCreateDto createDto=new AppointmentTypeCreateDto(
                "General Consultation",
                "Standard consultation",
                15,
                5,
                new BigDecimal(100));

        when(appointmentTypeRepository.save(any(AppointmentType.class))).thenReturn(createAppointmentType());
        when(appointmentTypeRepository.existsByNameIgnoreCase("General Consultation")).thenReturn(false);

        AppointmentTypeResponseDto responseDto=appointmentTypeService.createAppointmentType(createDto);

        verify(appointmentTypeRepository).existsByNameIgnoreCase(anyString());
        verify(appointmentTypeRepository).save(any(AppointmentType.class));
        verifyNoMoreInteractions(appointmentTypeRepository);
    }

    @Test
    void createAppointmentType_withExistingNameTest() {
        AppointmentTypeCreateDto createDto=new AppointmentTypeCreateDto(
                "General Consultation",
                "Standard consultation",
                15,
                5,
                new BigDecimal(100));

        when(appointmentTypeRepository.existsByNameIgnoreCase("General Consultation")).thenReturn(true);
        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,()->appointmentTypeService.createAppointmentType(createDto));
        assertEquals("Appointment Type already exist with name General Consultation",exception.getMessage());

        verify(appointmentTypeRepository).existsByNameIgnoreCase(anyString());
        verifyNoMoreInteractions(appointmentTypeRepository);
    }

    @Test
    void createAppointmentType_withInvalidDurationMinTest() {

        AppointmentTypeCreateDto createDto=new AppointmentTypeCreateDto(
                "General Consultation",
                "Standard consultation",
                10,
                5,
                new BigDecimal(100));

        when(appointmentTypeRepository.existsByNameIgnoreCase("General Consultation")).thenReturn(false);
        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,()->appointmentTypeService.createAppointmentType(createDto));

        assertEquals("Appointments cannot last less than 15 minutes.",exception.getMessage());
        verify(appointmentTypeRepository).existsByNameIgnoreCase(anyString());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void createAppointmentType_withNegativeBufferTimeTest() {

        AppointmentTypeCreateDto createDto=new AppointmentTypeCreateDto(
                "General Consultation",
                "Standard consultation",
                15,
                -1,
                new BigDecimal(100));

        when(appointmentTypeRepository.existsByNameIgnoreCase("General Consultation")).thenReturn(false);
        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,()->appointmentTypeService.createAppointmentType(createDto));

        assertEquals("Buffer Time must be positive.",exception.getMessage());
        verify(appointmentTypeRepository).existsByNameIgnoreCase(anyString());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void createAppointmentType_withInvalidBasePriceTest() {

        AppointmentTypeCreateDto createDto=new AppointmentTypeCreateDto(
                "General Consultation",
                "Standard consultation",
                15,
                5,
                new BigDecimal("0"));

        when(appointmentTypeRepository.existsByNameIgnoreCase("General Consultation")).thenReturn(false);
        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,()->appointmentTypeService.createAppointmentType(createDto));

        assertEquals("Price must be at least 1",exception.getMessage());
        verify(appointmentTypeRepository).existsByNameIgnoreCase(anyString());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    /* =================== PATCH METHODS =================== */

    @Test
    void updateAppointmentTypeTest() {

        AppointmentType originalAppointmentType=createAppointmentType();
        AppointmentType updatedAppointmentType=createAppointmentType();
        updatedAppointmentType.setName("Ordinary Appointment");

        AppointmentTypeUpdateDto updateDto=new AppointmentTypeUpdateDto("Ordinary Appointment",null,null,null,null);

        when(appointmentTypeRepository.findByIdAndActive(1L)).thenReturn(Optional.of(originalAppointmentType));
        when(appointmentTypeRepository.existsByNameIgnoreCase("Ordinary Appointment")).thenReturn(false);
        when(appointmentTypeRepository.save(any(AppointmentType.class))).thenReturn(updatedAppointmentType);

        AppointmentTypeResponseDto responseDto=appointmentTypeService.updateAppointmentType(1L,updateDto);
        assertEquals("Ordinary Appointment",responseDto.name());
        assertEquals(1L,responseDto.id());

        verify(appointmentTypeRepository).findByIdAndActive(anyLong());
        verify(appointmentTypeRepository).existsByNameIgnoreCase(anyString());
        verify(appointmentTypeRepository).save(any(AppointmentType.class));
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void updateAppointmentType_withInexistentIdTest() {

        AppointmentTypeUpdateDto updateDto=new AppointmentTypeUpdateDto("Ordinary Appointment",null,null,null,null);

        when(appointmentTypeRepository.findByIdAndActive(99L)).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->appointmentTypeService.updateAppointmentType(99L,updateDto));

        assertEquals("Appointment Type not found with id 99",exception.getMessage());
        verify(appointmentTypeRepository).findByIdAndActive(anyLong());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void updateAppointmentType_withExistingNameTest() {

        AppointmentTypeUpdateDto updateDto=new AppointmentTypeUpdateDto("Existing Name",null,null,null,null);
        AppointmentType existingAppointmentType=createAppointmentType();

        when(appointmentTypeRepository.findByIdAndActive(1L)).thenReturn(Optional.of(existingAppointmentType));
        when(appointmentTypeRepository.existsByNameIgnoreCase("Existing Name")).thenReturn(true);
        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,()->appointmentTypeService.updateAppointmentType(1L,updateDto));

        assertEquals("Appointment Type already exists with name: Existing Name",exception.getMessage());
        verify(appointmentTypeRepository).findByIdAndActive(anyLong());
        verify(appointmentTypeRepository).existsByNameIgnoreCase(anyString());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void updateAppointmentType_withInvalidDurationMinTest() {

        AppointmentTypeUpdateDto updateDto=new AppointmentTypeUpdateDto(null,null,10,null,null);
        AppointmentType existingAppointmentType=createAppointmentType();

        when(appointmentTypeRepository.findByIdAndActive(1L)).thenReturn(Optional.of(existingAppointmentType));
        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,()->appointmentTypeService.updateAppointmentType(1L,updateDto));

        assertEquals("Appointments cannot last less than 15 minutes.",exception.getMessage());
        verify(appointmentTypeRepository).findByIdAndActive(anyLong());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void updateAppointmentType_withNegativeBufferTimeTest() {

        AppointmentTypeUpdateDto updateDto=new AppointmentTypeUpdateDto(null,null,null,-1,null);
        AppointmentType existingAppointmentType=createAppointmentType();

        when(appointmentTypeRepository.findByIdAndActive(1L)).thenReturn(Optional.of(existingAppointmentType));
        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,()->appointmentTypeService.updateAppointmentType(1L,updateDto));

        assertEquals("Buffer Time must be positive.",exception.getMessage());
        verify(appointmentTypeRepository).findByIdAndActive(anyLong());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void updateAppointmentType_withInvalidBasePriceTest() {

        AppointmentTypeUpdateDto updateDto=new AppointmentTypeUpdateDto(null,null,null,null,new BigDecimal("0"));
        AppointmentType existingAppointmentType=createAppointmentType();

        when(appointmentTypeRepository.findByIdAndActive(1L)).thenReturn(Optional.of(existingAppointmentType));
        IllegalArgumentException exception=assertThrows(IllegalArgumentException.class,()->appointmentTypeService.updateAppointmentType(1L,updateDto));

        assertEquals("Price must be at least 1",exception.getMessage());
        verify(appointmentTypeRepository).findByIdAndActive(anyLong());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void updateAppointmentType_withNullFieldsTest() {

        AppointmentTypeUpdateDto updateDto=new AppointmentTypeUpdateDto(null,null,null,null,null);
        AppointmentType existingAppointmentType=createAppointmentType();

        when(appointmentTypeRepository.findByIdAndActive(1L)).thenReturn(Optional.of(existingAppointmentType));
        when(appointmentTypeRepository.save(any(AppointmentType.class))).thenReturn(existingAppointmentType);

        AppointmentTypeResponseDto responseDto=appointmentTypeService.updateAppointmentType(1L,updateDto);

        assertEquals("General Consultation",responseDto.name());
        assertEquals(1L,responseDto.id());
        verify(appointmentTypeRepository).findByIdAndActive(anyLong());
        verify(appointmentTypeRepository).save(any(AppointmentType.class));
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    /* =================== DELETE METHODS =================== */

    @Test
    void deleteAppointmentTypeByIdTest() {

        AppointmentType existingAppointmentType=createAppointmentType();

        when(appointmentTypeRepository.findByIdAndActive(1L)).thenReturn(Optional.of(existingAppointmentType));
        when(appointmentTypeRepository.save(any(AppointmentType.class))).thenReturn(existingAppointmentType);

        appointmentTypeService.deleteAppointmentType(1L);

        assertFalse(existingAppointmentType.isActive());
        verify(appointmentTypeRepository).findByIdAndActive(anyLong());
        verify(appointmentTypeRepository).save(any(AppointmentType.class));
        verifyNoMoreInteractions(appointmentTypeRepository);

    }

    @Test
    void deleteInexistentAppointmentTypeByIdTest() {

        when(appointmentTypeRepository.findByIdAndActive(99L)).thenReturn(Optional.empty());
        ResourceNotFound exception=assertThrows(ResourceNotFound.class,()->appointmentTypeService.deleteAppointmentType(99L));

        assertEquals("Appointment Type not found with id 99",exception.getMessage());
        verify(appointmentTypeRepository).findByIdAndActive(anyLong());
        verifyNoMoreInteractions(appointmentTypeRepository);

    }


    /* =================== HELPER METHODS =================== */
    private static AppointmentType createAppointmentType(){
        return new AppointmentType(
                1L,
                "General Consultation",
                "Standard consultation",
                15,
                5,
                new BigDecimal(100),
                true
        );
    }
}
