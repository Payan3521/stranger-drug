package com.microservicesix.login.autheticationApi.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microservicesix.login.autheticationApi.domain.exception.InvalidTokenException;
import com.microservicesix.login.autheticationApi.domain.model.RefreshToken;
import com.microservicesix.login.autheticationApi.domain.port.in.ILogoutUser;
import com.microservicesix.login.autheticationApi.domain.port.out.IRefreshTokenRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LogoutUserUseCase implements ILogoutUser {

    private final IRefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidTokenException("Refresh token no encontrado"));

        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);

    }
}