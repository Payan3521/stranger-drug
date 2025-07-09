package com.microservicesix.login.autheticationApi.application.service;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.microservicesix.login.autheticationApi.domain.exception.InvalidCredentialsException;
import com.microservicesix.login.autheticationApi.domain.port.out.ILoginAttemptRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BruteForceProtectionService {

    private final ILoginAttemptRepository loginAttemptRepository;

    @Value("${security.max-failed-attempts:5}")
    private int maxFailedAttempts;

    @Value("${security.lockout-duration-minutes:15}")
    private int lockoutDurationMinutes;

    public void validateLoginAttempt(String email, String ipAddress) {
        // Calcula el tiempo límite (15 minutos atrás)
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(lockoutDurationMinutes);

        // Verificar intentos fallidos por email
        long failedAttemptsByEmail = loginAttemptRepository.countFailedAttemptsByEmailSince(email, cutoffTime);
        
        if (failedAttemptsByEmail >= maxFailedAttempts) {
            throw new InvalidCredentialsException(email, 
                "Cuenta temporalmente bloqueada por múltiples intentos fallidos. Intenta nuevamente en " + 
                lockoutDurationMinutes + " minutos.");
        }

        // Verificar intentos fallidos por IP
        long failedAttemptsByIp = loginAttemptRepository.findFailedAttemptsByIpSince(ipAddress, cutoffTime).size();
        
        if (failedAttemptsByIp >= maxFailedAttempts * 2) { // Más permisivo para IP
            throw new InvalidCredentialsException(email, 
                "IP temporalmente bloqueada por múltiples intentos fallidos. Intenta nuevamente en " + 
                lockoutDurationMinutes + " minutos.");
        }

    }
}
