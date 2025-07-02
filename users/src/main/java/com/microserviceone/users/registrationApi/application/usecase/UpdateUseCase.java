package com.microserviceone.users.registrationApi.application.usecase;

import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.in.IUpdate;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateUseCase implements IUpdate{

    private final IRegisterRepository registerRepository;
    private final ValidateExistUserUseCase validateExistUserUseCase;
    private final ValidateUniqueEmailUseCase validateUniqueEmailUseCase;
    private final IdentifyUserUpdateUseCase identifyUserUpdateUseCase;
    private final LoggingService loggingService;

    @Override
    public Optional<User> update(Long id, User user) {
        Objects.requireNonNull(id, "ID must not be null");
        Objects.requireNonNull(user, "User must not be null");
        loggingService.logInfo("Iniciando actualización de usuario - ID: {}, Email: {}", id, user.getEmail());
        validateExistUserUseCase.validateExist(id);
        Optional<User> userDb = registerRepository.findById(id);
        if(!user.getEmail().equals(userDb.get().getEmail())){
            loggingService.logDebug("Validando nuevo email: {}", user.getEmail());
            validateUniqueEmailUseCase.validate(user.getEmail());
        }

        User userUpdated = identifyUserUpdateUseCase.updateUserByType(userDb.get(), user);
        registerRepository.update(id, userUpdated);

        loggingService.logDebug("Usuario actualizado exitosamente - ID: {}, Email: {}", id, userUpdated.getEmail());
        return Optional.of(userUpdated);
    }

}