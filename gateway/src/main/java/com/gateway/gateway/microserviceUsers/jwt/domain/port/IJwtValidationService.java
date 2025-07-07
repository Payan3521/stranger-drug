package com.gateway.gateway.microserviceUsers.jwt.domain.port;

import com.gateway.gateway.microserviceUsers.jwt.domain.model.UserContext;

public interface IJwtValidationService {
    UserContext validateToken(String token);
    boolean isTokenValid(String token);
}