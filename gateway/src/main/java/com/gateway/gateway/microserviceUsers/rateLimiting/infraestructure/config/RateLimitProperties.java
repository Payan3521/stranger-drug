package com.gateway.gateway.microserviceUsers.rateLimiting.infraestructure.config;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rate.limit")
public class RateLimitProperties {
    
    // Configuración de rate limits por endpoint
    private Map<String, Integer> endpointLimits = new HashMap<>();
    
    // Configuración de tiempos de bloqueo progresivo
    private Map<Integer, Integer> blockDurations = new HashMap<>();
    
    // Límite por defecto
    private int defaultLimit = 60;
    
    public RateLimitProperties() {
        initializeDefaultLimits();
        initializeDefaultBlockDurations();
    }
    
    private void initializeDefaultLimits() {
        // RegisterController - Rate limits
        endpointLimits.put("register_customer", 10);
        endpointLimits.put("register_admin", 10);
        endpointLimits.put("find_user", 50);
        endpointLimits.put("find_users_filters", 30);
        endpointLimits.put("update_customer", 5);
        endpointLimits.put("update_admin", 5);
        endpointLimits.put("delete_user", 4);
        endpointLimits.put("find_by_email", 15);
        
        // TermController - Rate limits
        endpointLimits.put("terms_get_all_active", 20);
        endpointLimits.put("terms_get_by_type", 20);
        endpointLimits.put("terms_get_all", 20);
        endpointLimits.put("terms_get_by_id", 20);
        endpointLimits.put("terms_accept_single", 5);
        endpointLimits.put("terms_accept_multiple", 5);
        endpointLimits.put("terms_verify_acceptance", 5);
        endpointLimits.put("terms_verify_by_email", 5);
        
        // VerificationCodeController - Rate limits
        endpointLimits.put("verification_send_code", 1);
        endpointLimits.put("verification_verify_code", 1);
        endpointLimits.put("verification_check_status", 5);
    }
    
    private void initializeDefaultBlockDurations() {
        blockDurations.put(1, 1);   // Primer bloqueo: 1 minuto
        blockDurations.put(2, 5);   // Segundo bloqueo: 5 minutos
        blockDurations.put(3, 10);  // Tercer bloqueo: 10 minutos
        blockDurations.put(4, 15);  // Cuarto bloqueo: 15 minutos
        blockDurations.put(5, 30);  // Quinto bloqueo: 30 minutos
    }
    
    public Map<String, Integer> getEndpointLimits() {
        return endpointLimits;
    }
    
    public void setEndpointLimits(Map<String, Integer> endpointLimits) {
        this.endpointLimits = endpointLimits;
    }
    
    public Map<Integer, Integer> getBlockDurations() {
        return blockDurations;
    }
    
    public void setBlockDurations(Map<Integer, Integer> blockDurations) {
        this.blockDurations = blockDurations;
    }
    
    public int getDefaultLimit() {
        return defaultLimit;
    }
    
    public void setDefaultLimit(int defaultLimit) {
        this.defaultLimit = defaultLimit;
    }
    
    public int getEndpointLimit(String endpointKey) {
        return endpointLimits.getOrDefault(endpointKey, defaultLimit);
    }
    
    public int getBlockDuration(int blockLevel) {
        return blockDurations.getOrDefault(blockLevel, 30); // 30 minutos por defecto
    }
}