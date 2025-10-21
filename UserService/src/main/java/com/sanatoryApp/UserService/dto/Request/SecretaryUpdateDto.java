package com.sanatoryApp.UserService.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SecretaryUpdateDto(

        String dni,
        String firstName,
        String lastName,
        @Email(message = "Invalid Format")  String email
) { }
