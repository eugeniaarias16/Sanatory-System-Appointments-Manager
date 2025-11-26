package com.sanatoryApp.api_gate_way.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ValidateCredentialsRequest(
        @NotBlank(message = "The username cannot be empty.")
        String username,
        @NotBlank(message = "The password cannot be empty.")
        String password
) { }
