package com.gateway.gateway.application.service;

import java.security.Key;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.gateway.gateway.domain.exception.InvalidTokenException;
import com.gateway.gateway.domain.model.UserContext;
import com.gateway.gateway.domain.port.IJwtValidationService;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class JwtValidationService implements IJwtValidationService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public UserContext validateToken(String token) {
        try {
            log.debug("Validando token JWT");
            
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            String role = claims.get("rol", String.class);

            log.debug("Token validado exitosamente para usuario: {}, rol: {}", email, role);

            return UserContext.builder()
                    .userId(email)
                    .email(email)
                    .role(role)
                    .isAuthenticated(true)
                    .build();

        } catch (Exception e) {
            log.error("Error al validar token JWT: {}", e.getMessage());
            throw new InvalidTokenException("Token inválido: " + e.getMessage());
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            validateToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
