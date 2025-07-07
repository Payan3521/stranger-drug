package com.gateway.gateway.microserviceUsers.rateLimiting.application.service;

import com.gateway.gateway.common.logging.LoggingService;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.model.RequestCounter;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.port.ICounterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CounterService implements ICounterService {
    
    private final LoggingService loggingService;
    
    // Cache para almacenar contadores de requests por usuario/endpoint (1 minuto)
    private final ConcurrentHashMap<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();
    
    // Cache para contadores de 5 minutos
    private final ConcurrentHashMap<String, RequestCounter> fiveMinuteCounters = new ConcurrentHashMap<>();
    
    @Override
    public RequestCounter getOrCreateCounter(String userIdentifier, String endpoint) {
        String key = generateKey(userIdentifier, endpoint);
        
        return requestCounters.computeIfAbsent(key, k -> {
            loggingService.logGatewayDebug("CounterService: Creando nuevo contador para key: {}", key);
            return new RequestCounter();
        });
    }
    
    @Override
    public RequestCounter getOrCreateFiveMinuteCounter(String userIdentifier, String endpoint) {
        String key = generateFiveMinuteKey(userIdentifier, endpoint);
        
        return fiveMinuteCounters.computeIfAbsent(key, k -> {
            loggingService.logGatewayDebug("CounterService: Creando nuevo contador de 5 minutos para key: {}", key);
            return new RequestCounter();
        });
    }
    
    @Override
    public void resetCounterIfNeeded(RequestCounter counter, int periodMinutes) {
        LocalDateTime now = LocalDateTime.now();
        
        // Reiniciar contador si ha pasado el período especificado
        if (counter.getLastReset().plus(periodMinutes, ChronoUnit.MINUTES).isBefore(now)) {
            counter.reset(now);
            loggingService.logGatewayDebug("CounterService: Contador reiniciado después de {} minutos", periodMinutes);
        }
    }
    
    @Override
    public int incrementCounter(RequestCounter counter) {
        return counter.increment();
    }
    
    /**
     * Genera una clave única para el cache basada en usuario y endpoint (1 minuto)
     */
    private String generateKey(String userIdentifier, String endpoint) {
        return userIdentifier + ":" + endpoint;
    }
    
    /**
     * Genera una clave única para el cache de 5 minutos
     */
    private String generateFiveMinuteKey(String userIdentifier, String endpoint) {
        return userIdentifier + ":5min:" + endpoint;
    }
}
