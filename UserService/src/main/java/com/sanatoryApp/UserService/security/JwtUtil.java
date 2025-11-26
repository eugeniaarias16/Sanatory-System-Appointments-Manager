package com.sanatoryApp.UserService.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.issuer}")
    private String issuer;

    private Algorithm algorithm;

    @PostConstruct
    public void initAlgorithm(){
        this.algorithm=Algorithm.HMAC256(secretKey);
    }

    //validate token
    public boolean validateToken(String token){
        try {
            JWTVerifier verifier= JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            verifier.verify(token);
            return true;

        }catch (JWTVerificationException exception){
            log.error("Invalid JWT token: {}",exception.getMessage());
            return false;
        }
    }

    //decode token
    public DecodedJWT decodedJWT(String token){
        try {
            JWTVerifier verifier=JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            return verifier.verify(token);
        }catch (JWTVerificationException exception){
            log.error("Failed to decode JWT: {}", exception.getMessage());
            throw new JWTVerificationException("Invalid token");
        }
    }


    //Extract Claim's Methods from Token
    public String getUsernameFromToken(String token){
        DecodedJWT decodedJWT=decodedJWT(token);
        return decodedJWT.getSubject();
    }

    public List<String> getRolesFromToken(String token){
        DecodedJWT decodedJWT=decodedJWT(token);
        return decodedJWT.getClaim("roles").asList(String.class);
    }

    public Long getUserIdFromToken(String token){
        DecodedJWT decodedJWT=decodedJWT(token);
        return decodedJWT.getClaim("userId").asLong();
    }

    public Claim extractSpecificClaim(String token,String claimName){
        DecodedJWT decodedJWT=decodedJWT(token);
        return decodedJWT.getClaim(claimName);
    }


}
