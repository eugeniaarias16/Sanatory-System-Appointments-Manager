package com.sanatoryApp.api_gate_way.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn,

        String username,

        @JsonProperty("user_id")
        Long userId
) {
    public AuthResponse(String accessToken, Long expiresIn, String username, Long userId) {
        this(accessToken, "Bearer", expiresIn, username, userId);
    }
}
