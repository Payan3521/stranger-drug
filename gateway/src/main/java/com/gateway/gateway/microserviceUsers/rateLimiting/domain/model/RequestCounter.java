package com.gateway.gateway.microserviceUsers.rateLimiting.domain.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class RequestCounter {
    private final AtomicInteger count = new AtomicInteger(0);
    private LocalDateTime lastReset = LocalDateTime.now();
    
    public AtomicInteger getCount() {
        return count;
    }
    
    public LocalDateTime getLastReset() {
        return lastReset;
    }
    
    public int increment() {
        return count.incrementAndGet();
    }
    
    public void reset(LocalDateTime resetTime) {
        count.set(0);
        lastReset = resetTime;
    }
    
    public int getCurrentValue() {
        return count.get();
    }
}