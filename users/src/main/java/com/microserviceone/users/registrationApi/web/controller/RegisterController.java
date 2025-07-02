package com.microserviceone.users.registrationApi.web.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.microserviceone.users.registrationApi.application.exception.UserNotFoundException;
import com.microserviceone.users.registrationApi.application.service.RegistrationService;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.web.dto.AdminRequest;
import com.microserviceone.users.registrationApi.web.dto.ApiResponse;
import com.microserviceone.users.registrationApi.web.dto.CustomerRequest;
import com.microserviceone.users.registrationApi.web.dto.UserResponse;
import com.microserviceone.users.registrationApi.web.webMapper.RegistrationWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.core.rateLimiting.RateLimit;
import com.microserviceone.users.registrationApi.application.service.UserValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
@Tag(name = "Register", description = "API para el registro de usuarios")
public class RegisterController {
    
    private final RegistrationService registrationService;
    private final RegistrationWebMapper registrationWebMapper;
    private final LoggingService loggingService;
    private final UserValidationService userValidationService;

    @Operation(summary = "Registrar cliente", description = "Registra un nuevo cliente en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Cliente registrado exitosamente",
            content = @Content(schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = UserResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto: El cliente ya existe"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @RateLimit(key = "register_customer", maxRequests = 10, description = "Registro de clientes - 10 solicitudes por minuto")
    @PostMapping("/customer")
    public ResponseEntity<ApiResponse<UserResponse>> registerCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        try {
            loggingService.logInfo("Iniciando registro de cliente - Email: {}, Nombre: {} {}", 
                customerRequest.getEmail(), customerRequest.getName(), customerRequest.getLastName());
            
            // Validar verificación de email
            loggingService.logDebug("Validando verificación de email para: {}", customerRequest.getEmail());
            userValidationService.validateUserPrerequisites(customerRequest.getEmail());
            
            // Mapear request a dominio
            loggingService.logDebug("Mapeando CustomerRequest a dominio para email: {}", customerRequest.getEmail());
            Customer customer = registrationWebMapper.toCustomer(customerRequest);
            
            // Registrar cliente
            loggingService.logDebug("Procesando registro de cliente con email: {}", customer.getEmail());
            Customer registeredCustomer = registrationService.save(customer);
            
            // Aceptar términos y condiciones para el usuario recién registrado
            loggingService.logDebug("Aceptando términos y condiciones para usuario ID: {}", registeredCustomer.getId());
            // Por ahora, aceptamos todos los términos activos (esto se puede hacer configurable)
            userValidationService.acceptTermsAndConditions(registeredCustomer.getId(), registeredCustomer.getEmail());
            
            // Mapear respuesta
            loggingService.logDebug("Mapeando respuesta para cliente ID: {}", registeredCustomer.getId());
            UserResponse userResponse = registrationWebMapper.toResponse(registeredCustomer);

            loggingService.logInfo("Cliente registrado exitosamente - ID: {}, Email: {}, Rol: {}", 
                userResponse.getId(), userResponse.getEmail(), userResponse.getRol());

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Customer registered successfully", userResponse));
                
        } catch (Exception e) {
            loggingService.logError("Error al registrar cliente con email: {}", customerRequest.getEmail(), e);
            throw e;
        }
    }

    @Operation(summary = "Registrar admin", description = "Registra un nuevo administrador en el sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "admin registrado exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflicto: El admin ya existe"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @RateLimit(key = "register_admin", maxRequests = 10, description = "Registro de administradores - 10 solicitudes por minuto")
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<UserResponse>> registerAdmin(@Valid @RequestBody AdminRequest adminRequest){
        try {

            loggingService.logInfo("Iniciando registro de administrador - Email: {}, Nombre: {} {}", 
                adminRequest.getEmail(), adminRequest.getName(), adminRequest.getLastName());

            // Validar verificación de email
            loggingService.logDebug("Validando verificación de email para: {}", adminRequest.getEmail());
            userValidationService.validateUserPrerequisites(adminRequest.getEmail());

            // Mapear request a dominio
            loggingService.logDebug("Mapeando AdminRequest a dominio para email: {}", adminRequest.getEmail());
            Admin admin = registrationWebMapper.toAdmin(adminRequest);

            // Registrar administrador
            loggingService.logDebug("Procesando registro de administrador con email: {}", admin.getEmail());
            Admin registeredAdmin = registrationService.save(admin);
            
            // Aceptar términos y condiciones para el usuario recién registrado
            loggingService.logDebug("Aceptando términos y condiciones para usuario ID: {}", registeredAdmin.getId());
            // Por ahora, aceptamos todos los términos activos (esto se puede hacer configurable)
            userValidationService.acceptTermsAndConditions(registeredAdmin.getId(), registeredAdmin.getEmail());

            // Mapear respuesta
            loggingService.logDebug("Mapeando respuesta para administrador ID: {}", registeredAdmin.getId());
            UserResponse userResponse = registrationWebMapper.toResponse(registeredAdmin);

            loggingService.logInfo("Administrador registrado exitosamente - ID: {}, Email: {}, Rol: {}", 
                userResponse.getId(), userResponse.getEmail(), userResponse.getRol());

            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin registered successfully", userResponse));

        } catch (Exception e) {
            loggingService.logError("Error al registrar administrador con email: {}", adminRequest.getEmail(), e);
            throw e;
        }
    }

    @Operation(summary = "Obtener usuario por ID", description = "Retorna un usuario específico basado en su ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @RateLimit(key = "find_user", maxRequests = 50, description = "Búsqueda de usuarios - 50 solicitudes por minuto")
    @GetMapping("/id/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> findUserById(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long id) {
        try {
            loggingService.logInfo("Iniciando búsqueda de usuario por ID: {}", id);
            
            Optional<User> userOptional = registrationService.findById(id);
            
            if (userOptional.isEmpty()) {
                loggingService.logWarning("Usuario no encontrado con ID: {}", id);
                throw new UserNotFoundException(id);
            }
            
            User user = userOptional.get();
            loggingService.logDebug("Mapeando respuesta para usuario ID: {}", user.getId());
            UserResponse userResponse = registrationWebMapper.toResponse(user);

            loggingService.logInfo("Usuario encontrado exitosamente - ID: {}, Email: {}", 
                userResponse.getId(), userResponse.getEmail());

            return ResponseEntity.ok(ApiResponse.success("User found successfully", userResponse));
                
        } catch (Exception e) {
            loggingService.logError("Error al buscar usuario por ID: {}", id, e);
            throw e;
        }
    }

    @Operation(summary = "Obtener usuario por FILTRO", description = "Retorna un usuario especifico basado en los filtros")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @RateLimit(key = "find_users_filters", maxRequests = 30, description = "Búsqueda de usuarios por filtros - 30 solicitudes por minuto")
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> findUsersByFilters(
            @Parameter(description = "Nombre del usuario") @RequestParam(required = false) String name,
            @Parameter(description = "Apellido del usuario") @RequestParam(required = false) String lastName,
            @Parameter(description = "Rol del usuario") @RequestParam(required = false) String rol) {
        try {
            loggingService.logInfo("Iniciando búsqueda de usuarios por filtros - Nombre: {}, Apellido: {}, Rol: {}", 
                name, lastName, rol);
            
            List<User> users = registrationService.findByFilters(name, lastName, rol);


            if (!users.isEmpty()) {

                loggingService.logDebug("Mapeando respuesta para {} usuarios encontrados", users.size());
                List<UserResponse> userResponses = users.stream()
                    .map(registrationWebMapper::toResponse)
                    .toList();

                loggingService.logInfo("Búsqueda completada exitosamente - Se encontraron {} usuarios", userResponses.size());

                return ResponseEntity.ok(ApiResponse.success("Users found successfully", userResponses));
            }

            loggingService.logInfo("No se encontraron usuarios con los filtros especificados");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
           
                
        } catch (Exception e) {
            loggingService.logError("Error al buscar usuarios por filtros - Nombre: {}, Apellido: {}, Rol: {}", 
                name, lastName, rol, e);
            throw e;
        }
    }

    @Operation(summary = "Actualiza el Admin", description = "Actualiza informacion del Admin existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Se actualizó exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Admin no encontrado")
    })
    @RateLimit(key = "update_admin", maxRequests = 5, description = "Actualización de administrators - 20 solicitudes por minuto")
    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateAdmin(
            @Parameter(description = "ID del admin", required = true) @PathVariable Long id,
            @Valid @RequestBody AdminRequest updateAdmin) {
        try {
            loggingService.logInfo("Iniciando actualización de admin - ID: {}, Email: {}", 
                id, updateAdmin.getEmail());
            
            // Mapear request a dominio
            loggingService.logDebug("Mapeando UpdateAdminRequest a dominio para ID: {}", id);
            User adminToUpdate = registrationWebMapper.toAdmin(updateAdmin);
            
            // Actualizar usuario
            loggingService.logDebug("Procesando actualización de admin ID: {}", id);
            Optional<User> updatedAdminOptional = registrationService.update(id, adminToUpdate);
            
            if (updatedAdminOptional.isEmpty()) {
                loggingService.logWarning("Admin no encontrado para actualizar con ID: {}", id);
                throw new UserNotFoundException(id);
            }
            
            User updatedAdmin = updatedAdminOptional.get();
            loggingService.logDebug("Mapeando respuesta para admin actualizado ID: {}", updatedAdmin.getId());
            UserResponse adminResponse = registrationWebMapper.toResponse(updatedAdmin);

            loggingService.logInfo("Admin actualizado exitosamente - ID: {}, Email: {}", 
                adminResponse.getId(), adminResponse.getEmail());

            return ResponseEntity.ok(ApiResponse.success("Admin updated successfully", adminResponse));
                
        } catch (Exception e) {
            loggingService.logError("Error al actualizar admin - ID: {}, Email: {}", 
                id, updateAdmin.getEmail(), e);
            throw e;
        }
    }

    @Operation(summary = "Actualiza el Customer", description = "Actualiza informacion del Customer existente")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Se actualizó exitosamente",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Customer no encontrado")
    })
    @RateLimit(key = "update_customer", maxRequests = 5, description = "Actualización de customers - 20 solicitudes por minuto")
    @PutMapping("/customer/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateCustomer(
        @Parameter(description = "ID del customer", required = true) @PathVariable Long id,
        @Valid @RequestBody CustomerRequest updateCustomer){

        try{
                loggingService.logInfo("Iniciando actualización de customer - ID: {}, Email: {}", 
                id, updateCustomer.getEmail());

                // Mapear request a dominio
            loggingService.logDebug("Mapeando UpdateCustomerRequest a dominio para ID: {}", id);
            User customerToUpdate = registrationWebMapper.toCustomer(updateCustomer);
            
            // Actualizar usuario
            loggingService.logDebug("Procesando actualización de customer ID: {}", id);
            Optional<User> updatedCustomerOptional = registrationService.update(id, customerToUpdate);
            
            if (updatedCustomerOptional.isEmpty()) {
                loggingService.logWarning("Customer no encontrado para actualizar con ID: {}", id);
                throw new UserNotFoundException(id);
            }

            User updatedCustomer = updatedCustomerOptional.get();
            loggingService.logDebug("Mapeando respuesta para customer actualizado ID: {}", updatedCustomer.getId());
            UserResponse customerResponse = registrationWebMapper.toResponse(updatedCustomer);

            loggingService.logInfo("customer actualizado exitosamente - ID: {}, Email: {}", 
                customerResponse.getId(), customerResponse.getEmail());

            return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", customerResponse));
        } catch(Exception e){
            loggingService.logError("Error al actualizar customer - ID: {}, Email: {}", 
                id, updateCustomer.getEmail(), e);
            throw e;
        }
                
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @RateLimit(key = "delete_user", maxRequests = 4, description = "Eliminación de usuarios - 10 solicitudes por minuto")
    @DeleteMapping("/id/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> deleteUser(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long id) {
        try {
            loggingService.logInfo("Iniciando eliminación de usuario - ID: {}", id);
            
            Optional<User> deletedUserOptional = registrationService.delete(id);
            
            if (deletedUserOptional.isEmpty()) {
                loggingService.logWarning("Usuario no encontrado para eliminar con ID: {}", id);
                throw new UserNotFoundException(id);
            }
            
            User deletedUser = deletedUserOptional.get();
            loggingService.logDebug("Mapeando respuesta para usuario eliminado ID: {}", deletedUser.getId());
            UserResponse userResponse = registrationWebMapper.toResponse(deletedUser);

            loggingService.logInfo("Usuario eliminado exitosamente - ID: {}, Email: {}", 
                userResponse.getId(), userResponse.getEmail());

            return ResponseEntity.ok(ApiResponse.success("User deleted successfully", userResponse));
                
        } catch (Exception e) {
            loggingService.logError("Error al eliminar usuario - ID: {}", id, e);
            throw e;
        }
    }
}