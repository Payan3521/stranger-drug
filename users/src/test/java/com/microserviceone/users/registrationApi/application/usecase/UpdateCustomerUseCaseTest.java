package com.microserviceone.users.registrationApi.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.model.User.UserRole;

@ExtendWith(MockitoExtension.class)
public class UpdateCustomerUseCaseTest {

    @Mock
    private PasswordEncripterUseCase passwordEncripterUseCase;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private UpdateCustomerUseCase updateCustomerUseCase;

    private Customer customerDb;
    private Customer customerEntrante;
    private static final String HASHED_PASSWORD = "newHashedPassword";

    @BeforeEach
    void setUp() {
        customerDb = new Customer();
        customerDb.setId(1L);
        customerDb.setName("Juan");
        customerDb.setLastName("Pérez");
        customerDb.setEmail("juan.perez@gmail.com");
        customerDb.setPassword("oldHashedPassword");
        customerDb.setPhone("3127147814");
        customerDb.setBirthDate(LocalDate.of(1990, 1, 1));
        customerDb.setRol(UserRole.CUSTOMER);
        customerDb.setVerifiedCode(true);
        customerDb.setVerifiedTerm(true);

        customerEntrante = new Customer();
        customerEntrante.setName("Juan Carlos");
        customerEntrante.setLastName("Pérez González");
        customerEntrante.setEmail("juan.carlos@gmail.com");
        customerEntrante.setPassword("newPassword123!");
        customerEntrante.setPhone("3127147815");
        customerEntrante.setBirthDate(LocalDate.of(1990, 2, 15));
    }

    @Test
    void updateAdmin_ValidUpdate_ReturnsUpdatedAdmin() {
        // Arrange
        when(passwordEncripterUseCase.encripter(customerEntrante.getPassword(), customerEntrante.getEmail()))
                .thenReturn(HASHED_PASSWORD);

        // Act
        Customer result = updateCustomerUseCase.updateCustomer(customerDb, customerEntrante);

        // Assert
        assertNotNull(result);
        assertEquals(customerDb.getId(), result.getId()); // ID should remain the same
        assertEquals(customerEntrante.getName(), result.getName());
        assertEquals(customerEntrante.getLastName(), result.getLastName());
        assertEquals(customerEntrante.getEmail(), result.getEmail());
        assertEquals(HASHED_PASSWORD, result.getPassword());
        assertEquals(customerEntrante.getPhone(), result.getPhone());
        assertEquals(customerDb.getRol(), result.getRol()); // Role should remain the same
        assertEquals(customerEntrante.getBirthDate(), result.getBirthDate());
        assertTrue(result.isVerifiedCode());
        assertTrue(result.isVerifiedTerm());

        verify(passwordEncripterUseCase).encripter(customerEntrante.getPassword(), customerDb.getEmail());
    }

    @Test
    void updateAdmin_PasswordEncryptionFails_ThrowsException() {
        // Arrange
        RuntimeException encryptionException = new RuntimeException("Encryption failed");
        when(passwordEncripterUseCase.encripter(anyString(), anyString())).thenThrow(encryptionException);

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            updateCustomerUseCase.updateCustomer(customerDb, customerEntrante);
        });

        assertNotNull(thrownException);
        assertEquals(encryptionException.getMessage(), thrownException.getMessage());

        verify(passwordEncripterUseCase).encripter(customerEntrante.getPassword(), customerDb.getEmail());
    }

    @Test
    void updateAdmin_NullAdminDb_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            updateCustomerUseCase.updateCustomer(null, customerEntrante);
        });
    }

    @Test
    void updateAdmin_NullAdminEntrante_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            updateCustomerUseCase.updateCustomer(customerDb, null);
        });
    }

    @Test
    void updateAdmin_RoleRemainsAdmin() {
        // Arrange
        when(passwordEncripterUseCase.encripter(customerEntrante.getPassword(), customerEntrante.getEmail()))
                .thenReturn(HASHED_PASSWORD);

        // Act
        Customer result = updateCustomerUseCase.updateCustomer(customerDb, customerEntrante);

        // Assert
        assertEquals(UserRole.CUSTOMER, result.getRol(), "Role should remain ADMIN");

        verify(passwordEncripterUseCase).encripter(customerEntrante.getPassword(), customerDb.getEmail());
    }

    @Test
    void updateAdmin_IdPreserved() {
        // Arrange
        Long originalId = customerDb.getId();
        when(passwordEncripterUseCase.encripter(customerEntrante.getPassword(), customerEntrante.getEmail()))
                .thenReturn(HASHED_PASSWORD);

        // Act
        Customer result = updateCustomerUseCase.updateCustomer(customerDb, customerEntrante);

        // Assert
        assertEquals(originalId, result.getId(), "ID should be preserved during update");

        verify(passwordEncripterUseCase).encripter(customerEntrante.getPassword(), customerDb.getEmail());
    }
}