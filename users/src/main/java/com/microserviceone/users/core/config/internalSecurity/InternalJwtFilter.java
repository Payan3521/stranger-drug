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
import com.microserviceone.users.core.logging.LoggingService;

@Component
public class InternalJwtFilter extends OncePerRequestFilter {

    private final LoggingService loggingService;

    @Value("${internal-jwt.secret}")
    private String secret;

    // Endpoints que requieren token JWT interno
    private final List<String> protectedPaths = Arrays.asList(
        "/verification/status",
        "/terms/accept/multiple"
    );

    public InternalJwtFilter(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();
        
        loggingService.logDebug("InternalJwtFilter: Procesando solicitud: {}", requestPath);
        
        // Solo validar JWT en endpoints protegidos
        if (!requiresJwtValidation(requestPath)) {
            loggingService.logDebug("InternalJwtFilter: Path no protegido, continuando sin validación JWT");
            filterChain.doFilter(request, response);
            return;
        }

        loggingService.logDebug("InternalJwtFilter: Path protegido detectado, validando JWT interno");

        String authHeader = request.getHeader("Authorization");
        loggingService.logDebug("InternalJwtFilter: Header Authorization: {}", authHeader != null ? "Presente" : "Ausente");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            loggingService.logWarning("InternalJwtFilter: Header Authorization ausente o inválido");
            response.setContentType("application/json");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } 

        String token = authHeader.replace("Bearer ", "");
        loggingService.logDebug("InternalJwtFilter: Token recibido: {}...", token.substring(0, Math.min(20, token.length())));

        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token);

            // Validar issuer (corregido el nombre del servicio)
            String issuer = claims.getBody().getIssuer();
            loggingService.logDebug("InternalJwtFilter: Token issuer: {}", issuer);
            
            if (!"user-service".equals(issuer)) {
                loggingService.logWarning("InternalJwtFilter: Issuer inválido: {} - Esperado: user-service", issuer);
                throw new JwtException("Issuer inválido: " + issuer);
            }

            loggingService.logInfo("InternalJwtFilter: Validación JWT interna exitosa - Issuer: {}", issuer);

        } catch (JwtException e) {
            loggingService.logError("InternalJwtFilter: Validación JWT interna fallida: {}", e.getMessage());
            response.setContentType("application/json");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, 
                "{\"error\": \"Token inválido: " + e.getMessage() + "\"}");
            return;
        }

        loggingService.logDebug("InternalJwtFilter: Continuando con la solicitud");
        filterChain.doFilter(request, response);
    }

    private boolean requiresJwtValidation(String requestPath) {
        // CAMBIO CRÍTICO: usar equals() en lugar de startsWith()
        boolean requires = protectedPaths.stream().anyMatch(path -> requestPath.equals(path));
        loggingService.logDebug("InternalJwtFilter: Path '{}' requiere JWT: {}", requestPath, requires);
        return requires;
    }
}