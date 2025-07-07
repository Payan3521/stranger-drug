package com.gateway.gateway.microserviceUsers.rateLimiting.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.gateway.gateway.common.logging.LoggingService;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.exception.TooManyRequestsException;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.model.RateLimitConfig;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.model.RequestCounter;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.port.IRateLimitService;
import com.gateway.gateway.microserviceUsers.rateLimiting.infraestructure.config.RateLimitProperties;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.port.IBlockService;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.port.ICounterService;

@Service
@RequiredArgsConstructor
public class RateLimitService implements IRateLimitService {
    
    private final LoggingService loggingService;
    private final RateLimitProperties rateLimitProperties;
    private final IBlockService blockService;
    private final ICounterService counterService;
    
    @Override
    public void checkRateLimit(String userIdentifier, String endpoint, int maxRequests) {
        String key = userIdentifier + ":" + endpoint;
        
        loggingService.logGatewayDebug("RateLimitService: Verificando rate limit - Key: {}, Endpoint: {}, MaxRequests: {}", 
            key, endpoint, maxRequests);
        
        // Verificar si el usuario está bloqueado para este endpoint específico
        if (blockService.isUserBlocked(userIdentifier, endpoint)) {
            var blockInfo = blockService.getBlockInfo(userIdentifier, endpoint);
            loggingService.logSecurityWarning("RateLimitService: Usuario bloqueado intenta acceder al endpoint {} - Bloqueado hasta: {}", 
                endpoint, blockInfo.getBlockedUntil());
            throw new TooManyRequestsException("Usuario bloqueado para este endpoint. Intenta más tarde.", 
                userIdentifier, endpoint, 0, maxRequests);
        }
        
        // Obtener o crear contador para esta combinación usuario/endpoint
        RequestCounter counter = counterService.getOrCreateCounter(userIdentifier, endpoint);
        
        // Verificar si el contador necesita ser reiniciado (cada minuto)
        counterService.resetCounterIfNeeded(counter, 1);
        
        // Verificar límite
        int currentCount = counter.getCurrentValue();
        loggingService.logGatewayDebug("RateLimitService: Contador actual para {}: {}/{}", key, currentCount, maxRequests);
        
        if (currentCount >= maxRequests) {
            loggingService.logSecurityWarning("RateLimitService: Rate limit excedido para usuario: {} en endpoint: {} - Intentos: {}", 
                userIdentifier, endpoint, currentCount);
            
            // Bloquear usuario solo para este endpoint específico
            blockService.blockUser(userIdentifier, endpoint);
            
            throw new TooManyRequestsException("Demasiadas solicitudes para este endpoint, inténtalo más tarde", 
                userIdentifier, endpoint, currentCount, maxRequests);
        }
        
        // Incrementar contador
        int newCount = counterService.incrementCounter(counter);
        
        loggingService.logGatewayDebug("RateLimitService: Request permitida para usuario: {} en endpoint: {} - Contador: {}/{}", 
            userIdentifier, endpoint, newCount, maxRequests);
    }
    
    @Override
    public void checkFiveMinuteRateLimit(String userIdentifier, String endpoint) {
        String key = userIdentifier + ":5min:" + endpoint;
        
        loggingService.logGatewayDebug("RateLimitService: Verificando rate limit de 5 minutos - Key: {}, Endpoint: {}", 
            key, endpoint);
        
        // Verificar si el usuario está bloqueado
        if (blockService.isUserBlocked(userIdentifier, endpoint)) {
            var blockInfo = blockService.getBlockInfo(userIdentifier, endpoint);
            loggingService.logSecurityWarning("RateLimitService: Usuario bloqueado intenta acceder al endpoint {} - Bloqueado hasta: {}", 
                endpoint, blockInfo.getBlockedUntil());
            throw new TooManyRequestsException("Usuario bloqueado para este endpoint. Intenta más tarde.", 
                userIdentifier, endpoint, 0, 1);
        }
        
        // Obtener o crear contador de 5 minutos
        RequestCounter counter = counterService.getOrCreateFiveMinuteCounter(userIdentifier, endpoint);
        
        // Verificar si el contador necesita ser reiniciado (cada 5 minutos)
        counterService.resetCounterIfNeeded(counter, 5);
        
        // Verificar límite (máximo 1 solicitud en 5 minutos)
        int currentCount = counter.getCurrentValue();
        loggingService.logGatewayDebug("RateLimitService: Contador actual para {}: {}/1", key, currentCount);
        
        if (currentCount >= 1) {
            loggingService.logSecurityWarning("RateLimitService: Rate limit de 5 minutos excedido para usuario: {} en endpoint: {} - Intentos: {}", 
                userIdentifier, endpoint, currentCount);
            
            // Bloquear usuario
            blockService.blockUser(userIdentifier, endpoint);
            
            throw new TooManyRequestsException("Demasiadas solicitudes para este endpoint. Solo se permite 1 solicitud cada 5 minutos.", 
                userIdentifier, endpoint, currentCount, 1);
        }
        
        // Incrementar contador
        int newCount = counterService.incrementCounter(counter);
        
        loggingService.logGatewayDebug("RateLimitService: Request permitida para usuario: {} en endpoint: {} - Contador: {}/1 (5 minutos)", 
            userIdentifier, endpoint, newCount);
    }
    
    @Override
    public RateLimitConfig getRateLimitConfig(String endpointKey) {
        int maxRequests = rateLimitProperties.getEndpointLimit(endpointKey);
        boolean isFiveMinute = isFiveMinuteEndpoint(endpointKey);
        
        return new RateLimitConfig(endpointKey, maxRequests, isFiveMinute, 
            "Rate limit config for " + endpointKey);
    }
    
    @Override
    public boolean isFiveMinuteEndpoint(String endpointKey) {
        return "verification_send_code".equals(endpointKey) || 
               "verification_verify_code".equals(endpointKey);
    }
}