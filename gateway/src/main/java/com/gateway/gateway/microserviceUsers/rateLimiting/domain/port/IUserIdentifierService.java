package com.gateway.gateway.microserviceUsers.rateLimiting.domain.port;

import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * Puerto del dominio para el servicio de identificación de usuarios
 */
public interface IUserIdentifierService {
    
    /**
     * Obtiene el identificador del usuario desde el contexto de autenticación o IP
     */
    String getUserIdentifier(ServerHttpRequest request);
    
    /**
     * Obtiene la dirección IP real del cliente considerando proxies
     */
    String getClientIpAddress(ServerHttpRequest request);
}