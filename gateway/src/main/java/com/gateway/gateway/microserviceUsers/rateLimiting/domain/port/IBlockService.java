package com.gateway.gateway.microserviceUsers.rateLimiting.domain.port;

import com.gateway.gateway.microserviceUsers.rateLimiting.domain.model.BlockInfo;

public interface IBlockService {
    
    /**
     * Verifica si un usuario está bloqueado para un endpoint específico
     */
    boolean isUserBlocked(String userIdentifier, String endpoint);
    
    /**
     * Bloquea un usuario para un endpoint específico
     */
    void blockUser(String userIdentifier, String endpoint);
    
    /**
     * Obtiene información del bloqueo actual de un usuario
     */
    BlockInfo getBlockInfo(String userIdentifier, String endpoint);
}