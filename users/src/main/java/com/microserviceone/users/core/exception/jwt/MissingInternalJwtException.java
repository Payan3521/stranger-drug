package com.microserviceone.users.core.exception.jwt;

public class MissingInternalJwtException extends RuntimeException {
    public MissingInternalJwtException(String message) {
        super(message);
    }
} 