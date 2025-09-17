package com.sanatoryApp.UserService.dto.Request;


import com.sanatoryApp.UserService.entity.Doctor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorCreateDto {

    @NotBlank(message = "First Name is mandatory")
    private String firstName;

    @NotBlank(message = "Last Name is mandatory")
    private String lastName;

    @Email(message = "Invalid Format")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid international phone format")
    @Schema(description = "Phone Number in international format ",example = "+5491112345678")
    private String phoneNumber;

    public Doctor toEntity(){
        Doctor doctor=new Doctor();
        doctor.setFirstName(firstName);
        doctor.setLastName(lastName);
        doctor.setEmail(email);
        doctor.setPhoneNumber(phoneNumber);
        return doctor;
    }
}
