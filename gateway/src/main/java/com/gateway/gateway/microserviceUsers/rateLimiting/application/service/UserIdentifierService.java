package com.gateway.gateway.microserviceUsers.rateLimiting.application.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import com.gateway.gateway.microserviceUsers.rateLimiting.domain.port.IUserIdentifierService;


@Service
@RequiredArgsConstructor
public class UserIdentifierService implements IUserIdentifierService {
    
    @Override
    public String getUserIdentifier(ServerHttpRequest request) {
        // Intentar obtener el email del header agregado por el filtro JWT
        String userEmail = request.getHeaders().getFirst("X-User-Email");
        
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            return userEmail;
        }
        
        // Fallback a IP address para usuarios no autenticados
        return getClientIpAddress(request);
    }
    
    @Override
    public String getClientIpAddress(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        // En Gateway reactive, usar la IP remota del request
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }
        
        return "unknown";
    }

}