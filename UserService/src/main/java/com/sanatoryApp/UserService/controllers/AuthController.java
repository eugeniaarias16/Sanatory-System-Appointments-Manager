package com.sanatoryApp.UserService.controllers;

import com.sanatoryApp.UserService.dto.Request.ValidateCredentialsRequest;
import com.sanatoryApp.UserService.dto.Response.ValidateCredentialsResponse;
import com.sanatoryApp.UserService.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /*
     * Validates user credentials
     * This endpoint is called by the API Gateway via Feign.
     * It should NOT be directly accessible from the internet.
     */
    @Operation(summary = "Validate user credentials (internal use)")
    @PostMapping("/validate")
    public ResponseEntity<ValidateCredentialsResponse>validateCredentials(@Valid @RequestBody ValidateCredentialsRequest request) {
        ValidateCredentialsResponse response= authService.validateCredentials(request);
        return ResponseEntity.ok(response);
    }


}
