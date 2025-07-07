package com.gateway.gateway.microserviceUsers.rateLimiting.infraestructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import com.gateway.gateway.common.logging.LoggingService;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.exception.TooManyRequestsException;

@ControllerAdvice
@RequiredArgsConstructor
public class RateLimitExceptionHandler {
    
    private final LoggingService loggingService;
    
    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Map<String, Object>> handleTooManyRequestsException(TooManyRequestsException ex) {
        loggingService.logSecurityWarning("RateLimitExceptionHandler: Manejando excepción de rate limit - Usuario: {}, Endpoint: {}, Mensaje: {}", 
            ex.getUserIdentifier(), ex.getEndpoint(), ex.getMessage());
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        body.put("error", "Too Many Requests");
        body.put("message", ex.getMessage());
        
        if (ex.getUserIdentifier() != null) {
            body.put("userIdentifier", ex.getUserIdentifier());
        }
        
        if (ex.getEndpoint() != null) {
            body.put("endpoint", ex.getEndpoint());
            body.put("currentCount", ex.getCurrentCount());
            body.put("maxRequests", ex.getMaxRequests());
        }
        
        return new ResponseEntity<>(body, HttpStatus.TOO_MANY_REQUESTS);
    }
}
