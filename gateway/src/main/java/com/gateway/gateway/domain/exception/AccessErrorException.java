package com.gateway.gateway.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AccessErrorException extends RuntimeException {
    public AccessErrorException() {
        super("Acceso no autorizado - X-Secret requerido");
    }
    public AccessErrorException(String message) {
        super(message);
    }
}