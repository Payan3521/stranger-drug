package com.gateway.gateway.microserviceUsers.jwt.application.service;

import java.security.Key;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gateway.gateway.common.logging.LoggingService;
import com.gateway.gateway.microserviceUsers.jwt.domain.exception.InvalidTokenException;
import com.gateway.gateway.microserviceUsers.jwt.domain.model.UserContext;
import com.gateway.gateway.microserviceUsers.jwt.domain.port.IJwtValidationService;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtValidationService implements IJwtValidationService {

    private final LoggingService loggingService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public UserContext validateToken(String token) {
        loggingService.logSecurity("JwtValidationService: Iniciando validación de token JWT");
        
        if (token == null || token.trim().isEmpty()) {
            loggingService.logSecurityWarning("JwtValidationService: Token JWT es null o vacío");
            throw new InvalidTokenException("Token no proporcionado");
        }

        try {
            loggingService.logGatewayDebug("JwtValidationService: Procesando token JWT - Longitud: {}", token.length());
            
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            String role = claims.get("rol", String.class);

            loggingService.logSecurity("JwtValidationService: Token validado exitosamente - Usuario: {}, Rol: {}", email, role);
            loggingService.logGatewayDebug("JwtValidationService: Claims extraídos - Email: {}, Rol: {}, IssuedAt: {}, Expiration: {}", 
                email, role, claims.getIssuedAt(), claims.getExpiration());

            UserContext userContext = UserContext.builder()
                    .userId(email)
                    .email(email)
                    .role(role)
                    .isAuthenticated(true)
                    .build();

            loggingService.logGatewayDebug("JwtValidationService: UserContext creado exitosamente - Usuario: {}, Autenticado: {}", 
                userContext.getEmail(), userContext.isAuthenticated());

            return userContext;

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            loggingService.logSecurityError("JwtValidationService: Token JWT expirado - Error: {}", e.getMessage());
            throw new InvalidTokenException("Token expirado: " + e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            loggingService.logSecurityError("JwtValidationService: Token JWT malformado - Error: {}", e.getMessage());
            throw new InvalidTokenException("Token malformado: " + e.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            loggingService.logSecurityError("JwtValidationService: Token JWT no soportado - Error: {}", e.getMessage());
            throw new InvalidTokenException("Token no soportado: " + e.getMessage());
        } catch (io.jsonwebtoken.security.SignatureException e) {
            loggingService.logSecurityError("JwtValidationService: Firma del token JWT inválida - Error: {}", e.getMessage());
            throw new InvalidTokenException("Firma del token inválida: " + e.getMessage());
        } catch (Exception e) {
            loggingService.logSecurityError("JwtValidationService: Error inesperado al validar token JWT - Error: {}", e.getMessage());
            throw new InvalidTokenException("Error al validar token: " + e.getMessage());
        }
    }

    @Override
    public boolean isTokenValid(String token) {
        loggingService.logGatewayDebug("JwtValidationService: Verificando si token es válido");
        
        try {
            validateToken(token);
            loggingService.logGatewayDebug("JwtValidationService: Token es válido");
            return true;
        } catch (Exception e) {
            loggingService.logSecurityWarning("JwtValidationService: Token no es válido - Error: {}", e.getMessage());
            return false;
        }
    }
}
