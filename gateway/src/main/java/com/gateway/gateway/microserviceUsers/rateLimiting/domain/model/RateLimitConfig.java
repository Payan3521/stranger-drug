package com.gateway.gateway.microserviceUsers.rateLimiting.domain.model;

public class RateLimitConfig {
    private final String endpointKey;
    private final int maxRequests;
    private final boolean isFiveMinuteEndpoint;
    private final String description;
    
    public RateLimitConfig(String endpointKey, int maxRequests, boolean isFiveMinuteEndpoint, String description) {
        this.endpointKey = endpointKey;
        this.maxRequests = maxRequests;
        this.isFiveMinuteEndpoint = isFiveMinuteEndpoint;
        this.description = description;
    }
    
    public String getEndpointKey() {
        return endpointKey;
    }
    
    public int getMaxRequests() {
        return maxRequests;
    }
    
    public boolean isFiveMinuteEndpoint() {
        return isFiveMinuteEndpoint;
    }
    
    public String getDescription() {
        return description;
    }
}
