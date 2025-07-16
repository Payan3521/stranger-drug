package com.microserviceone.users.core.exception.jwt;

public class InvalidInternalJwtIssuerException extends RuntimeException {
    public InvalidInternalJwtIssuerException(String message) {
        super(message);
    }
}