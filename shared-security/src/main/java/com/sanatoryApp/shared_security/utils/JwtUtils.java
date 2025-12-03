package com.sanatoryApp.shared_security.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.issuer}")
    private String issuer;

    private Algorithm algorithm;

    @PostConstruct
    public void initAlgorithm() {
        this.algorithm = Algorithm.HMAC256(secretKey);
    }

    // Validate Jwt token
    public boolean validateToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            return true;
        } catch (JWTVerificationException ex) {
            log.error("Failed to validate JWT: {}", ex.getMessage());
            return false;
        }
    }


    //Decode Jwt token
    public DecodedJWT decodedJWT(String token) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException ex) {
            log.error("Failed to decode JWT: {}", ex.getMessage());
            throw new JWTVerificationException("Invalid token");
        }
    }

    // Get roles from token
    public List<String> getRolesFromToken(String token){
        DecodedJWT decodedJWT=decodedJWT(token);
        return decodedJWT.getClaim("roles").asList(String.class);
    }

    //Get username from token
    public String getUsernameFromToken(String token){
        DecodedJWT decodedJWT=decodedJWT(token);
        return  decodedJWT.getSubject();
    }


    //Get userId from token
    public Long getUserIdFromToken(String token){
        DecodedJWT decodedJWT=decodedJWT(token);
        return decodedJWT.getClaim("userId").asLong();
    }

}