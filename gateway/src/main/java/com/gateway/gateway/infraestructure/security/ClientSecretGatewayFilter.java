package com.gateway.gateway.infraestructure.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.gateway.gateway.application.service.ClientSecretValidationService;
import com.gateway.gateway.domain.exception.AccessErrorException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientSecretGatewayFilter implements GlobalFilter, Ordered {

    private final ClientSecretValidationService clientSecretValidationService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod() != null ? 
            exchange.getRequest().getMethod().name() : "GET";

        log.debug("ClientSecretFilter: Procesando solicitud {} {} - VALIDANDO CLIENT SECRET PARA TODOS LOS ENDPOINTS", method, path);

        // Obtener el header del client secret
        String headerName = clientSecretValidationService.getSecretHeaderName();
        String clientSecret = exchange.getRequest().getHeaders().getFirst(headerName);

        log.debug("ClientSecretFilter: Validando header {} para endpoint {} {}", headerName, method, path);

        // VALIDAR CLIENT SECRET PARA TODOS LOS ENDPOINTS SIN EXCEPCIÓN
        if (!clientSecretValidationService.isValidClientSecret(clientSecret)) {
            log.warn("ClientSecretFilter: Client secret inválido para {} {} - ACCESO DENEGADO", method, path);
            return Mono.error(new AccessErrorException());
        }

        log.debug("ClientSecretFilter: Client secret válido para {} {}, continuando", method, path);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -2; // Ejecutar ANTES que JwtGatewayFilter - PRIMERA VALIDACIÓN SIEMPRE
    }
}