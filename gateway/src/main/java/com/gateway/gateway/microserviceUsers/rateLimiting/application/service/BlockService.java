package com.gateway.gateway.microserviceUsers.rateLimiting.application.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import com.gateway.gateway.common.logging.LoggingService;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.model.BlockInfo;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.port.IBlockService;
import com.gateway.gateway.microserviceUsers.rateLimiting.infraestructure.config.RateLimitProperties;


@Service
@RequiredArgsConstructor
public class BlockService implements IBlockService {
    
    private final LoggingService loggingService;
    private final RateLimitProperties rateLimitProperties;
    
    // Cache para almacenar información de bloqueos
    private final ConcurrentHashMap<String, BlockInfo> blockedUsers = new ConcurrentHashMap<>();
    
    @Override
    public boolean isUserBlocked(String userIdentifier, String endpoint) {
        String blockKey = generateBlockKey(userIdentifier, endpoint);
        BlockInfo blockInfo = blockedUsers.get(blockKey);
        
        if (blockInfo == null) {
            return false;
        }
        
        if (blockInfo.isActive()) {
            loggingService.logGatewayDebug("BlockService: Usuario bloqueado - Nivel: {}, Bloqueado hasta: {}", 
                blockInfo.getBlockLevel(), blockInfo.getBlockedUntil());
            return true;
        }
        
        // El bloqueo ha expirado, pero mantenemos el nivel para el siguiente bloqueo
        loggingService.logGatewayDebug("BlockService: Bloqueo expirado para usuario: {} en endpoint: {} - Nivel anterior: {}", 
            userIdentifier, endpoint, blockInfo.getBlockLevel());
        return false;
    }
    
    @Override
    public void blockUser(String userIdentifier, String endpoint) {
        String blockKey = generateBlockKey(userIdentifier, endpoint);
        BlockInfo currentBlock = blockedUsers.get(blockKey);
        
        // Incrementar nivel de bloqueo o empezar en 1 si no hay bloqueo previo
        int blockLevel = (currentBlock != null) ? currentBlock.getBlockLevel() + 1 : 1;
        
        // Obtener duración del bloqueo según el nivel
        int blockMinutes = rateLimitProperties.getBlockDuration(blockLevel);
        
        LocalDateTime blockedUntil = LocalDateTime.now().plus(blockMinutes, ChronoUnit.MINUTES);
        
        BlockInfo newBlock = new BlockInfo(blockLevel, blockedUntil);
        blockedUsers.put(blockKey, newBlock);
        
        loggingService.logSecurityWarning("BlockService: Usuario bloqueado para endpoint {}: {} - Nivel: {} - Duración: {} minutos - Bloqueado hasta: {}", 
            endpoint, userIdentifier, blockLevel, blockMinutes, blockedUntil);
    }
    
    @Override
    public BlockInfo getBlockInfo(String userIdentifier, String endpoint) {
        String blockKey = generateBlockKey(userIdentifier, endpoint);
        return blockedUsers.get(blockKey);
    }
    
    /**
     * Genera una clave única para el bloqueo
     */
    private String generateBlockKey(String userIdentifier, String endpoint) {
        return userIdentifier + ":block:" + endpoint;
    }
}