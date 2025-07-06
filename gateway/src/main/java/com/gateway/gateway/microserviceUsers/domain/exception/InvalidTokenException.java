package com.gateway.gateway.microserviceUsers.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException() {
        super("Token inválido o expirado");
    }
    public InvalidTokenException(String message) {
        super(message);
    }
}