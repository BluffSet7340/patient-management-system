package com.pm.auth_service.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    private final Key secretKey;

    // injecting secretKey via env variables
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Base64.getDecoder()
                .decode(secret.getBytes(StandardCharsets.UTF_8));

        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(secretKey)
                .compact();
    }

    public void validateToken(String token) {
        // okay so there are the steps right here

        // create a parser
        // configure it - "use this key when you verify"
        // build the configured parser
        // NOW actually do the work

        try {
            Jwts.parser().verifyWith((SecretKey) secretKey) // secretkey comes from the env variables
                    .build().parseSignedClaims(token); // the token is the entire string separated by dots
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Invalid JWT signature!");
        } catch (JwtException e) {
            throw new JwtException("Invalid JWT");
        }

    }
}
