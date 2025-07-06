package com.gateway.gateway.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.FORBIDDEN)
public class NoAdminAccessException extends RuntimeException{
    public NoAdminAccessException() {
        super("No tienes permisos de administrador");
    }
    public NoAdminAccessException(String message) {
        super(message);
    }
}