package com.microserviceone.users.registrationApi.application.usecase;

import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.application.exception.UserNotFoundException;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateExistUserUseCase {
    private final IRegisterRepository registerRepository;
    private final LoggingService loggingService;

    public void validateExist(Long id){
        loggingService.logInfo("Validando existencia de usuario con ID: {}", id);
        if (!registerRepository.findById(id).isPresent()) {
            loggingService.logError("Usuario no encontrado con ID: {}", id);
            throw new UserNotFoundException(id);
        }
        loggingService.logDebug("Usuario encontrado con ID: {}", id);
    }
}