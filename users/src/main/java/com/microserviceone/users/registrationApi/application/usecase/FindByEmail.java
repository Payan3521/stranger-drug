package com.microserviceone.users.registrationApi.application.usecase;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.in.IFindByEmail;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import lombok.RequiredArgsConstructor;
import com.microserviceone.users.core.logging.LoggingService;

@Service
@RequiredArgsConstructor
public class FindByEmail implements IFindByEmail {

    private final IRegisterRepository registerRepository;
    private final LoggingService loggingService;
    private final ValidateExistByEmailUseCase validateExistByEmailUseCase;

    @Override
    public Optional<User> findByEmail(String email) {
        loggingService.logInfo("Buscando usuario con email: {}", email);
        validateExistByEmailUseCase.validateExistByEmail(email);
        Optional<User> user = registerRepository.findByEmail(email);
        loggingService.logDebug("Usuario encontrado - ID: {}, Email: {}", user.get().getId(), user.get().getEmail());
        return Optional.of(user.get());
    }
    
}