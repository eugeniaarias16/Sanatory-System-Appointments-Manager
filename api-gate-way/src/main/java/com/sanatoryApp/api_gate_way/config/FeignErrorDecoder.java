package com.sanatoryApp.api_gate_way.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Error in Feign call: {} - Status: {}", methodKey, response.status());
        //If it is 401 or 403 → invalid credentials
        if (response.status() == 401 || response.status() == 403) {
            return new RuntimeException("Invalid credentials");
        }

        // If it is 5xx → service unavailable
        if (response.status() >= 500) {
            return new RuntimeException("User Service not available");
        }

        //Any other error
        return new RuntimeException("Error in " + methodKey + " - Status: " + response.status());
    }
}

