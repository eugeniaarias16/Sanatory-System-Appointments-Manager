package com.sanatoryApp.AppointmentService.service;

import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeCreateDto;
import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeUpdateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentTypeResponseDto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IAppointmentTypeService {
    //BASIC CRUD
    AppointmentTypeResponseDto findAppointmentTypeById(Long id);
    AppointmentTypeResponseDto createAppointmentType(AppointmentTypeCreateDto dto);
    AppointmentTypeResponseDto updateAppointmentType(Long id, AppointmentTypeUpdateDto dto);
    void deleteAppointmentType(Long id);

    //OTHER METHODS
    AppointmentTypeResponseDto findAppointmentTypeByName(String name);
    List<AppointmentTypeResponseDto>findAppointmentTypeByLikeName(String name);
    List<AppointmentTypeResponseDto>findAppointmentTypeByRangeBasePrice(BigDecimal minPrice,BigDecimal maxPrice);
    boolean existsAppointmentTypeByName(String name);
}
