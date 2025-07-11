package com.microserviceone.users.registrationApi.application.service;

import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.in.IDelete;
import com.microserviceone.users.registrationApi.domain.port.in.IFindByEmail;
import com.microserviceone.users.registrationApi.domain.port.in.IFindByFilters;
import com.microserviceone.users.registrationApi.domain.port.in.IFindById;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveAdmin;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveCustomer;
import com.microserviceone.users.registrationApi.domain.port.in.IUpdate;
import com.microserviceone.users.registrationApi.domain.port.in.IUpdateLastLogin;

import java.util.List;
import java.util.Optional;
import com.microserviceone.users.core.logging.LoggingService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrationService implements ISaveAdmin, ISaveCustomer, IDelete, IUpdate, IFindByFilters, IFindById, IFindByEmail, IUpdateLastLogin{

    private final ISaveAdmin saveAdmin;
    private final ISaveCustomer saveCustomer;
    private final IFindById findByIdUseCase;
    private final IFindByFilters findByFiltersUseCase;
    private final IUpdate updateUseCase;
    private final IDelete deleteUseCase;
    private final IFindByEmail findByEmailUseCase;
    private final IUpdateLastLogin updateLastLoginUseCase;
    private final LoggingService loggingService;

    @Override
    public Customer save(Customer customer) {
        try {
            loggingService.logInfo("RegistrationService: Procesando registro de cliente - Email: {}", customer.getEmail());
            
            Customer savedCustomer = saveCustomer.save(customer);
            
            loggingService.logInfo("RegistrationService: Cliente registrado exitosamente - ID: {}, Email: {}", 
                savedCustomer.getId(), savedCustomer.getEmail());
            
            return savedCustomer;
            
        } catch (Exception e) {
            loggingService.logError("RegistrationService: Error al registrar cliente - Email: {}", customer.getEmail(), e);
            throw e;
        }
    }

    @Override
    public Admin save(Admin admin) {
        try {
            loggingService.logInfo("RegistrationService: Procesando registro de administrador - Email: {}", admin.getEmail());
            
            Admin savedAdmin = saveAdmin.save(admin);
            
            loggingService.logInfo("RegistrationService: Administrador registrado exitosamente - ID: {}, Email: {}", 
                savedAdmin.getId(), savedAdmin.getEmail());
            
            return savedAdmin;
            
        } catch (Exception e) {
            loggingService.logError("RegistrationService: Error al registrar administrador - Email: {}", admin.getEmail(), e);
            throw e;
        }
    }

    @Override
    public Optional<User> findById(Long id) {
         try {
            loggingService.logInfo("RegistrationService: Iniciando búsqueda de usuario por ID: {}", id);
            
            Optional<User> user = findByIdUseCase.findById(id);
            
            loggingService.logInfo("RegistrationService: Usuario encontrado exitosamente - ID: {}", id);
            
            return user;
            
        } catch (Exception e) {
            loggingService.logError("RegistrationService: Error al buscar usuario por ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<User> findByFilters(String name, String lastName, String rol) {
        try {
            loggingService.logInfo("RegistrationService: Iniciando búsqueda por filtros - Name: {}, LastName: {}, Rol: {}", 
                name, lastName, rol);
            
            List<User> users = findByFiltersUseCase.findByFilters(name, lastName, rol);
            
            loggingService.logInfo("RegistrationService: Búsqueda por filtros completada. Se encontraron {} usuarios", 
                users.size());
            
            return users;
            
        } catch (Exception e) {
            loggingService.logError("RegistrationService: Error al buscar usuarios por filtros - Name: {}, LastName: {}, Rol: {}", 
                name, lastName, rol, e);
            throw e;
        }
    }

    @Override
    public Optional<User> update(Long id, User user) {
        try {
            loggingService.logInfo("RegistrationService: Iniciando actualización de usuario - ID: {}, Email: {}", 
                id, user.getEmail());
            
            Optional<User> updatedUser = updateUseCase.update(id, user);
            
            loggingService.logInfo("RegistrationService: Usuario actualizado exitosamente - ID: {}", id);
            
            return updatedUser;
            
        } catch (Exception e) {
            loggingService.logError("RegistrationService: Error al actualizar usuario - ID: {}, Email: {}", 
                id, user.getEmail(), e);
            throw e;
        }
    }

    @Override
    public Optional<User> delete(Long id) {
        try {
            loggingService.logInfo("RegistrationService: Iniciando eliminación de usuario - ID: {}", id);
            
            Optional<User> deletedUser = deleteUseCase.delete(id);
            
            loggingService.logInfo("RegistrationService: Usuario eliminado exitosamente - ID: {}", id);
            
            return deletedUser;
            
        } catch (Exception e) {
            loggingService.logError("RegistrationService: Error al eliminar usuario - ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            loggingService.logInfo("RegistrationService: Iniciando búsqueda por email - Email: {}", email);
            
            Optional<User> user = findByEmailUseCase.findByEmail(email);
            
            loggingService.logInfo("RegistrationService: Búsqueda por email completada. Se encontró {} usuario", 
                user.isPresent() ? "1" : "0");
            
            return user;
            
        } catch (Exception e) {
            loggingService.logError("RegistrationService: Error al buscar usuario por email - Email: {}", email, e);
            throw e;
        }
    }

    @Override
    public Optional<User> updateLastLogin(Long id) {
        
        try{ 
            loggingService.logInfo("RegistrationService: Iniciando actualización por id -Id", id);
            Optional<User> user = updateLastLoginUseCase.updateLastLogin(id);

            loggingService.logInfo("RegistrationService: Actualización por id completada con id -Id", user.get().getId());
            return user;
        } catch(Exception e){
            loggingService.logError("Error al actualizar usuario con id -Id {}", id, e);
            throw e;
        }
    }
    
}