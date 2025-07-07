package com.gateway.gateway.microserviceUsers.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;
import com.gateway.gateway.common.logging.LoggingService;

@Getter
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException{
    
    private final LoggingService loggingService;
    
    public InvalidTokenException() {
        super("Token inválido o expirado");
        this.loggingService = new LoggingService();
        loggingService.logSecurityError("InvalidTokenException: Token inválido o expirado");
    }
    
    public InvalidTokenException(String message) {
        super(message);
        this.loggingService = new LoggingService();
        loggingService.logSecurityError("InvalidTokenException: {}", message);
    }
}