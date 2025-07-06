package com.gateway.gateway.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NoTokenException extends RuntimeException{
    public NoTokenException() {
        super("Debes ingresar un token de acceso");
    }
    public NoTokenException(String message) {
        super(message);
    }
}