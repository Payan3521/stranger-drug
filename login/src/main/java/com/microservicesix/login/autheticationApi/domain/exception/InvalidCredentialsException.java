package com.microservicesix.login.autheticationApi.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidCredentialsException extends RuntimeException {
    private final String email;

    public InvalidCredentialsException(String email) {
        super("Credenciales inválidas para el usuario: " + email);
        this.email = email;
    }

    public InvalidCredentialsException(String email, String message) {
        super(message);
        this.email = email;
    }
}
