package com.sanatoryApp.UserService.dto.Request;

import com.sanatoryApp.UserService.entity.Secretary;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


public record SecretaryCreateDto(
        @NotBlank(message = "Dni is mandatory")
        String dni,

        @NotBlank(message = "First Name is mandatory")
        String firstName,

        @NotBlank(message = "Last Name is mandatory")
        String lastName,

        @Email(message = "Invalid Format")
        @NotBlank(message = "Email is mandatory")
        String email
) {


    public Secretary toEntity() {
        Secretary secretary = new Secretary();
        secretary.setDni(dni.trim());
        secretary.setFirstName(firstName);
        secretary.setLastName(lastName);
        secretary.setEmail(email.trim().toLowerCase());

        return secretary;
    }
}
