package com.gateway.gateway.microserviceUsers.jwt.infraestructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import com.gateway.gateway.microserviceUsers.jwt.domain.exception.NoAdminAccessException;
import com.gateway.gateway.microserviceUsers.jwt.domain.exception.NoTokenException;
import com.gateway.gateway.microserviceUsers.jwt.domain.model.UserContext;
import com.gateway.gateway.microserviceUsers.jwt.domain.port.IEndpointSecurityService;
import com.gateway.gateway.microserviceUsers.jwt.domain.port.IJwtValidationService;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private final IJwtValidationService jwtValidationService;
    private final IEndpointSecurityService endpointSecurityService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod() != null ? 
            exchange.getRequest().getMethod().name() : "GET";

        log.debug("JwtAuthenticationWebFilter: Procesando solicitud {} {}", method, path);

        // Si es endpoint público, continuar sin validación JWT
        if (endpointSecurityService.isPublicEndpoint(path, method)) {
            log.debug("JwtAuthenticationWebFilter: Endpoint público, omitiendo validación JWT");
            return chain.filter(exchange);
        }

        // Obtener header Authorization
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("JwtAuthenticationWebFilter: Token no proporcionado para {} {}", method, path);
            return Mono.error(new NoTokenException());
        }

        try {
            String jwt = authHeader.substring(7);
            UserContext userContext = jwtValidationService.validateToken(jwt);

            // Verificar permisos para endpoints que requieren ADMIN
            if (endpointSecurityService.requiresAdminRole(path, method) && !userContext.isAdmin()) {
                log.warn("JwtAuthenticationWebFilter: Acceso denegado - se requiere rol ADMIN para {} {}", method, path);
                return Mono.error(new NoAdminAccessException());
            }

            // Crear token de autenticación para Spring Security
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userContext.getEmail(),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_" + userContext.getRole()))
            );

            // Agregar headers con información del usuario para los microservicios
            ServerWebExchange modifiedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                    .header("X-User-Id", userContext.getUserId())
                    .header("X-User-Email", userContext.getEmail())
                    .header("X-User-Role", userContext.getRole())
                    .build())
                .build();

            log.debug("JwtAuthenticationWebFilter: Autenticación exitosa para usuario: {}", userContext.getEmail());
            
            // Establecer contexto de seguridad y continuar
            return chain.filter(modifiedExchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authToken));

        } catch (Exception e) {
            log.error("JwtAuthenticationWebFilter: Error durante validación JWT: {}", e.getMessage());
            return Mono.error(new NoTokenException());
        }
    }
}