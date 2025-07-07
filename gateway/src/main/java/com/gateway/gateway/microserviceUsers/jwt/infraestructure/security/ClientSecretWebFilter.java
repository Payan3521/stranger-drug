package com.gateway.gateway.microserviceUsers.jwt.infraestructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import com.gateway.gateway.microserviceUsers.jwt.application.service.ClientSecretValidationService;
import com.gateway.gateway.microserviceUsers.jwt.domain.exception.AccessErrorException;
import com.gateway.gateway.microserviceUsers.jwt.domain.port.IEndpointSecurityService;

import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientSecretWebFilter implements WebFilter {

    private final ClientSecretValidationService clientSecretValidationService;
    private final IEndpointSecurityService endpointSecurityService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod() != null ? 
            exchange.getRequest().getMethod().name() : "GET";

        log.debug("ClientSecretWebFilter: Procesando solicitud {} {}", method, path);

        // Verificar si el endpoint requiere Client Secret
        if (!endpointSecurityService.requiresClientSecret(path, method)) {
            log.debug("ClientSecretWebFilter: Endpoint exento de Client Secret: {} {}", method, path);
            return chain.filter(exchange);
        }

        // Obtener el header del client secret
        String headerName = clientSecretValidationService.getSecretHeaderName();
        String clientSecret = exchange.getRequest().getHeaders().getFirst(headerName);

        log.debug("ClientSecretWebFilter: Validando header {} para endpoint protegido {} {}", headerName, method, path);

        // Validar client secret
        if (!clientSecretValidationService.isValidClientSecret(clientSecret)) {
            log.warn("ClientSecretWebFilter: Client secret inválido para {} {} - ACCESO DENEGADO", method, path);
            return Mono.error(new AccessErrorException("Client secret requerido y válido"));
        }

        log.debug("ClientSecretWebFilter: Client secret válido para {} {}, continuando", method, path);
        return chain.filter(exchange);
    }
}