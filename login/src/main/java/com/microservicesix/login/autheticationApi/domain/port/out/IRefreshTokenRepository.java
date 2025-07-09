package com.microservicesix.login.autheticationApi.domain.port.out;

import java.util.Optional;

import com.microservicesix.login.autheticationApi.domain.model.RefreshToken;

public interface IRefreshTokenRepository {
    RefreshToken save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void revokeAllByUserEmail(String userEmail);
    void deleteExpiredTokens();
}