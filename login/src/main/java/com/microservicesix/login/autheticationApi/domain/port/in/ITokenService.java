package com.microservicesix.login.autheticationApi.domain.port.in;

import com.microservicesix.login.autheticationApi.domain.model.User;

public interface ITokenService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean validateToken(String token);
    String extractEmailFromToken(String token);
    Long getTokenExpirationTime();
}