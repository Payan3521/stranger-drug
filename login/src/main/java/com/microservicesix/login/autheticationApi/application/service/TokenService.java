package com.microservicesix.login.autheticationApi.application.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.microservicesix.login.autheticationApi.domain.model.User;
import com.microservicesix.login.autheticationApi.domain.port.in.ITokenService;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService implements ITokenService {


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration:2592000000}") // 30 días por defecto
    private long refreshExpiration;

    @Override
    public String generateAccessToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("rol", user.getRole().name());
        claims.put("name", user.getName());
        claims.put("lastName", user.getLastName());
        claims.put("type", "access");

        return createToken(claims, user.getEmail(), jwtExpiration);
    }

    @Override
    public String generateRefreshToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", user.getEmail());
        claims.put("type", "refresh");
        claims.put("jti", UUID.randomUUID().toString()); // JWT ID único

        return createToken(claims, user.getEmail(), refreshExpiration);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String extractEmailFromToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Error al extraer email del token", e);
        }
    }

    @Override
    public Long getTokenExpirationTime() {
        return jwtExpiration / 1000; // Retornar en segundos
    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}