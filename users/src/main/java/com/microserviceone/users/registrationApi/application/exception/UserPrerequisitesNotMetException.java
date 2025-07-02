package com.microserviceone.users.registrationApi.application.exception;

public class UserPrerequisitesNotMetException extends RuntimeException {
    
    public UserPrerequisitesNotMetException(String message) {
        super(message);
    }
    
    public UserPrerequisitesNotMetException(String message, Throwable cause) {
        super(message, cause);
    }
}