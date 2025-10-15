package com.sanatoryApp.CalendarService.dto.Request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sanatoryApp.CalendarService.entity.DoctorCalendar;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZoneId;

@Data
public class DoctorCalendarCreateDto {

        @NotNull(message = "Doctor ID is mandatory")
        @JsonProperty("doctor_id")
        private Long doctorId;

        @NotBlank(message = "Name is mandatory")
        private String name;

        @NotBlank(message = "Time zone is mandatory")
        @JsonProperty("time_zone")
        private String timeZone;  // Receive String from JSON


        public DoctorCalendar toEntity() {
                DoctorCalendar calendar = new DoctorCalendar();
                calendar.setDoctorId(this.doctorId);
                calendar.setName(this.name.toLowerCase());
                calendar.setTimeZone(ZoneId.of(this.timeZone));  //  String → ZoneId
                calendar.setActive(true);
                return calendar;
        }
}
