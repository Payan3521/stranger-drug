package com.microserviceone.users.registrationApi.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveAdmin;
import com.microserviceone.users.registrationApi.domain.port.in.ISaveCustomer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {
    @Mock
    private ISaveAdmin saveAdmin;

    @Mock
    private ISaveCustomer saveCustomer;

    @Mock
    private LoggingService loggingService;

    @Mock
    private com.microserviceone.users.registrationApi.domain.port.in.IFindById findByIdUseCase;
    @Mock
    private com.microserviceone.users.registrationApi.domain.port.in.IFindByFilters findByFiltersUseCase;
    @Mock
    private com.microserviceone.users.registrationApi.domain.port.in.IUpdate updateUseCase;
    @Mock
    private com.microserviceone.users.registrationApi.domain.port.in.IDelete deleteUseCase;

    @InjectMocks
    private RegistrationService registrationService;

    private Customer customer;

    private Customer savedCustomer;

    private Admin admin;

    private Admin savedAdmin;

    @BeforeEach
    void setUp(){
        customer = new Customer();
        customer.setName("juan");
        customer.setLastName("perez");
        customer.setEmail("pablobedoya3521@gmail.com");
        customer.setPassword("Password123!");
        customer.setPhone("3127147814");
        customer.setBirthDate(LocalDate.of(1990, 11, 11));

        savedCustomer = new Customer();
        savedCustomer.setId(1L);
        savedCustomer.setName("juan");
        savedCustomer.setLastName("perez");
        savedCustomer.setEmail("pablobedoya3521@gmail.com");
        savedCustomer.setPassword("hashedPassword");
        savedCustomer.setPhone("3127147814");
        savedCustomer.setBirthDate(LocalDate.of(1990, 11, 11));
        savedCustomer.setVerifiedCode(true);
        savedCustomer.setVerifiedTerm(true);

        admin = new Admin();
        admin.setName("admin");
        admin.setLastName("admin");
        admin.setEmail("admin123@gmail.com");
        admin.setPassword("Admin123!");
        admin.setPhone("3127147814");
        admin.setArea("SISTEMAS");


        savedAdmin = new Admin();
        savedAdmin.setId(1L);
        savedAdmin.setName("admin");
        savedAdmin.setLastName("admin");
        savedAdmin.setEmail("admin123@gmail.com");
        savedAdmin.setPassword("hashedPassword");
        savedAdmin.setPhone("3127147814");
        savedAdmin.setArea("SISTEMAS");
        savedAdmin.setVerifiedCode(true);
        savedAdmin.setVerifiedTerm(true);
    }

    @Test
    void saveCustomer_success(){

        String passwordOriginal = customer.getPassword();

        when(saveCustomer.save(any(Customer.class))).thenReturn(savedCustomer);

        Customer result = registrationService.save(customer);

        // Assertions can be added here to verify the result
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(customer.getName(), result.getName());
        assertEquals(customer.getLastName(), result.getLastName());
        assertEquals(customer.getEmail(), result.getEmail());
        assertEquals(customer.getPhone(), result.getPhone());
        assertEquals(customer.getBirthDate(), result.getBirthDate());
        assertEquals(savedCustomer.getPassword(), result.getPassword());
        assertEquals(savedCustomer.isVerifiedCode(), result.isVerifiedCode());
        assertEquals(savedCustomer.isVerifiedTerm(), result.isVerifiedTerm());

        assertNotEquals(passwordOriginal, result.getPassword());

        // Verify that the saveCustomer method was called with the correct customer object
        verify(saveCustomer).save(customer);
        verify(loggingService).logInfo("RegistrationService: Procesando registro de cliente - Email: {}", customer.getEmail());
        verify(loggingService).logInfo("RegistrationService: Cliente registrado exitosamente - ID: {}, Email: {}", 
            result.getId(), result.getEmail());

    }

    @Test
    void saveCustomer_error(){
        RuntimeException exception = new RuntimeException("Error al guardar el cliente");

        when(saveCustomer.save(any(Customer.class))).thenThrow(exception);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            registrationService.save(customer);
        });

        assertNotNull(thrownException);
        assertEquals(exception.getMessage(), thrownException.getMessage());

        verify(saveCustomer).save(customer);
        verify(loggingService).logInfo("RegistrationService: Procesando registro de cliente - Email: {}", customer.getEmail());
        verify(loggingService).logError("RegistrationService: Error al registrar cliente - Email: {}", customer.getEmail(), exception);
    }

    @Test
    void saveAdmin_success(){

        String passwordOriginal = admin.getPassword();

        when(saveAdmin.save(any(Admin.class))).thenReturn(savedAdmin);

        Admin result = registrationService.save(admin);

        // Assertions can be added here to verify the result
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(admin.getName(), result.getName());
        assertEquals(admin.getLastName(), result.getLastName());
        assertEquals(admin.getEmail(), result.getEmail());
        assertEquals(admin.getPhone(), result.getPhone());
        assertEquals(admin.getArea(), result.getArea());
        assertEquals(savedAdmin.getPassword(), result.getPassword());
        assertEquals(savedAdmin.isVerifiedCode(), result.isVerifiedCode());
        assertEquals(savedAdmin.isVerifiedTerm(), result.isVerifiedTerm());
        assertNotEquals(passwordOriginal, result.getPassword());

        // Verify that the saveAdmin method was called with the correct admin object
        verify(saveAdmin).save(admin);
        verify(loggingService).logInfo("RegistrationService: Procesando registro de administrador - Email: {}", admin.getEmail());
        verify(loggingService).logInfo("RegistrationService: Administrador registrado exitosamente - ID: {}, Email: {}", 
            result.getId(), result.getEmail());
    }

    @Test
    void saveAdmin_error(){

        RuntimeException exception = new RuntimeException("Error al guardar el administrador");

        when(saveAdmin.save(any(Admin.class))).thenThrow(exception);

        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            registrationService.save(admin);
        });

        assertNotNull(thrownException);
        assertEquals(exception.getMessage(), thrownException.getMessage());

        verify(saveAdmin).save(admin);
        verify(loggingService).logInfo("RegistrationService: Procesando registro de administrador - Email: {}", admin.getEmail());
        verify(loggingService).logError("RegistrationService: Error al registrar administrador - Email: {}", admin.getEmail(), exception);
    }

    @Test
    void findById_success() {
        Long id = 1L;
        when(findByIdUseCase.findById(id)).thenReturn(java.util.Optional.of(savedCustomer));
        java.util.Optional<com.microserviceone.users.registrationApi.domain.model.User> result = registrationService.findById(id);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(savedCustomer.getId(), result.get().getId());
        verify(findByIdUseCase).findById(id);
        verify(loggingService).logInfo("RegistrationService: Iniciando búsqueda de usuario por ID: {}", id);
        verify(loggingService).logInfo("RegistrationService: Usuario encontrado exitosamente - ID: {}", id);
    }

    @Test
    void findById_error() {
        Long id = 2L;
        RuntimeException exception = new RuntimeException("Error al buscar usuario por ID");
        when(findByIdUseCase.findById(id)).thenThrow(exception);
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> registrationService.findById(id));
        assertEquals(exception.getMessage(), thrown.getMessage());
        verify(findByIdUseCase).findById(id);
        verify(loggingService).logInfo("RegistrationService: Iniciando búsqueda de usuario por ID: {}", id);
        verify(loggingService).logError("RegistrationService: Error al buscar usuario por ID: {}", id, exception);
    }

    @Test
    void findByFilters_success() {
        String name = "juan";
        String lastName = "perez";
        String rol = "CUSTOMER";
        java.util.List<com.microserviceone.users.registrationApi.domain.model.User> users = java.util.List.of(savedCustomer);
        when(findByFiltersUseCase.findByFilters(name, lastName, rol)).thenReturn(users);
        java.util.List<com.microserviceone.users.registrationApi.domain.model.User> result = registrationService.findByFilters(name, lastName, rol);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(savedCustomer.getId(), result.get(0).getId());
        verify(findByFiltersUseCase).findByFilters(name, lastName, rol);
        verify(loggingService).logInfo("RegistrationService: Iniciando búsqueda por filtros - Name: {}, LastName: {}, Rol: {}", name, lastName, rol);
        verify(loggingService).logInfo("RegistrationService: Búsqueda por filtros completada. Se encontraron {} usuarios", result.size());
    }

    @Test
    void findByFilters_error() {
        String name = "juan";
        String lastName = "perez";
        String rol = "CUSTOMER";
        RuntimeException exception = new RuntimeException("Error al buscar por filtros");
        when(findByFiltersUseCase.findByFilters(name, lastName, rol)).thenThrow(exception);
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> registrationService.findByFilters(name, lastName, rol));
        assertEquals(exception.getMessage(), thrown.getMessage());
        verify(findByFiltersUseCase).findByFilters(name, lastName, rol);
        verify(loggingService).logInfo("RegistrationService: Iniciando búsqueda por filtros - Name: {}, LastName: {}, Rol: {}", name, lastName, rol);
        verify(loggingService).logError("RegistrationService: Error al buscar usuarios por filtros - Name: {}, LastName: {}, Rol: {}", name, lastName, rol, exception);
    }

    @Test
    void update_success() {
        Long id = 1L;
        com.microserviceone.users.registrationApi.domain.model.User userToUpdate = savedCustomer;
        when(updateUseCase.update(id, userToUpdate)).thenReturn(java.util.Optional.of(savedCustomer));
        java.util.Optional<com.microserviceone.users.registrationApi.domain.model.User> result = registrationService.update(id, userToUpdate);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(savedCustomer.getId(), result.get().getId());
        verify(updateUseCase).update(id, userToUpdate);
        verify(loggingService).logInfo("RegistrationService: Iniciando actualización de usuario - ID: {}, Email: {}", id, userToUpdate.getEmail());
        verify(loggingService).logInfo("RegistrationService: Usuario actualizado exitosamente - ID: {}", id);
    }

    @Test
    void update_error() {
        Long id = 1L;
        com.microserviceone.users.registrationApi.domain.model.User userToUpdate = savedCustomer;
        RuntimeException exception = new RuntimeException("Error al actualizar usuario");
        when(updateUseCase.update(id, userToUpdate)).thenThrow(exception);
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> registrationService.update(id, userToUpdate));
        assertEquals(exception.getMessage(), thrown.getMessage());
        verify(updateUseCase).update(id, userToUpdate);
        verify(loggingService).logInfo("RegistrationService: Iniciando actualización de usuario - ID: {}, Email: {}", id, userToUpdate.getEmail());
        verify(loggingService).logError("RegistrationService: Error al actualizar usuario - ID: {}, Email: {}", id, userToUpdate.getEmail(), exception);
    }

    @Test
    void delete_success() {
        Long id = 1L;
        when(deleteUseCase.delete(id)).thenReturn(java.util.Optional.of(savedCustomer));
        java.util.Optional<com.microserviceone.users.registrationApi.domain.model.User> result = registrationService.delete(id);
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(savedCustomer.getId(), result.get().getId());
        verify(deleteUseCase).delete(id);
        verify(loggingService).logInfo("RegistrationService: Iniciando eliminación de usuario - ID: {}", id);
        verify(loggingService).logInfo("RegistrationService: Usuario eliminado exitosamente - ID: {}", id);
    }

    @Test
    void delete_error() {
        Long id = 1L;
        RuntimeException exception = new RuntimeException("Error al eliminar usuario");
        when(deleteUseCase.delete(id)).thenThrow(exception);
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> registrationService.delete(id));
        assertEquals(exception.getMessage(), thrown.getMessage());
        verify(deleteUseCase).delete(id);
        verify(loggingService).logInfo("RegistrationService: Iniciando eliminación de usuario - ID: {}", id);
        verify(loggingService).logError("RegistrationService: Error al eliminar usuario - ID: {}", id, exception);
    }
}