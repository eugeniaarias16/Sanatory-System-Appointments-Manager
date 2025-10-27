package com.sanatoryApp.CalendarService.controller;

import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionCreateDto;
import com.sanatoryApp.CalendarService.dto.Request.CalendarExceptionUpdateDto;
import com.sanatoryApp.CalendarService.dto.Response.CalendarExceptionResponseDto;
import com.sanatoryApp.CalendarService.service.ICalendarExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/calendarException")
@RequiredArgsConstructor
public class CalendarExceptionController {

    private final ICalendarExceptionService calendarExceptionService;

    /*  GET ENDPOINTS  */
    @GetMapping("/{id}")
    public ResponseEntity <CalendarExceptionResponseDto> findById(@PathVariable Long id){
        return ResponseEntity.ok(calendarExceptionService.findById(id));
    }

    @GetMapping("/doctorCalendar/{id}")
    public ResponseEntity <List<CalendarExceptionResponseDto>> findByDoctorCalendarId(@PathVariable Long doctorCalendarId){
        return ResponseEntity.ok(calendarExceptionService.findByDoctorCalendarId(doctorCalendarId));
    }

    @GetMapping("/search")
    public ResponseEntity <List<CalendarExceptionResponseDto>>findApplicableExceptionsInTimeRange(@RequestParam Long calendarId,
                                                                                                  @RequestParam Long doctorId,
                                                                                                  @RequestParam LocalDate startTime,
                                                                                                  @RequestParam LocalDate endTime){
        return ResponseEntity.ok(calendarExceptionService.findApplicableExceptionsInTimeRange(calendarId,doctorId,startTime,endTime));
    }

    @GetMapping("/search")
    public ResponseEntity <List<CalendarExceptionResponseDto>> findApplicableExceptionsForCalendar(@RequestParam Long calendarId,
                                                                                                   @RequestParam Long doctorId,
                                                                                                   @RequestParam LocalDate date){
        return ResponseEntity.ok(calendarExceptionService.findApplicableExceptionsForCalendar(calendarId,doctorId,date));
    }

    @GetMapping("/globalException/doctor/{doctorId}")
    public ResponseEntity <List<CalendarExceptionResponseDto>>findGlobalExceptionsByDoctorId(@PathVariable Long doctorId){
        return ResponseEntity.ok(calendarExceptionService.findGlobalExceptionsByDoctorId(doctorId));

    }

    @GetMapping("/search")
    public ResponseEntity <List<CalendarExceptionResponseDto>> findFutureExceptions(@RequestParam Long calendarId,
                                                                                    @RequestParam LocalDate currentDate){
        return ResponseEntity.ok(calendarExceptionService.findFutureExceptions(calendarId,currentDate));
    }

    /*  PUT ENDPOINT  */
    @PutMapping("/create")
    public ResponseEntity <CalendarExceptionResponseDto> createCalendarException(@RequestBody CalendarExceptionCreateDto dto){
        return ResponseEntity.ok(calendarExceptionService.createCalendarException(dto));
    }

    /*  PATCH ENDPOINT  */
    @PatchMapping("/update/{id}")
    public ResponseEntity <CalendarExceptionResponseDto> updateCalendarException(@PathVariable Long id,
                                                                                 @RequestBody CalendarExceptionUpdateDto dto){
        return ResponseEntity.ok(calendarExceptionService.updateCalendarException(id,dto));
    }

    /*  DELETE ENDPOINT  */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCalendarException(@PathVariable Long id){
        calendarExceptionService.deleteCalendarException(id);
        return ResponseEntity.ok("Calendar Exception with id "+id+" successfully deleted");
    }


}
