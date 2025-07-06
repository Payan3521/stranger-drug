package com.gateway.gateway.domain.port;

import com.gateway.gateway.domain.model.UserContext;

public interface IJwtValidationService {
    UserContext validateToken(String token);
    boolean isTokenValid(String token);
}