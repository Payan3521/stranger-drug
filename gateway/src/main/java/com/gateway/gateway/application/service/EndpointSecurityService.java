package com.gateway.gateway.application.service;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.gateway.gateway.domain.port.IEndpointSecurityService;

@Slf4j
@Service
public class EndpointSecurityService implements IEndpointSecurityService {

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

    @Override
    public boolean isPublicEndpoint(String path, String method) {
        log.debug("Verificando si es endpoint público: {} {}", method, path);

        // Endpoints de documentación y monitoreo
        for (String prefix : PUBLIC_PATH_PREFIXES) {
            if (path.startsWith(prefix)) {
                log.debug("Endpoint público por prefijo: {}", prefix);
                return true;
            }
        }

        // Endpoints específicos públicos (solo POST)
        if ("POST".equals(method)) {
            boolean isPublic = PUBLIC_POST_ENDPOINTS.contains(path);
            if (isPublic) {
                log.debug("Endpoint público POST: {}", path);
            }
            return isPublic;
        }

        return false;
    }

    @Override
    public boolean requiresAuthentication(String path, String method) {
        return !isPublicEndpoint(path, method);
    }

    @Override
    public boolean requiresAdminRole(String path, String method) {
        // Todos los endpoints no públicos requieren rol ADMIN
        return requiresAuthentication(path, method);
    }
}