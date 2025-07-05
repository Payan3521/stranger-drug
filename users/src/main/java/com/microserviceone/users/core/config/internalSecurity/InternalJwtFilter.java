package com.microserviceone.users.core.config.internalSecurity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class InternalJwtFilter extends OncePerRequestFilter {

    @Value("${internal-jwt.secret}")
    private String secret;

    // Endpoints que requieren token JWT interno
    private final List<String> protectedPaths = Arrays.asList(
        "/verification/status",
        "/terms/accept/multiple"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        
        // LOG PARA DEBUG
        System.out.println("[JWT Filter] Processing request: " + requestPath);
        
        // Solo validar JWT en endpoints protegidos
        if (!requiresJwtValidation(requestPath)) {
            System.out.println("[JWT Filter] Path not protected, continuing...");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("[JWT Filter] Protected path detected, validating JWT...");

        String authHeader = request.getHeader("Authorization");
        System.out.println("[JWT Filter] Authorization header: " + (authHeader != null ? "Present" : "Missing"));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT Filter] Missing or invalid Authorization header");
            response.setContentType("application/json");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } 

        String token = authHeader.replace("Bearer ", "");
        System.out.println("[JWT Filter] Token received: " + token.substring(0, Math.min(20, token.length())) + "...");

        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token);

            // Validar issuer (corregido el nombre del servicio)
            String issuer = claims.getBody().getIssuer();
            System.out.println("[JWT Filter] Token issuer: " + issuer);
            
            if (!"user-service".equals(issuer)) {
                System.out.println("[JWT Filter] Invalid issuer: " + issuer);
                throw new JwtException("Issuer inválido: " + issuer);
            }

            System.out.println("[JWT Filter] JWT validation successful");

        } catch (JwtException e) {
            System.out.println("[JWT Filter] JWT validation failed: " + e.getMessage());
            response.setContentType("application/json");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
                "{\"error\": \"Token inválido: " + e.getMessage());
            return;
        }

        System.out.println("[JWT Filter] Proceeding with request...");
        filterChain.doFilter(request, response);
    }

    private boolean requiresJwtValidation(String requestPath) {
        // CAMBIO CRÍTICO: usar equals() en lugar de startsWith()
        boolean requires = protectedPaths.stream().anyMatch(path -> requestPath.equals(path));
        System.out.println("[JWT Filter] Path '" + requestPath + "' requires JWT: " + requires);
        return requires;
    }
}