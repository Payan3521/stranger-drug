package com.microserviceone.users.registrationApi.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.Getter;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException{
    private final Long id;
    private final LoggingService loggingService;

    public UserNotFoundException(Long id){
        super("Usuario no encontrado con ID: " + id);
        this.id=id;
        this.loggingService=new LoggingService();
        loggingService.logDebug("Usuario no encontrado - ID: {}", id);
    }
}