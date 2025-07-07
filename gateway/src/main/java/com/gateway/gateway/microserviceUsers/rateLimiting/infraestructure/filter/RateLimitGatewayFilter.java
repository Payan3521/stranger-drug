package com.gateway.gateway.microserviceUsers.rateLimiting.infraestructure.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.exception.TooManyRequestsException;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.model.RateLimitConfig;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.port.IRateLimitService;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.port.IUserIdentifierService;
import com.gateway.gateway.common.logging.LoggingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitGatewayFilter extends AbstractGatewayFilterFactory<RateLimitGatewayFilter.Config> {
    
    private final IRateLimitService rateLimitService;
    private final IUserIdentifierService userIdentifierService;
    private final LoggingService loggingService;
    
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().toString();
            String method = exchange.getRequest().getMethod() != null ? 
                exchange.getRequest().getMethod().name() : "GET";
            
            // Obtener la clave del endpoint desde la configuración
            String endpointKey = config.getEndpointKey();
            
            if (endpointKey == null || endpointKey.trim().isEmpty()) {
                loggingService.logGatewayDebug("RateLimitGatewayFilter: No se encontró endpointKey para el path: {}", path);
                return chain.filter(exchange);
            }
            
            try {
                // Obtener identificador del usuario
                String userIdentifier = userIdentifierService.getUserIdentifier(exchange.getRequest());
                
                // Obtener configuración del rate limit
                RateLimitConfig rateLimitConfig = rateLimitService.getRateLimitConfig(endpointKey);
                
                loggingService.logGatewayDebug("RateLimitGatewayFilter: Verificando rate limit para usuario: {} en endpoint: {} - Límite: {}", 
                    userIdentifier, endpointKey, rateLimitConfig.getMaxRequests());
                
                // Aplicar rate limiting según el tipo de endpoint
                if (rateLimitConfig.isFiveMinuteEndpoint()) {
                    rateLimitService.checkFiveMinuteRateLimit(userIdentifier, endpointKey);
                } else {
                    rateLimitService.checkRateLimit(userIdentifier, endpointKey, rateLimitConfig.getMaxRequests());
                }
                
                return chain.filter(exchange);
                
            } catch (TooManyRequestsException e) {
                loggingService.logSecurityWarning("RateLimitGatewayFilter: Rate limit excedido para path: {} - Error: {}", 
                    path, e.getMessage());
                
                // Configurar respuesta de error
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                
                String errorBody = String.format(
                    "{\"timestamp\":\"%s\",\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"%s\",\"path\":\"%s\"}", 
                    java.time.LocalDateTime.now(), e.getMessage(), path);
                
                var buffer = exchange.getResponse().bufferFactory().wrap(errorBody.getBytes());
                return exchange.getResponse().writeWith(Mono.just(buffer));
                
            } catch (Exception e) {
                loggingService.logError("RateLimitGatewayFilter: Error inesperado al aplicar rate limiting para path: {} - Error: {}", 
                    path, e.getMessage());
                return chain.filter(exchange);
            }
        };
    }
    
    /**
     * Configuración del filtro
     */
    public static class Config {
        private String endpointKey;
        
        public String getEndpointKey() {
            return endpointKey;
        }
        
        public void setEndpointKey(String endpointKey) {
            this.endpointKey = endpointKey;
        }
    }
}