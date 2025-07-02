package com.microserviceone.users.registrationApi.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.application.exception.UserAlreadyRegisteredException;
import com.microserviceone.users.registrationApi.application.exception.UserNotFoundException;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;

@ExtendWith(MockitoExtension.class)
public class UpdateUseCaseTest {
    @Mock
    private IRegisterRepository registerRepository;

    @Mock
    private ValidateExistUserUseCase validateExistUserUseCase;

    @Mock
    private ValidateUniqueEmailUseCase validateUniqueEmailUseCase;

    @Mock
    private IdentifyUserUpdateUseCase identifyUserUpdateUseCase;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private UpdateUseCase updateUseCase;

    private Customer customerDb;
    private Customer customerUpdate;
    private Customer updatedCustomer;
    private Admin adminDb;
    private Admin adminUpdate;
    private Admin updatedAdmin;
    private static final Long VALID_ID = 1L;
    private static final Long INVALID_ID = 999L;


    @BeforeEach
    void setUp() {
        // Customer setup
        customerDb = new Customer();
        customerDb.setId(VALID_ID);
        customerDb.setName("Juan");
        customerDb.setLastName("Pérez");
        customerDb.setEmail("juan.perez@gmail.com");
        customerDb.setPassword("oldHashedPassword");
        customerDb.setPhone("3127147814");
        customerDb.setBirthDate(LocalDate.of(1990, 1, 1));

        customerUpdate = new Customer();
        customerUpdate.setName("Juan Carlos");
        customerUpdate.setLastName("Pérez González");
        customerUpdate.setEmail("juan.carlos@gmail.com"); // Different email
        customerUpdate.setPassword("newPassword123!");
        customerUpdate.setPhone("3127147815");
        customerUpdate.setBirthDate(LocalDate.of(1990, 1, 1));

        updatedCustomer = new Customer();
        updatedCustomer.setId(VALID_ID);
        updatedCustomer.setName("Juan Carlos");
        updatedCustomer.setLastName("Pérez González");
        updatedCustomer.setEmail("juan.carlos@gmail.com");
        updatedCustomer.setPassword("newHashedPassword");
        updatedCustomer.setPhone("3127147815");
        updatedCustomer.setBirthDate(LocalDate.of(1990, 1, 1));

        // Admin setup
        adminDb = new Admin();
        adminDb.setId(VALID_ID);
        adminDb.setName("Admin");
        adminDb.setLastName("User");
        adminDb.setEmail("admin@gmail.com");
        adminDb.setPassword("oldHashedPassword");
        adminDb.setPhone("3127147816");
        adminDb.setArea("IT");

        adminUpdate = new Admin();
        adminUpdate.setName("Super Admin");
        adminUpdate.setLastName("Super User");
        adminUpdate.setEmail("admin@gmail.com"); // Same email
        adminUpdate.setPassword("newAdminPassword123!");
        adminUpdate.setPhone("3127147817");
        adminUpdate.setArea("SYSTEMS");

        updatedAdmin = new Admin();
        updatedAdmin.setId(VALID_ID);
        updatedAdmin.setName("Super Admin");
        updatedAdmin.setLastName("Super User");
        updatedAdmin.setEmail("admin@gmail.com");
        updatedAdmin.setPassword("newHashedAdminPassword");
        updatedAdmin.setPhone("3127147817");
        updatedAdmin.setArea("SYSTEMS");
    }


    @Test
    void update_CustomerWithDifferentEmail_ReturnsUpdatedCustomer() {
        // Arrange
        doNothing().when(validateExistUserUseCase).validateExist(VALID_ID);
        when(registerRepository.findById(VALID_ID)).thenReturn(Optional.of(customerDb));
        doNothing().when(validateUniqueEmailUseCase).validate(customerUpdate.getEmail());
        when(identifyUserUpdateUseCase.updateUserByType(any(User.class), any(User.class)))
                .thenReturn(updatedCustomer);
        when(registerRepository.update(anyLong(), any(User.class))).thenReturn(Optional.of(updatedCustomer));

        // Act
        Optional<User> result = updateUseCase.update(VALID_ID, customerUpdate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(updatedCustomer.getId(), result.get().getId());
        assertEquals(updatedCustomer.getEmail(), result.get().getEmail());
        assertEquals(updatedCustomer.getName(), result.get().getName());

        verify(validateExistUserUseCase).validateExist(VALID_ID);
        verify(registerRepository).findById(VALID_ID);
        verify(validateUniqueEmailUseCase).validate(customerUpdate.getEmail());
        verify(identifyUserUpdateUseCase).updateUserByType(customerDb, customerUpdate);
        verify(registerRepository).update(VALID_ID, updatedCustomer);
    }

     @Test
    void update_AdminWithSameEmail_ReturnsUpdatedAdmin() {
        // Arrange
        doNothing().when(validateExistUserUseCase).validateExist(VALID_ID);
        when(registerRepository.findById(VALID_ID)).thenReturn(Optional.of(adminDb));
        when(identifyUserUpdateUseCase.updateUserByType(any(User.class), any(User.class)))
                .thenReturn(updatedAdmin);
        when(registerRepository.update(anyLong(), any(User.class))).thenReturn(Optional.of(updatedAdmin));

        // Act
        Optional<User> result = updateUseCase.update(VALID_ID, adminUpdate);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(updatedAdmin.getId(), result.get().getId());
        assertEquals(updatedAdmin.getEmail(), result.get().getEmail());
        assertEquals(updatedAdmin.getName(), result.get().getName());
        assertTrue(result.get() instanceof Admin);

        verify(validateExistUserUseCase).validateExist(VALID_ID);
        verify(registerRepository).findById(VALID_ID);
        verify(validateUniqueEmailUseCase, never()).validate(anyString()); // Same email, no validation needed
        verify(identifyUserUpdateUseCase).updateUserByType(adminDb, adminUpdate);
        verify(registerRepository).update(VALID_ID, updatedAdmin);
    }

     @Test
    void update_UserNotExists_ThrowsUserNotFoundException() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException(INVALID_ID);
        doThrow(exception).when(validateExistUserUseCase).validateExist(INVALID_ID);

        // Act & Assert
        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> {
            updateUseCase.update(INVALID_ID, customerUpdate);
        });

