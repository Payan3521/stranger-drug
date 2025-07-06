package com.gateway.gateway.infraestructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.gateway.gateway.domain.exception.NoAdminAccessException;
import com.gateway.gateway.domain.exception.NoTokenException;
import com.gateway.gateway.domain.model.UserContext;
import com.gateway.gateway.domain.port.IEndpointSecurityService;
import com.gateway.gateway.domain.port.IJwtValidationService;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private final IJwtValidationService jwtValidationService;
    private final IEndpointSecurityService endpointSecurityService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod() != null ? 
            exchange.getRequest().getMethod().name() : "GET";

        log.debug("JwtFilter: Procesando solicitud {} {}", method, path);

        // Si es endpoint público, continuar sin validación JWT
        if (endpointSecurityService.isPublicEndpoint(path, method)) {
            log.debug("JwtFilter: Endpoint público, omitiendo validación JWT");
            return chain.filter(exchange);
        }

        // Obtener header Authorization
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("JwtFilter: Token no proporcionado para {} {}", method, path);
            return Mono.error(new NoTokenException());
        }

        try {
            String jwt = authHeader.substring(7);
            UserContext userContext = jwtValidationService.validateToken(jwt);

            // Verificar permisos para endpoints que requieren ADMIN
            if (endpointSecurityService.requiresAdminRole(path, method) && !userContext.isAdmin()) {
                log.warn("JwtFilter: Acceso denegado - se requiere rol ADMIN para {} {}", method, path);
                return Mono.error(new NoAdminAccessException());
            }

            // Agregar headers con información del usuario para los microservicios
            ServerWebExchange modifiedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                    .header("X-User-Id", userContext.getUserId())
                    .header("X-User-Email", userContext.getEmail())
                    .header("X-User-Role", userContext.getRole())
                    .build())
                .build();

            log.debug("JwtFilter: Autenticación exitosa para usuario: {}", userContext.getEmail());
            return chain.filter(modifiedExchange);

        } catch (Exception e) {
            log.error("JwtFilter: Error durante validación JWT: {}", e.getMessage());
            return Mono.error(new NoTokenException());
        }
    }

    @Override
    public int getOrder() {
        return -1; // Ejecutar después de ClientSecretGatewayFilter
    }
}