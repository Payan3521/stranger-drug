package com.microserviceone.users.registrationApi.application.usecase;

import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.stereotype.Service;

import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.in.IUpdateLastLogin;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateLastLoginUseCase  implements IUpdateLastLogin{

    private final IRegisterRepository registerRepository;
    private final ValidateExistUserUseCase validateExistUserUseCase;
    private final LoggingService loggingService;

    @Override
    public Optional<User> updateLastLogin(Long id) {

        loggingService.logInfo("Validando que exista user con el -ID id {}", id);
        validateExistUserUseCase.validateExist(id);

        loggingService.logInfo( "Buscando por id para ser actualizado", id);
        Optional<User> userDb = registerRepository.findById(id);
        User user = userDb.get();
        user.setLastLogin(LocalDateTime.now());

        loggingService.logDebug("Actualizando campo de usuario con id -ID", id);
        registerRepository.update(id, user);

        loggingService.logInfo("Modificado exitosamente con id ID", id);
        return Optional.of(user);
    }

}