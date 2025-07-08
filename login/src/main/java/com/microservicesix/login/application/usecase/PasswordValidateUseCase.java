package com.microservicesix.login.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordValidateUseCase {
    private final PasswordEncoder passwordEncoder;
    
    public void validatePassword(String password, String encodedPassword) {
        if (!passwordEncoder.matches(password, encodedPassword)) {
            throw new InvalidCredentialsException("Invalid password");
        }
    }
}