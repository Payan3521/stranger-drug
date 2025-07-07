package com.gateway.gateway.microserviceUsers.rateLimiting.domain.port;

import com.gateway.gateway.microserviceUsers.rateLimiting.domain.model.RateLimitConfig;

/**
 * Puerto del dominio para el servicio de rate limiting
 */
public interface IRateLimitService {
    
    /**
     * Verifica si una solicitud está permitida según los límites de rate limiting
     */
    void checkRateLimit(String userIdentifier, String endpoint, int maxRequests);
    
    /**
     * Verifica rate limit para endpoints que requieren 1 solicitud cada 5 minutos
     */
    void checkFiveMinuteRateLimit(String userIdentifier, String endpoint);
    
    /**
     * Obtiene la configuración de rate limit para un endpoint específico
     */
    RateLimitConfig getRateLimitConfig(String endpointKey);
    
    /**
     * Determina si un endpoint requiere rate limiting de 5 minutos
     */
    boolean isFiveMinuteEndpoint(String endpointKey);
}