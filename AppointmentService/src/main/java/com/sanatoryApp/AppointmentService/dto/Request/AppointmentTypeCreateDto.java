package com.sanatoryApp.AppointmentService.dto.Request;

import com.sanatoryApp.AppointmentService.entity.AppointmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class AppointmentTypeCreateDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private int durationMin;
    @NotNull
    private int bufferTimeMin;
    @NotNull
    @Positive
    private BigDecimal basePrice;

    public AppointmentType toEntity(){
        AppointmentType appointmentType=new AppointmentType();
        appointmentType.setName(name);
        appointmentType.setDescription(description);
        appointmentType.setDurationMin(durationMin);
        appointmentType.setBufferTimeMin(bufferTimeMin);
        appointmentType.setBasePrice(basePrice);
        appointmentType.setActive(true);
        return appointmentType;
    }

}
