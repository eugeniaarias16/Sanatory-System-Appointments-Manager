package com.sanatoryApp.UserService.dto.Response;

import java.util.List;

public record ValidateCredentialsResponse(
        boolean valid,
        Long userId,
        String username,
        List<String>roles
) {

    public static ValidateCredentialsResponse invalid() {
        return new ValidateCredentialsResponse(false, null, null, List.of());
    }
}
