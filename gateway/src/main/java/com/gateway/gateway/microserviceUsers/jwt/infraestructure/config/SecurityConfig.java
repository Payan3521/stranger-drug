package com.gateway.gateway.microserviceUsers.jwt.infraestructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.gateway.gateway.microserviceUsers.jwt.infraestructure.security.ClientSecretWebFilter;
import com.gateway.gateway.microserviceUsers.jwt.infraestructure.security.JwtAuthenticationWebFilter;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ClientSecretWebFilter clientSecretWebFilter;
    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        log.info("Configurando seguridad del Gateway");
        
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                // Endpoints públicos específicos (POST)
                .pathMatchers("POST", "/users/terms/accept").permitAll()
                .pathMatchers("POST", "/users/terms/accept/multiple").permitAll()
                .pathMatchers("POST", "/users/register/customer").permitAll()
                .pathMatchers("POST", "/users/verification/send").permitAll()
                .pathMatchers("POST", "/users/verification/check").permitAll()
                
                // Endpoints de documentación y monitoreo
                .pathMatchers("/v3/api-docs/**").permitAll()
                .pathMatchers("/swagger-ui/**").permitAll()
                .pathMatchers("/swagger-ui.html").permitAll()
                .pathMatchers("/api-docs/**").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                
                // Endpoints de Actuator
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/gateway/health").permitAll()
                
                // TODOS los demás endpoints requieren autenticación y rol ADMIN
                .anyExchange().hasRole("ADMIN")
            )
            // Agregar filtros personalizados en orden específico
            .addFilterBefore(clientSecretWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterBefore(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}