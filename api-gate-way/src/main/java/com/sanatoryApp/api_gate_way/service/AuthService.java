package com.sanatoryApp.api_gate_way.service;

import com.sanatoryApp.api_gate_way.client.UserServiceClient;
import com.sanatoryApp.api_gate_way.dto.request.LoginRequest;
import com.sanatoryApp.api_gate_way.dto.request.ValidateCredentialsRequest;
import com.sanatoryApp.api_gate_way.dto.response.AuthResponse;
import com.sanatoryApp.api_gate_way.dto.response.ValidateCredentialsResponse;
import com.sanatoryApp.api_gate_way.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtils jwtUtils;
    private final UserServiceClient userService;


    public AuthResponse login(LoginRequest loginRequest){
        try{
            //LoginRequest -> ValidateCredentialsRequest
            ValidateCredentialsRequest validateRequest = new ValidateCredentialsRequest(loginRequest.username(), loginRequest.password());

            //Call UserService to validate credential
            ValidateCredentialsResponse response = userService.validateCredentials(validateRequest);

            //Verify  credentials
            if (!response.valid()) {
                throw new RuntimeException("Invalid Credentials.");
            }

            //Generate Token
            String token = jwtUtils.createToken(response);

            // Generate response with token
            return new AuthResponse(
                    token,
                    jwtUtils.getExpirationMillis(),
                    response.username(),
                    response.userId()
            );
        }catch (RuntimeException e){
            throw new RuntimeException("Error while authenticating: "+e.getMessage(),e);
        }

    }


}
