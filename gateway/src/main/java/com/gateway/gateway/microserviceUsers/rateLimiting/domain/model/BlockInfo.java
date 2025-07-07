package com.gateway.gateway.microserviceUsers.rateLimiting.domain.model;

import java.time.LocalDateTime;

public class BlockInfo {
    private final int blockLevel;
    private final LocalDateTime blockedUntil;
    
    public BlockInfo(int blockLevel, LocalDateTime blockedUntil) {
        this.blockLevel = blockLevel;
        this.blockedUntil = blockedUntil;
    }
    
    public int getBlockLevel() {
        return blockLevel;
    }
    
    public LocalDateTime getBlockedUntil() {
        return blockedUntil;
    }
    
    public boolean isActive() {
        return blockedUntil.isAfter(LocalDateTime.now());
    }
}