package com.gateway.gateway.microserviceUsers.jwt.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;
import com.gateway.gateway.common.logging.LoggingService;

@Getter
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AccessErrorException extends RuntimeException {
    
    private final LoggingService loggingService;
    
    public AccessErrorException() {
        super("Acceso no autorizado - X-Secret requerido");
        this.loggingService = new LoggingService();
        loggingService.logSecurityError("AccessErrorException: Acceso no autorizado - X-Secret requerido");
    }
    
    public AccessErrorException(String message) {
        super(message);
        this.loggingService = new LoggingService();
        loggingService.logSecurityError("AccessErrorException: {}", message);
    }
}