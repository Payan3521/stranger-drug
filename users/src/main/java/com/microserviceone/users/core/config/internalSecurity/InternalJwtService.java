package com.microserviceone.users.core.config.internalSecurity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import com.microserviceone.users.core.logging.LoggingService;

@Component
public class InternalJwtService {
    
    private final LoggingService loggingService;
    
    @Value("${internal-jwt.secret}")
    private String secret; 

    public InternalJwtService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    public String generateToken(String issuerServiceName) {
        loggingService.logDebug("InternalJwtService: Generando token JWT interno para servicio: {}", issuerServiceName);
        
        String token = Jwts.builder()
                .setSubject("internal-call")
                .setIssuer(issuerServiceName)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
                
        loggingService.logInfo("InternalJwtService: Token JWT interno generado exitosamente - Servicio: {}, Longitud: {}", 
            issuerServiceName, token.length());
            
        return token;
    }
}