package com.microservicesix.login.autheticationApi.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.microservicesix.login.autheticationApi.domain.port.in.ITokenService;
import com.microservicesix.login.autheticationApi.domain.port.in.IValidateToken;


@Service
@RequiredArgsConstructor
public class ValidateTokenUseCase implements IValidateToken {

    private final ITokenService tokenService;

    @Override
    public boolean validateToken(String token) {
        
        boolean isValid = tokenService.validateToken(token);
        
        if (isValid) {
            
        } else {
            
        }
        
        return isValid;
    }
}
