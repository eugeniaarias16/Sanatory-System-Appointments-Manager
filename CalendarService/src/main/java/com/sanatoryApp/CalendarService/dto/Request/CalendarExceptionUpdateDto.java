package com.sanatoryApp.CalendarService.dto.Request;

import com.sanatoryApp.CalendarService.entity.ExceptionScope;
import com.sanatoryApp.CalendarService.entity.ExceptionType;
import jakarta.validation.constraints.Future;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@NoArgsConstructor
public class CalendarExceptionUpdateDto {
    private JsonNullable<Long> doctorCalendarId = JsonNullable.undefined();
    private JsonNullable<Long> doctorId = JsonNullable.undefined();
    private JsonNullable<@Future LocalDate> startDate = JsonNullable.undefined();
    private JsonNullable<LocalDate> endDate = JsonNullable.undefined();
    private JsonNullable<LocalTime> startTime = JsonNullable.undefined();
    private JsonNullable<LocalTime> endTime = JsonNullable.undefined();
    private JsonNullable<ExceptionType> exceptionType = JsonNullable.undefined();
    private JsonNullable<ExceptionScope> scope = JsonNullable.undefined();
    private JsonNullable<String> reason = JsonNullable.undefined();
}
