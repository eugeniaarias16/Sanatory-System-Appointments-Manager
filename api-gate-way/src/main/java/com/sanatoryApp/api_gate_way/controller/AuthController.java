package com.sanatoryApp.api_gate_way.controller;

import com.sanatoryApp.api_gate_way.dto.request.LoginRequest;
import com.sanatoryApp.api_gate_way.dto.response.AuthResponse;
import com.sanatoryApp.api_gate_way.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse>login(@Valid @RequestBody LoginRequest loginRequest){
        AuthResponse authResponse=authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }
}
