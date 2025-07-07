package com.gateway.gateway.microserviceUsers.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;
import com.gateway.gateway.common.logging.LoggingService;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoTokenException extends RuntimeException{
    private final LoggingService loggingService;

    public NoTokenException() {
        super("Debes ingresar un token de acceso");
        this.loggingService = new LoggingService();
        loggingService.logSecurityError("NoTokenException: Debes ingresar un token de acceso");
    }
    public NoTokenException(String message) {
        super(message);
        this.loggingService = new LoggingService();
        loggingService.logSecurityError("NoTokenException: {}", message);
    }
}