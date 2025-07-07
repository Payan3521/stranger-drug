package com.gateway.gateway.microserviceUsers.jwt.application.service;

import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.gateway.gateway.common.logging.LoggingService;
import com.gateway.gateway.microserviceUsers.jwt.domain.port.IEndpointSecurityService;

@Slf4j
@RequiredArgsConstructor
@Service
public class EndpointSecurityService implements IEndpointSecurityService {

    private final LoggingService loggingService;

    private static final List<String> PUBLIC_POST_ENDPOINTS = Arrays.asList(
        "/users/terms/accept",
        "/users/terms/accept/multiple", 
        "/users/register/customer",
        "/users/verification/send",
        "/users/verification/check"
    );

    private static final List<String> PUBLIC_PATH_PREFIXES = Arrays.asList(
        "/actuator",
        "/swagger-ui",
        "/v3/api-docs",
        "/api-docs"
    );

    private static final List<String> CLIENT_SECRET_EXEMPT_PREFIXES = Arrays.asList(
        "/actuator",
        "/swagger-ui",
        "/v3/api-docs",
        "/api-docs"
    );

    @Override
    public boolean isPublicEndpoint(String path, String method) {
        loggingService.logGatewayDebug("EndpointSecurityService: Verificando si es endpoint público - Método: {}, Path: {}", method, path);

        // Endpoints de documentación y monitoreo
        for (String prefix : PUBLIC_PATH_PREFIXES) {
            if (path.startsWith(prefix)) {
                loggingService.logGatewayDebug("EndpointSecurityService: Endpoint público por prefijo: {} - Path: {}", prefix, path);
                return true;
            }
        }

        // Endpoints específicos públicos (solo POST)
        if ("POST".equals(method)) {
            boolean isPublic = PUBLIC_POST_ENDPOINTS.contains(path);
            if (isPublic) {
                loggingService.logGatewayDebug("EndpointSecurityService: Endpoint público POST encontrado: {}", path);
            } else {
                loggingService.logGatewayDebug("EndpointSecurityService: Endpoint POST no es público: {}", path);
            }
            return isPublic;
        }

        loggingService.logGatewayDebug("EndpointSecurityService: Endpoint no es público - Método: {}, Path: {}", method, path);
        return false;
    }

    @Override
    public boolean requiresAuthentication(String path, String method) {
        boolean requiresAuth = !isPublicEndpoint(path, method);
        loggingService.logGatewayDebug("EndpointSecurityService: Endpoint requiere autenticación: {} - Método: {}, Path: {}", 
            requiresAuth ? "SÍ" : "NO", method, path);
        return requiresAuth;
    }

    @Override
    public boolean requiresAdminRole(String path, String method) {
        // Todos los endpoints no públicos requieren rol ADMIN
        boolean requiresAdmin = requiresAuthentication(path, method);
        loggingService.logGatewayDebug("EndpointSecurityService: Endpoint requiere rol ADMIN: {} - Método: {}, Path: {}", 
            requiresAdmin ? "SÍ" : "NO", method, path);
        return requiresAdmin;
    }

    @Override
    public boolean requiresClientSecret(String path, String method) {
        // En desarrollo, permitir acceso a documentación sin Client Secret
        for (String prefix : CLIENT_SECRET_EXEMPT_PREFIXES) {
            if (path.startsWith(prefix)) {
                log.debug("Endpoint exento de Client Secret: {}", path);
                return false;
            }
        }
        
        // Todos los demás endpoints requieren Client Secret
        return true;
    }
}