package com.microservicesix.login.autheticationApi.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{
    private final String email;

    public UserNotFoundException(String email){
        super("Usuario no encontrado con email: " + email);
        this.email=email;
    }

}