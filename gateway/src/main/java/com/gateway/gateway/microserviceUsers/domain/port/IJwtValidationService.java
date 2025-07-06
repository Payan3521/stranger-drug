package com.gateway.gateway.microserviceUsers.domain.port;

import com.gateway.gateway.microserviceUsers.domain.model.UserContext;

public interface IJwtValidationService {
    UserContext validateToken(String token);
    boolean isTokenValid(String token);
}