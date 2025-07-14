package com.gateway.gateway.microserviceUsers.rateLimiting.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.gateway.gateway.microserviceUsers.rateLimiting.infraestructure.filter.RateLimitGatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RateLimitGatewayConfig {
    
    private final RateLimitGatewayFilter rateLimitGatewayFilter;
    
    @Bean
    public RouteLocator rateLimitRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // Rutas específicas del microservicio de usuarios con rate limiting
            .route("users-register-customer", r -> r
                .path("/users/register/customer")
                .and().method("POST")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("register_customer"))))
                .uri("lb://users-service"))
                
            .route("users-register-admin", r -> r
                .path("/users/register/admin")
                .and().method("POST")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("register_admin"))))
                .uri("lb://users-service"))
                
            .route("users-find-by-id", r -> r
                .path("/users/register/id/*")
                .and().method("GET")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("find_user"))))
                .uri("lb://users-service"))
                
            .route("users-find-by-filters", r -> r
                .path("/users/register")
                .and().method("GET")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("find_users_filters"))))
                .uri("lb://users-service"))
                
            .route("users-update-admin", r -> r
                .path("/users/register/admin/*")
                .and().method("PUT")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("update_admin"))))
                .uri("lb://users-service"))
                
            .route("users-update-customer", r -> r
                .path("/users/register/customer/*")
                .and().method("PUT")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("update_customer"))))
                .uri("lb://users-service"))
                
            .route("users-update-lastLogin", r -> r
                .path("/users/register/id/*")
                .and().method("PATCH")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("update_lastLogin"))))
                .uri("lb://users-service"))
                
            .route("users-delete", r -> r
                .path("/users/register/id/*")
                .and().method("DELETE")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("delete_user"))))
                .uri("lb://users-service"))

            .route("users-find-by-email", r -> r
                .path("/users/register/email")
                .and().method("GET")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("find_by_email"))))
                .uri("lb://users-service"))
                
            // Rutas de términos y condiciones con rate limiting
            .route("users-terms-get-active", r -> r
                .path("/users/terms")
                .and().method("GET")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("terms_get_all_active"))))
                .uri("lb://users-service"))
                
            .route("users-terms-get-by-type", r -> r
                .path("/users/terms/type")
                .and().method("GET")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("terms_get_by_type"))))
                .uri("lb://users-service"))
                
            .route("users-terms-accept", r -> r
                .path("/users/terms/accept")
                .and().method("POST")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("terms_accept_single"))))
                .uri("lb://users-service"))
                
            .route("users-terms-accept-multiple", r -> r
                .path("/users/terms/accept/multiple")
                .and().method("POST")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("terms_accept_multiple"))))
                .uri("lb://users-service"))
                
            .route("users-terms-verify", r -> r
                .path("/users/terms/verify")
                .and().method("GET")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("terms_verify_acceptance"))))
                .uri("lb://users-service"))
                
            .route("users-terms-verify-email", r -> r
                .path("/users/terms/verify/email")
                .and().method("GET")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("terms_verify_by_email"))))
                .uri("lb://users-service"))
                
            .route("users-terms-get-all", r -> r
                .path("/users/terms/all")
                .and().method("GET")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("terms_get_all"))))
                .uri("lb://users-service"))
                
            .route("users-terms-get-by-id", r -> r
                .path("/users/terms/id/*")
                .and().method("GET")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("terms_get_by_id"))))
                .uri("lb://users-service"))
                
            // Rutas de verificación con rate limiting especial (5 minutos)
            .route("users-verification-send", r -> r
                .path("/users/verification/send")
                .and().method("POST")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("verification_send_code"))))
                .uri("lb://users-service"))
                
            .route("users-verification-check", r -> r
                .path("/users/verification/check")
                .and().method("POST")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("verification_verify_code"))))
                .uri("lb://users-service"))
                
            .route("users-verification-status", r -> r
                .path("/users/verification/status")
                .and().method("POST")
                .filters(f -> f
                    .rewritePath("/users/(?<segment>.*)", "/${segment}")
                    .filter(rateLimitGatewayFilter.apply(createConfig("verification_check_status"))))
                .uri("lb://users-service"))
                
            // Ruta genérica para usuarios (sin rate limiting específico)
            .route("users-service-generic", r -> r
                .path("/users/**")
                .filters(f -> f.rewritePath("/users/(?<segment>.*)", "/${segment}"))
                .uri("lb://users-service"))
                
            .build();
    }
    
    private RateLimitGatewayFilter.Config createConfig(String endpointKey) {
        RateLimitGatewayFilter.Config config = new RateLimitGatewayFilter.Config();
        config.setEndpointKey(endpointKey);
        return config;
    }
}