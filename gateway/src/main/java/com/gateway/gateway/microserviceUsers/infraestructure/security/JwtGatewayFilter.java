package com.gateway.gateway.microserviceUsers.infraestructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.gateway.gateway.microserviceUsers.domain.exception.NoAdminAccessException;
import com.gateway.gateway.microserviceUsers.domain.exception.NoTokenException;
import com.gateway.gateway.microserviceUsers.domain.model.UserContext;
import com.gateway.gateway.microserviceUsers.domain.port.IEndpointSecurityService;
import com.gateway.gateway.microserviceUsers.domain.port.IJwtValidationService;
import com.gateway.gateway.common.logging.LoggingService;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGatewayFilter implements GlobalFilter, Ordered {

    private final IJwtValidationService jwtValidationService;
    private final IEndpointSecurityService endpointSecurityService;
    private final LoggingService loggingService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod() != null ? 
            exchange.getRequest().getMethod().name() : "GET";

        loggingService.logGatewayDebug("JwtGatewayFilter: Procesando solicitud {} {}", method, path);

        // Si es endpoint público, continuar sin validación JWT
        if (endpointSecurityService.isPublicEndpoint(path, method)) {
            loggingService.logGatewayDebug("JwtGatewayFilter: Endpoint público, omitiendo validación JWT");
            return chain.filter(exchange);
        }

        // Obtener header Authorization
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            loggingService.logSecurityWarning("JwtGatewayFilter: Token no proporcionado para {} {}", method, path);
            return Mono.error(new NoTokenException());
        }

        try {
            String jwt = authHeader.substring(7);
            loggingService.logGatewayDebug("JwtGatewayFilter: Validando JWT para {} {} - Longitud del token: {}", method, path, jwt.length());
            
            UserContext userContext = jwtValidationService.validateToken(jwt);

            loggingService.logGatewayDebug("JwtGatewayFilter: JWT validado exitosamente - Usuario: {}, Rol: {}", userContext.getEmail(), userContext.getRole());

            // Verificar permisos para endpoints que requieren ADMIN
            if (endpointSecurityService.requiresAdminRole(path, method) && !userContext.isAdmin()) {
                loggingService.logSecurityWarning("JwtGatewayFilter: Acceso denegado - se requiere rol ADMIN para {} {} - Usuario: {}, Rol: {}", 
                    method, path, userContext.getEmail(), userContext.getRole());
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

            loggingService.logSecurity("JwtGatewayFilter: Autenticación exitosa para usuario: {} - Endpoint: {} {}", 
                userContext.getEmail(), method, path);
            return chain.filter(modifiedExchange);

        } catch (Exception e) {
            loggingService.logSecurityError("JwtGatewayFilter: Error durante validación JWT para {} {} - Error: {}", method, path, e.getMessage());
            return Mono.error(new NoTokenException());
        }
    }

    @Override
    public int getOrder() {
        return -1; // Ejecutar después de ClientSecretGatewayFilter
    }
}