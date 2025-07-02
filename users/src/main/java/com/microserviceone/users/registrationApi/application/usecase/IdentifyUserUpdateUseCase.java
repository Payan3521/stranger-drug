package com.microserviceone.users.registrationApi.application.usecase;

import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.model.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdentifyUserUpdateUseCase {
    private final UpdateAdminUseCase updateAdminUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final LoggingService loggingService;

    public User updateUserByType(User userDb, User userEntrante){
        loggingService.logInfo("Identificando tipo de usuario para actualización - Email: {}", userDb.getEmail());
        if (userDb instanceof Customer && userEntrante instanceof Customer) {
            loggingService.logInfo("Actualizando usuario tipo Customer - Email: {}", userDb.getEmail());
            return updateCustomerUseCase.updateCustomer((Customer) userDb, (Customer) userEntrante);
        }else if(userDb instanceof Admin && userEntrante instanceof Admin){
            loggingService.logInfo("Actualizando usuario tipo Admin - Email: {}", userDb.getEmail());
            return updateAdminUseCase.updateAdmin((Admin) userDb, (Admin) userEntrante);
        }else{
            loggingService.logError("Error: Tipo de usuario no coincide - Email: {}", userDb.getEmail());
            throw new IllegalArgumentException("Tipo de usuario no coincide");    
        }
    }

}