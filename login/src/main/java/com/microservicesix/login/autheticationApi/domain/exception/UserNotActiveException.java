package com.microservicesix.login.autheticationApi.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.FORBIDDEN)
public class UserNotActiveException extends RuntimeException {
    private final String email;

    public UserNotActiveException(String email) {
        super("Usuario no activo o no ha completado el proceso de verificación: " + email);
        this.email = email;
    }
}