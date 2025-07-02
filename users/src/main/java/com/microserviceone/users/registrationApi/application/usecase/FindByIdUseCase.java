package com.microserviceone.users.registrationApi.application.usecase;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.in.IFindById;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindByIdUseCase implements IFindById{

    private final ValidateExistUserUseCase validateExistUserUseCase;
    private final IRegisterRepository registerRepository;
    private final LoggingService loggingService;

    @Override
    public Optional<User> findById(Long id) {

        loggingService.logInfo("Buscando usuario con ID: {}", id);
        loggingService.logInfo("validando que el usuario exista", id);

        validateExistUserUseCase.validateExist(id);
        Optional<User> user = registerRepository.findById(id);

        loggingService.logDebug("Usuario encontrado - ID: {}, Email: {}", id, user.get().getEmail());

        return Optional.of(user.get());
        
    }

}