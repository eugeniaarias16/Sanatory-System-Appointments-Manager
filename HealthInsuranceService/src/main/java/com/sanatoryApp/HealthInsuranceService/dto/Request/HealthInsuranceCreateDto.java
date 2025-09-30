package com.sanatoryApp.HealthInsuranceService.dto.Request;

import com.sanatoryApp.HealthInsuranceService.entity.HealthInsurance;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Dto to represent requests to create Health Insurance Entity.")
public class HealthInsuranceCreateDto {


    @NotBlank(message = "Company's name is mandatory")
    @Size(min = 5,max = 50, message = "The name must be between 5 and 50 characters long.")
    private String companyName;
    @Positive(message = "Company's Code must be positive.")
    private Long companyCode;
    @NotBlank
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Invalid international phone format")
    @Schema(description = "Phone Number in international format ",example = "+5491112345678")
    private String phoneNumber;
    @NotBlank(message = "Company's email is mandatory.")
    @Email(message = "Invalid format.")
    @Schema(description = "Contact email", example = "contact@insurance.com")
    private String email;



    public HealthInsurance toEntity(){
        HealthInsurance healthInsurance= new HealthInsurance();
        healthInsurance.setCompanyCode(companyCode);
        healthInsurance.setCompanyName(companyName);
        healthInsurance.setPhoneNumber(phoneNumber);
        healthInsurance.setEmail(email);
        healthInsurance.setActive(true);
        return healthInsurance;
    }

}
