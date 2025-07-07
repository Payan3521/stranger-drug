package com.gateway.gateway.microserviceUsers.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;
import com.gateway.gateway.common.logging.LoggingService;

@Getter
@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoAdminAccessException extends RuntimeException{
    
    private final LoggingService loggingService;
    
    public NoAdminAccessException() {
        super("No tienes permisos de administrador");
        this.loggingService = new LoggingService();
        loggingService.logSecurityError("NoAdminAccessException: No tienes permisos de administrador");
    }
    
    public NoAdminAccessException(String message) {
        super(message);
        this.loggingService = new LoggingService();
        loggingService.logSecurityError("NoAdminAccessException: {}", message);
    }
}