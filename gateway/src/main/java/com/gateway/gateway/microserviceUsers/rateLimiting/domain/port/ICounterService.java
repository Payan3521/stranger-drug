package com.gateway.gateway.microserviceUsers.rateLimiting.domain.port;

import com.gateway.gateway.microserviceUsers.rateLimiting.domain.model.RequestCounter;

public interface ICounterService {
    
    /**
     * Obtiene o crea un contador para una combinación usuario/endpoint
     */
    RequestCounter getOrCreateCounter(String userIdentifier, String endpoint);
    
    /**
     * Obtiene o crea un contador para períodos de 5 minutos
     */
    RequestCounter getOrCreateFiveMinuteCounter(String userIdentifier, String endpoint);
    
    /**
     * Verifica si el contador necesita ser reiniciado y lo reinicia si es necesario
     */
    void resetCounterIfNeeded(RequestCounter counter, int periodMinutes);
    
    /**
     * Incrementa el contador y retorna el nuevo valor
     */
    int incrementCounter(RequestCounter counter);
}