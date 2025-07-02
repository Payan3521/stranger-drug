package com.microserviceone.users.registrationApi.application.usecase;

import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateCustomerUseCase {
    private final PasswordEncripterUseCase passwordEncripterUseCase;
    private final LoggingService loggingService;

    public Customer updateCustomer(Customer customerDb, Customer customerEntrante){
        loggingService.logInfo("Iniciando actualización de cliente - ID: {}, Email: {}", customerDb.getId(), customerDb.getEmail());
        customerDb.setId(customerDb.getId());
        customerDb.setName(customerEntrante.getName());
        customerDb.setLastName(customerEntrante.getLastName());
        customerDb.setEmail(customerEntrante.getEmail());
        customerDb.setPassword(passwordEncripterUseCase.encripter(customerEntrante.getPassword(), customerDb.getEmail()));
        customerDb.setPhone(customerEntrante.getPhone());
        customerDb.setRol(customerDb.getRol());
        customerDb.setBirthDate(customerEntrante.getBirthDate());
        customerDb.setVerifiedCode(true);
        customerDb.setVerifiedTerm(true);
        loggingService.logDebug("Cliente actualizado exitosamente - ID: {}, Email: {}", customerDb.getId(), customerDb.getEmail());
        return customerDb;
    }
}