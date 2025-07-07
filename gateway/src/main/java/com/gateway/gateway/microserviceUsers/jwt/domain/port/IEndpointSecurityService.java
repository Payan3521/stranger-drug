package com.gateway.gateway.microserviceUsers.jwt.domain.port;

public interface IEndpointSecurityService {
    boolean isPublicEndpoint(String path, String method);
    boolean requiresAuthentication(String path, String method);
    boolean requiresAdminRole(String path, String method);
    boolean requiresClientSecret(String path, String method);
}