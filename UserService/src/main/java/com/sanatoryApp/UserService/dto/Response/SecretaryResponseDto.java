package com.sanatoryApp.UserService.dto.Response;

import com.sanatoryApp.UserService.entity.Secretary;

public record SecretaryResponseDto(
        Long id,
        String dni,
        String firstName,
        String lastName,
        String email
) {
    public static SecretaryResponseDto fromEntity(Secretary secretary){
        return new SecretaryResponseDto(
                secretary.getId(),
                secretary.getDni(),
                secretary.getFirstName(),
                secretary.getLastName(),
                secretary.getEmail()
        );
    }
}
