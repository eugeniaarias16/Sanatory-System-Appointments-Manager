package com.sanatoryApp.AppointmentService.controller;


import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeCreateDto;
import com.sanatoryApp.AppointmentService.dto.Request.AppointmentTypeUpdateDto;
import com.sanatoryApp.AppointmentService.dto.Response.AppointmentTypeResponseDto;
import com.sanatoryApp.AppointmentService.service.IAppointmentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/appointmentType")
@RequiredArgsConstructor
public class AppointmentTypeController {

    private final IAppointmentTypeService appointmentTypeService;


    /*  GET ENDPOINT  */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentTypeResponseDto> findAppointmentTypeById(@PathVariable Long id){
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<AppointmentTypeResponseDto> findAppointmentTypeByName(@PathVariable String name){
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeByName(name));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AppointmentTypeResponseDto>> findAppointmentTypeByLikeName(@RequestParam String name){
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeByLikeName(name));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AppointmentTypeResponseDto>>findAppointmentTypeByRangeBasePrice(@RequestParam BigDecimal minPrice,@RequestParam BigDecimal maxPrice){
        return ResponseEntity.ok(appointmentTypeService.findAppointmentTypeByRangeBasePrice(minPrice,maxPrice));
    }


    /*  PUT ENDPOINT  */
    @PutMapping("/create")
    public ResponseEntity<AppointmentTypeResponseDto> createAppointmentType(@RequestBody AppointmentTypeCreateDto dto){
        return ResponseEntity.ok(appointmentTypeService.createAppointmentType(dto));
    }

    /*  PATCH ENDPOINT  */
    @PatchMapping("/update/{id}")
    public ResponseEntity<AppointmentTypeResponseDto> updateAppointmentType(@PathVariable Long id,@RequestBody AppointmentTypeUpdateDto dto){
        return ResponseEntity.ok(appointmentTypeService.updateAppointmentType(id,dto));
    }

    /*  DELETE ENDPOINT  */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteAppointmentType(@PathVariable Long id){
        appointmentTypeService.deleteAppointmentType(id);
        return ResponseEntity.ok("Appointment Type successfully deleted");
    }

}
