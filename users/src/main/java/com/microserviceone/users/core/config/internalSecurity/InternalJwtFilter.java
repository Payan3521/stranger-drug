package com.microserviceone.users.core.config.internalSecurity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
import com.microserviceone.users.core.exception.jwt.MissingInternalJwtException;
import com.microserviceone.users.core.exception.jwt.InvalidInternalJwtIssuerException;

@Component
public class InternalJwtFilter extends OncePerRequestFilter {

    private final LoggingService loggingService;

    @Value("${internal-jwt.secret}")
    private String secret;

    // Endpoints que requieren token JWT interno y su issuer esperado
    private final Map<String, String> protectedPathToIssuer = new HashMap<>() {{
        put("/verification/status", "user-service");
        put("/terms/accept/multiple", "user-service");
        put("/register/email", "login-service");
        put("/register/id/", "login-service"); // prefijo para soportar /register/id/{id}
    }};

    public InternalJwtFilter(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    private String getExpectedIssuer(String requestPath) {
        for (Map.Entry<String, String> entry : protectedPathToIssuer.entrySet()) {
            if (requestPath.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
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
            throw new MissingInternalJwtException("No se envió el token interno en la cabecera Authorization o el formato es inválido");
        } 

        String token = authHeader.replace("Bearer ", "");
        loggingService.logDebug("InternalJwtFilter: Token recibido: {}...", token.substring(0, Math.min(20, token.length())));

        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token);

            // Validar issuer según el endpoint
            String issuer = claims.getBody().getIssuer();
            loggingService.logDebug("InternalJwtFilter: Token issuer: {}", issuer);
            String expectedIssuer = getExpectedIssuer(requestPath);
            if (expectedIssuer == null || !expectedIssuer.equals(issuer)) {
                loggingService.logWarning("InternalJwtFilter: Issuer inválido: {} - Esperado: {} para path: {}", issuer, expectedIssuer, requestPath);
                throw new InvalidInternalJwtIssuerException("Issuer inválido: " + issuer + ". Se esperaba: " + expectedIssuer);
            }

            loggingService.logInfo("InternalJwtFilter: Validación JWT interna exitosa - Issuer: {} para path: {}", issuer, requestPath);

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
        return getExpectedIssuer(requestPath) != null;
    }
}