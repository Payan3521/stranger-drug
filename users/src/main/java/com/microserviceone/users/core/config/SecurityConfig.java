package com.microserviceone.users.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.microserviceone.users.core.config.internalSecurity.InternalJwtFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private InternalJwtFilter internalJwtFilter;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         http
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
            .addFilterBefore(internalJwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    } 
}