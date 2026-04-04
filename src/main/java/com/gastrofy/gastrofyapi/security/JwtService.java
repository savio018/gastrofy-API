package com.gastrofy.gastrofyapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.security.Key;
import java.util.function.Function;

@Service
public class JwtService {

    private final Key signingKey;
    private final long expirationMillis;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expirationMillis
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMillis = expirationMillis;
    }

    public String gerarToken(String email) {
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(expirationMillis);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(java.util.Date.from(now))
                .setExpiration(java.util.Date.from(expiryDate))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    public boolean tokenValido(String token, String emailEsperado) {
        String email = extrairEmail(token);
        return email != null && email.equals(emailEsperado) && !estaExpirado(token);
    }

    private boolean estaExpirado(String token) {
        Instant expiration = extrairClaim(token, Claims::getExpiration).toInstant();
        return expiration.isBefore(Instant.now());
    }

    private <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }
}

