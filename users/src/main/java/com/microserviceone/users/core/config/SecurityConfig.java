package com.microserviceone.users.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.microserviceone.users.core.config.internalSecurity.InternalJwtFilter;
import com.microserviceone.users.core.logging.LoggingService;

@Configuration
public class SecurityConfig {

    private final LoggingService loggingService;

    @Autowired
    private InternalJwtFilter internalJwtFilter;
    
    public SecurityConfig(LoggingService loggingService) {
        this.loggingService = loggingService;
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        loggingService.logInfo("SecurityConfig: Iniciando configuración de seguridad del microservicio users");
        
        SecurityFilterChain filterChain = http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                // QUITAR estas líneas que están en conflicto:
                // .requestMatchers("/verification/status").authenticated()
                // .requestMatchers("/terms/accept/multiple").authenticated()
                
                // Solo endpoints públicos
                .requestMatchers("/register/**").permitAll()
                .requestMatchers("/verification/**").permitAll()  // Permitir todos los verification
                .requestMatchers("/terms/**").permitAll()         // Permitir todos los terms
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().permitAll()
            )
            // Aplicar el filtro JWT antes del filtro de autenticación
            .addFilterBefore(internalJwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();

        loggingService.logInfo("SecurityConfig: Configuración de seguridad completada - Todos los endpoints permitidos, filtro JWT interno configurado");
        
        return filterChain;
    } 
}