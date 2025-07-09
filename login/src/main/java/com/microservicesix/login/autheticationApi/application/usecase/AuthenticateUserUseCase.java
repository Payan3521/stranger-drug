package com.microservicesix.login.autheticationApi.application.usecase;

import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservicesix.login.autheticationApi.application.exception.UserNotFoundException;
import com.microservicesix.login.autheticationApi.application.service.BruteForceProtectionService;
import com.microservicesix.login.autheticationApi.domain.exception.InvalidCredentialsException;
import com.microservicesix.login.autheticationApi.domain.exception.UserNotActiveException;
import com.microservicesix.login.autheticationApi.domain.model.AuthenticationResult;
import com.microservicesix.login.autheticationApi.domain.model.LoginAttempt;
import com.microservicesix.login.autheticationApi.domain.model.RefreshToken;
import com.microservicesix.login.autheticationApi.domain.model.User;
import com.microservicesix.login.autheticationApi.domain.port.in.IAuthenticateUser;
import com.microservicesix.login.autheticationApi.domain.port.in.ITokenService;
import com.microservicesix.login.autheticationApi.domain.port.out.ILoginAttemptRepository;
import com.microservicesix.login.autheticationApi.domain.port.out.IRefreshTokenRepository;
import com.microservicesix.login.autheticationApi.domain.port.out.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticateUserUseCase implements IAuthenticateUser {

    private final IUserRepository userRepository;
    private final ILoginAttemptRepository loginAttemptRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final ITokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final BruteForceProtectionService bruteForceProtectionService;

    @Override
    @Transactional
    public AuthenticationResult authenticate(String email, String password, String ipAddress, String userAgent) {

        try {
            // Verificar protección contra fuerza bruta
            bruteForceProtectionService.validateLoginAttempt(email, ipAddress);

            // Buscar usuario
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException(email));

            // Verificar contraseña
            if (!passwordEncoder.matches(password, user.getPassword())) {
                recordFailedAttempt(email, ipAddress, userAgent, "Contraseña incorrecta");
                throw new InvalidCredentialsException(email, "Contraseña incorrecta");
            }

            // Verificar que el usuario puede hacer login
            if (!user.canLogin()) {
                recordFailedAttempt(email, ipAddress, userAgent, "Usuario no activo o no verificado");
                throw new UserNotActiveException(email);
            }

            // Generar tokens
            String accessToken = tokenService.generateAccessToken(user);
            String refreshToken = tokenService.generateRefreshToken(user);

            // Guardar refresh token
            saveRefreshToken(refreshToken, user.getEmail());

            // Registrar intento exitoso
            recordSuccessfulAttempt(email, ipAddress, userAgent);

            // Actualizar último login
            userRepository.updateLastLogin(email);


            return AuthenticationResult.success(accessToken, refreshToken, 
                    tokenService.getTokenExpirationTime(), user);

        } catch (Exception e) {
            throw e;
        }
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

    private void recordSuccessfulAttempt(String email, String ipAddress, String userAgent) {
        LoginAttempt attempt = LoginAttempt.successful(email, ipAddress, userAgent);
        loginAttemptRepository.save(attempt);
    }

    private void recordFailedAttempt(String email, String ipAddress, String userAgent, String reason) {
        LoginAttempt attempt = LoginAttempt.failed(email, ipAddress, userAgent, reason);
        loginAttemptRepository.save(attempt);
    }
}
