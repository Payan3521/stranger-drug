package com.microserviceone.users.registrationApi.application.usecase;

import java.util.List;
import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.in.IFindByFilters;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindByFiltersUseCase implements IFindByFilters{

    private final IRegisterRepository registerRepository;
    private final LoggingService loggingService;

    @Override
    public List<User> findByFilters(String name, String lastName, String rol) {
        loggingService.logInfo("Buscando usuarios con filtros - Name: {}, LastName: {}, Rol: {}", name, lastName, rol);
        List<User> users = registerRepository.findByFilters(name, lastName, rol);
        loggingService.logDebug("Se encontraron {} usuarios con los filtros especificados", users.size());
        return users;
    }

}