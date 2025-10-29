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
@Schema(description = "DTO for creating a new Health Insurance entity")
public class HealthInsuranceCreateDto {

    @NotBlank(message = "Company name is required")
    @Size(min = 5, max = 50, message = "Company name must be between 5 and 50 characters")
    @Schema(description = "Name of the insurance company", example = "Blue Cross Health Insurance")
    private String companyName;

    @NotNull(message = "Company code is required")
    @Positive(message = "Company code must be positive")
    @Schema(description = "Unique company identification code", example = "12345")
    private Long companyCode;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in international format (E.164)")
    @Schema(description = "Contact phone number in international format", example = "+5491112345678")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "Contact email address", example = "contact@insurance.com")
    private String email;

    public HealthInsurance toEntity() {
        HealthInsurance healthInsurance = new HealthInsurance();
        healthInsurance.setCompanyCode(this.companyCode);
        healthInsurance.setCompanyName(this.companyName);
        healthInsurance.setPhoneNumber(this.phoneNumber);
        healthInsurance.setEmail(this.email);
        healthInsurance.setActive(true);
        return healthInsurance;
    }
}