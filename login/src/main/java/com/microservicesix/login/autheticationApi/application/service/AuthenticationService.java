package com.microservicesix.login.autheticationApi.application.service;

import org.springframework.stereotype.Service;

import com.microservicesix.login.autheticationApi.domain.model.AuthenticationResult;
import com.microservicesix.login.autheticationApi.domain.port.in.IAuthenticateUser;
import com.microservicesix.login.autheticationApi.domain.port.in.ILogoutUser;
import com.microservicesix.login.autheticationApi.domain.port.in.IRefreshUserToken;
import com.microservicesix.login.autheticationApi.domain.port.in.IValidateToken;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticateUser, IRefreshUserToken, ILogoutUser, IValidateToken {

    private final IAuthenticateUser authenticateUserUseCase;
    private final IRefreshUserToken refreshUserTokenUseCase;
    private final ILogoutUser logoutUserUseCase;
    private final IValidateToken validateTokenUseCase;

    @Override
    public AuthenticationResult authenticate(String email, String password, String ipAddress, String userAgent) {
        try {
            
            AuthenticationResult result = authenticateUserUseCase.authenticate(email, password, ipAddress, userAgent);
            
            return result;
            
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public AuthenticationResult refreshToken(String refreshToken) {
        try {
            
            AuthenticationResult result = refreshUserTokenUseCase.refreshToken(refreshToken);
            
            return result;
            
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void logout(String refreshToken) {
        try {
            
            logoutUserUseCase.logout(refreshToken);
            
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            
            boolean isValid = validateTokenUseCase.validateToken(token);
            
            return isValid;
            
        } catch (Exception e) {
            return false;
        }
    }
}