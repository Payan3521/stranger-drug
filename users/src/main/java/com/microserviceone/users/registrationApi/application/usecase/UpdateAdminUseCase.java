package com.microserviceone.users.registrationApi.application.usecase;

import org.springframework.stereotype.Service;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UpdateAdminUseCase {
    private final PasswordEncripterUseCase passwordEncripterUseCase;
    private final LoggingService loggingService;

    public Admin updateAdmin(Admin adminDb, Admin adminEntrante){
        loggingService.logInfo("Iniciando actualización de administrador - ID: {}, Email: {}", adminDb.getId(), adminDb.getEmail());
        adminDb.setId(adminDb.getId());
        adminDb.setName(adminEntrante.getName());
        adminDb.setLastName(adminEntrante.getLastName());
        adminDb.setEmail(adminEntrante.getEmail());
        adminDb.setPassword(passwordEncripterUseCase.encripter(adminEntrante.getPassword(), adminDb.getEmail()));
        adminDb.setPhone(adminEntrante.getPhone());
        adminDb.setRol(adminDb.getRol());
        adminDb.setArea(adminEntrante.getArea());
        adminDb.setVerifiedCode(true);
        adminDb.setVerifiedTerm(true);
        loggingService.logDebug("Administrador actualizado exitosamente - ID: {}, Email: {}", adminDb.getId(), adminDb.getEmail());
        return adminDb;
    }
}