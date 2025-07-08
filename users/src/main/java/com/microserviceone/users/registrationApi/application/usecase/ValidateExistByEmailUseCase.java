package com.microserviceone.users.registrationApi.application.usecase;

import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import lombok.RequiredArgsConstructor;
import com.microserviceone.users.registrationApi.application.exception.UserNotFoundException;

@Service
@RequiredArgsConstructor
public class ValidateExistByEmailUseCase {
    
    private final IRegisterRepository registerRepository;
    private final LoggingService loggingService;

    public void validateExistByEmail(String email){
        loggingService.logInfo("Validando existencia de usuario con email: {}", email);
        if (!registerRepository.findByEmail(email).isPresent()) {
            loggingService.logError("Usuario no encontrado con email: {}", email);
            throw new UserNotFoundException(email);
        }
        loggingService.logDebug("Usuario encontrado con email: {}", email);
    }
}