        assertNotNull(thrownException);
        assertEquals(exception.getMessage(), thrownException.getMessage());
        assertEquals(INVALID_ID, thrownException.getId());

        verify(validateExistUserUseCase).validateExist(INVALID_ID);
        verify(registerRepository, never()).findById(anyLong());
    }

    @Test
    void update_EmailAlreadyExists_ThrowsUserAlreadyRegisteredException() {
        // Arrange
        doNothing().when(validateExistUserUseCase).validateExist(VALID_ID);
        when(registerRepository.findById(VALID_ID)).thenReturn(Optional.of(customerDb));
        UserAlreadyRegisteredException emailException = new UserAlreadyRegisteredException(customerUpdate.getEmail());
        doThrow(emailException).when(validateUniqueEmailUseCase).validate(customerUpdate.getEmail());

        // Act & Assert
        UserAlreadyRegisteredException thrownException = assertThrows(UserAlreadyRegisteredException.class, () -> {
            updateUseCase.update(VALID_ID, customerUpdate);
        });

        assertNotNull(thrownException);
        assertEquals(emailException.getMessage(), thrownException.getMessage());
        assertEquals(customerUpdate.getEmail(), thrownException.getEmail());

        verify(validateExistUserUseCase).validateExist(VALID_ID);
        verify(registerRepository).findById(VALID_ID);
        verify(validateUniqueEmailUseCase).validate(customerUpdate.getEmail());
        verify(identifyUserUpdateUseCase, never()).updateUserByType(any(), any());
        verify(loggingService).logInfo("Iniciando actualización de usuario - ID: {}, Email: {}", VALID_ID, customerUpdate.getEmail());
        verify(loggingService).logDebug("Validando nuevo email: {}", customerUpdate.getEmail());
    }

    @Test
    void update_IdentifyUserUpdateThrowsException_ThrowsException() {
        // Arrange
        doNothing().when(validateExistUserUseCase).validateExist(VALID_ID);
        when(registerRepository.findById(VALID_ID)).thenReturn(Optional.of(customerDb));
        doNothing().when(validateUniqueEmailUseCase).validate(customerUpdate.getEmail());
        RuntimeException updateException = new RuntimeException("Type mismatch error");

        when(identifyUserUpdateUseCase.updateUserByType(any(User.class), any(User.class)))
                .thenThrow(updateException);

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            updateUseCase.update(VALID_ID, customerUpdate);
        });

        assertNotNull(thrownException);
        assertEquals(updateException.getMessage(), thrownException.getMessage());

        verify(validateExistUserUseCase).validateExist(VALID_ID);
        verify(registerRepository).findById(VALID_ID);
        verify(validateUniqueEmailUseCase).validate(customerUpdate.getEmail());
        verify(identifyUserUpdateUseCase).updateUserByType(customerDb, customerUpdate);
        verify(registerRepository, never()).update(anyLong(), any());
    }

    @Test
    void update_RepositoryUpdateThrowsException_ThrowsException() {
        // Arrange
        doNothing().when(validateExistUserUseCase).validateExist(VALID_ID);
        when(registerRepository.findById(VALID_ID)).thenReturn(Optional.of(customerDb));
        doNothing().when(validateUniqueEmailUseCase).validate(customerUpdate.getEmail());
        when(identifyUserUpdateUseCase.updateUserByType(any(User.class), any(User.class)))
                .thenReturn(updatedCustomer);
        RuntimeException repositoryException = new RuntimeException("Database update error");
        when(registerRepository.update(anyLong(), any(User.class))).thenThrow(repositoryException);

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            updateUseCase.update(VALID_ID, customerUpdate);
        });

        assertNotNull(thrownException);
        assertEquals(repositoryException.getMessage(), thrownException.getMessage());

        verify(validateExistUserUseCase).validateExist(VALID_ID);
        verify(registerRepository).findById(VALID_ID);
        verify(validateUniqueEmailUseCase).validate(customerUpdate.getEmail());
        verify(identifyUserUpdateUseCase).updateUserByType(customerDb, customerUpdate);
        verify(registerRepository).update(VALID_ID, updatedCustomer);
    }

    @Test
    void update_NullUser_ThrowsException() {
        // Arrange
        Long validId = VALID_ID;
        User nullUser = null;

        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            updateUseCase.update(validId, nullUser);
        });
    }

    @Test
    void update_NullId_HandlesCorrectly() {
        Long nullId = null;
        User user = customerUpdate;

        assertThrows(NullPointerException.class, () -> {
            updateUseCase.update(nullId, user);
        });
    }

}
