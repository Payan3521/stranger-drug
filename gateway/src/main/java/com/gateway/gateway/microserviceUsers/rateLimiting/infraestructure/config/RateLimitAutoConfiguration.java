package com.gateway.gateway.microserviceUsers.rateLimiting.infraestructure.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitAutoConfiguration {
    // Esta clase habilita la carga automática de RateLimitProperties
}