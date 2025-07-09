package com.microservicesix.login.core.config.internalSecurity;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InternalJwtService {
    
    
    @Value("${internal-jwt.secret}")
    private String secret; 

    public String generateToken(String issuerServiceName) {
        
        String token = Jwts.builder()
                .setSubject("internal-call")
                .setIssuer(issuerServiceName)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();
                
        return token;
    }
}
