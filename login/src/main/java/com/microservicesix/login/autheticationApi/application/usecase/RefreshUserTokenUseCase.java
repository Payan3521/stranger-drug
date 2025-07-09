package com.microservicesix.login.autheticationApi.application.usecase;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.microservicesix.login.autheticationApi.domain.exception.InvalidTokenException;
import com.microservicesix.login.autheticationApi.domain.exception.UserNotActiveException;
import com.microservicesix.login.autheticationApi.domain.model.AuthenticationResult;
import com.microservicesix.login.autheticationApi.domain.model.RefreshToken;
import com.microservicesix.login.autheticationApi.domain.model.User;
import com.microservicesix.login.autheticationApi.domain.port.in.IRefreshUserToken;
import com.microservicesix.login.autheticationApi.domain.port.in.ITokenService;
import com.microservicesix.login.autheticationApi.domain.port.out.IRefreshTokenRepository;
import com.microservicesix.login.autheticationApi.domain.port.out.IUserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshUserTokenUseCase implements IRefreshUserToken {

    private final IRefreshTokenRepository refreshTokenRepository;
    private final IUserRepository userRepository;
    private final ITokenService tokenService;

    @Override
    @Transactional
    public AuthenticationResult refreshToken(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new InvalidTokenException("Refresh token no encontrado"));

        if (!refreshToken.isValid()) {
            throw new InvalidTokenException("Refresh token inválido o expirado");
        }

        User user = userRepository.findByEmail(refreshToken.getUserEmail())
                .orElseThrow(() -> new InvalidTokenException("Usuario no encontrado para el refresh token"));

        if (!user.canLogin()) {
            throw new UserNotActiveException(user.getEmail());
        }

        // Generar nuevos tokens
        String newAccessToken = tokenService.generateAccessToken(user);
        String newRefreshToken = tokenService.generateRefreshToken(user);

        // Revocar el refresh token anterior
        refreshToken.revoke();
        refreshTokenRepository.save(refreshToken);

        // Guardar nuevo refresh token
        saveRefreshToken(newRefreshToken, user.getEmail());

        return AuthenticationResult.success(newAccessToken, newRefreshToken, 
                tokenService.getTokenExpirationTime(), user);
    }

    private void saveRefreshToken(String token, String userEmail) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .userEmail(userEmail)
                .expiryDate(LocalDateTime.now().plusDays(30)) // 30 días de expiración
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}