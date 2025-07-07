package com.gateway.gateway.microserviceUsers.rateLimiting.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends RuntimeException {
    
    private final String userIdentifier;
    private final String endpoint;
    private final int currentCount;
    private final int maxRequests;
    
    public TooManyRequestsException(String message) {
        super(message);
        this.userIdentifier = null;
        this.endpoint = null;
        this.currentCount = 0;
        this.maxRequests = 0;
    }
    
    public TooManyRequestsException(String message, String userIdentifier, String endpoint, int currentCount, int maxRequests) {
        super(message);
        this.userIdentifier = userIdentifier;
        this.endpoint = endpoint;
        this.currentCount = currentCount;
        this.maxRequests = maxRequests;
    }
    
    public String getUserIdentifier() {
        return userIdentifier;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public int getCurrentCount() {
        return currentCount;
    }
    
    public int getMaxRequests() {
        return maxRequests;
    }
}