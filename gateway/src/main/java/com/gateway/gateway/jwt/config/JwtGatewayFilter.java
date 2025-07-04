package com.gateway.gateway.jwt.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.gateway.gateway.jwt.exception.NoAdminAccessException;
import com.gateway.gateway.jwt.exception.NoTokenException;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod() != null ? 
            exchange.getRequest().getMethod().name() : "GET";

        if (isPublicEndpoint(path, method)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.error(new NoTokenException());
        }

        
        try {
            String jwt = authHeader.substring(7);
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            if (!isAuthorized(path, method, claims)) {
                return Mono.error(new NoAdminAccessException());
            }

            exchange.getRequest().mutate()
                .header("X-User-Id", claims.getSubject())
                .header("X-User-Role", claims.get("rol", String.class))
                .build();
        } catch (Exception e) {
            return Mono.error(new NoTokenException());
        }
        return chain.filter(exchange);
    }

    private boolean isPublicEndpoint(String path, String method) {
        // Endpoints públicos específicos
        if ("POST".equals(method)) {
            return path.equals("/users/terms/accept") ||
                   path.equals("/users/terms/accept/multiple") ||
                   path.equals("/users/register/customer") ||
                   path.equals("/users/verification/send") ||
                   path.equals("/users/verification/check");
        }
        // Endpoints de documentación y monitoreo
        if (path.startsWith("/actuator") ||
            path.startsWith("/swagger-ui") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/api-docs")) {
            return true;
        }
        return false;
    }

    private boolean isAuthorized(String path, String method, Claims claims) {
        String role = claims.get("rol", String.class);
        // Solo ADMIN puede acceder a cualquier endpoint no público
        return "ADMIN".equals(role);
    }

    @Override
    public int getOrder() {
        return -1; // Alta prioridad
    }
}