package com.gateway.gateway.microserviceUsers.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import com.gateway.gateway.common.logging.LoggingService;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final LoggingService loggingService;

    public SecurityConfig(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        loggingService.logSecurity("SecurityConfig: Iniciando configuración de seguridad del gateway");
        
        SecurityWebFilterChain filterChain = http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                // Permitir todos los endpoints - la autenticación se maneja en los filtros personalizados
                .anyExchange().permitAll()
            )
            .build();
            
        loggingService.logSecurity("SecurityConfig: Configuración de seguridad completada - Todos los endpoints permitidos, autenticación manejada por filtros personalizados");
        
        return filterChain;
    }
}