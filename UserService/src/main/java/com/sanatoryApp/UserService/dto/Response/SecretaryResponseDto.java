package com.sanatoryApp.UserService.dto.Response;

public record SecretaryResponseDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
