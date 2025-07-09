package com.microservicesix.login.oauth2Api.application.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import com.microservicesix.login.autheticationApi.domain.model.User;
import com.microservicesix.login.autheticationApi.domain.port.in.ITokenService;
import com.microservicesix.login.autheticationApi.domain.port.out.IUserRepository;
import com.microservicesix.login.oauth2Api.web.dto.AuthorizationRequest;
import com.microservicesix.login.oauth2Api.web.dto.TokenRequest;
import com.microservicesix.login.oauth2Api.web.dto.TokenResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final ITokenService tokenService;
    private final IUserRepository userRepository;
    
    // Cache temporal para authorization codes (en producción usar Redis)
    private final ConcurrentHashMap<String, AuthorizationCodeInfo> authorizationCodes = new ConcurrentHashMap<>();

    public String generateAuthorizationCode(AuthorizationRequest request) {
        
        // Validar client_id (en producción, validar contra base de datos)
        if (!"my-client-app".equals(request.getClientId())) {
            throw new RuntimeException("Invalid client_id");
        }
        
        String code = UUID.randomUUID().toString();
        
        // Guardar información del código (expira en 10 minutos)
        AuthorizationCodeInfo codeInfo = new AuthorizationCodeInfo(
            request.getClientId(),
            request.getRedirectUri(),
            request.getScope(),
            LocalDateTime.now().plusMinutes(10),
            "user@example.com" // En producción, obtener del contexto de autenticación
        );
        
        authorizationCodes.put(code, codeInfo);
        
        return code;
    }

    public TokenResponse exchangeCodeForTokens(TokenRequest request) {
        
        // Validar grant_type
        if (!"authorization_code".equals(request.getGrantType())) {
            throw new RuntimeException("Unsupported grant_type");
        }
        
        // Obtener información del código
        AuthorizationCodeInfo codeInfo = authorizationCodes.get(request.getCode());
        if (codeInfo == null || codeInfo.isExpired()) {
            authorizationCodes.remove(request.getCode()); // Limpiar código expirado
            throw new RuntimeException("Invalid or expired authorization code");
        }
        
        // Validar client_id y redirect_uri
        if (!codeInfo.getClientId().equals(request.getClientId()) ||
            !codeInfo.getRedirectUri().equals(request.getRedirectUri())) {
            throw new RuntimeException("Invalid client credentials");
        }
        
        // Obtener usuario
        User user = userRepository.findByEmail(codeInfo.getUserEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Generar tokens
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = tokenService.generateRefreshToken(user);
        
        // Remover código usado (one-time use)
        authorizationCodes.remove(request.getCode());

        
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(tokenService.getTokenExpirationTime())
                .scope(codeInfo.getScope() != null ? codeInfo.getScope() : "read write")
                .build();
    }

    public boolean introspectToken(String token) {
        return tokenService.validateToken(token);
    }

    // Clase interna para información del authorization code
    private static class AuthorizationCodeInfo {
        private final String clientId;
        private final String redirectUri;
        private final String scope;
        private final LocalDateTime expiryTime;
        private final String userEmail;

        public AuthorizationCodeInfo(String clientId, String redirectUri, String scope, 
                                   LocalDateTime expiryTime, String userEmail) {
            this.clientId = clientId;
            this.redirectUri = redirectUri;
            this.scope = scope;
            this.expiryTime = expiryTime;
            this.userEmail = userEmail;
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expiryTime);
        }

        // Getters
        public String getClientId() { return clientId; }
        public String getRedirectUri() { return redirectUri; }
        public String getScope() { return scope; }
        public String getUserEmail() { return userEmail; }
    }
}