package com.microservicesix.login.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {
    private final PasswordValidateUseCase passwordValidateUseCase;

    public LoginResponse login(LoginRequest loginRequest){
        
    }
}