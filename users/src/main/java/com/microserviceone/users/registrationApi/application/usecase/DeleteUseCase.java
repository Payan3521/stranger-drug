package com.microserviceone.users.registrationApi.application.usecase;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.in.IDelete;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeleteUseCase implements IDelete{

    private final IRegisterRepository registerRepository;
    private final ValidateExistUserUseCase validateExistUserUseCase;
    private final LoggingService loggingService;

    @Override
    public Optional<User> delete(Long id) {
        loggingService.logInfo("Iniciando proceso de eliminación para usuario con ID: {}", id);
        validateExistUserUseCase.validateExist(id);
        User user = registerRepository.findById(id).get();
        registerRepository.delete(user.getId());
        loggingService.logDebug("Usuario eliminado exitosamente - ID: {}, Email: {}", id, user.getEmail());
        return Optional.of(user);
    }
     
}