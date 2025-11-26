package com.sanatoryApp.api_gate_way.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sanatoryApp.api_gate_way.dto.response.ValidateCredentialsResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtils {

    // Import properties values
   @Value("${app.jwt.secret}")
    private String secretKey;
    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.expiration-minutes}")
    private Integer expirationMin;

    private Algorithm algorithm;

    @PostConstruct
    public void initAlgorithm(){
        this.algorithm=Algorithm.HMAC256(secretKey);
    }

    // Create Token
    public String createToken(ValidateCredentialsResponse response){
        Date today= new Date();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(response.username())
                .withClaim("userId", response.userId())
                .withClaim("roles", response.roles())
                .withIssuedAt(today)
                .withNotBefore(today)
                .withExpiresAt(expiryDate(expirationMin))
                .withJWTId(UUID.randomUUID().toString())
                .sign(algorithm);

    }



    // Decode token
    public DecodedJWT decodeToken(String token){
        try {
            JWTVerifier verifier=JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            return verifier.verify(token);
        }catch (JWTVerificationException exception){
            throw new JWTVerificationException("Invalid Token");
        }
    }

    /*
     * Convert minutes to an expiry date
     * @return Date with the expiry date
     */
    public Date expiryDate(Integer min){
        int minutes=(min==null)?0:min;
        int minutesToMills=minutes*60000;
        return new Date(System.currentTimeMillis()+minutesToMills);
    }

    public Long getExpirationMillis() {
        return expirationMin * 60000L;
    }

/**
    //METHODS TO REFRESH TOKEN
    // Validate Token
    public boolean validateToken(String token){
        try {
            JWTVerifier verifier=JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception){
            return false;
        }
    }
    // Get claims
    public Long getUserIdFromToken(String token){
        DecodedJWT decodedJWT=decodeToken(token);
        return decodedJWT.getClaim("userId").asLong();
    }

    public String getUsernameFromToken(String token){
        DecodedJWT decodedJWT=decodeToken(token);
        return decodedJWT.getSubject();
    }

    public List<String> getRolesFromToken(String token){
        DecodedJWT decodedJWT=decodeToken(token);
        return decodedJWT.getClaim("roles").asList(String.class);
    }

    public Claim extractEspecificClaim(String token, String claimName){
        DecodedJWT decodedJWT=decodeToken(token);
        return decodedJWT.getClaim(claimName);
    }
*/




}
