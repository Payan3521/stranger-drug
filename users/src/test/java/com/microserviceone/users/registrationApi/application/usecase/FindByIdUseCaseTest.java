package com.microserviceone.users.registrationApi.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
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
import com.microserviceone.users.registrationApi.application.exception.UserNotFoundException;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;

@ExtendWith(MockitoExtension.class)
public class FindByIdUseCaseTest {
    @Mock
    private ValidateExistUserUseCase validateExistUserUseCase;

    @Mock
    private IRegisterRepository registerRepository;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private FindByIdUseCase findByIdUseCase;

    private Customer customer;
    private Admin admin;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Juan");
        customer.setLastName("Pérez");
        customer.setEmail("juan.perez@gmail.com");
        customer.setPassword("hashedPassword");
        customer.setPhone("3127147814");
        customer.setBirthDate(LocalDate.of(1990, 1, 1));
        customer.setVerifiedCode(true);
        customer.setVerifiedTerm(true);

        admin = new Admin();
        admin.setId(1L);
        admin.setName("Admin");
        admin.setLastName("User");
        admin.setEmail("admin@gmail.com");
        admin.setPassword("hashedPassword");
        admin.setPhone("3127147815");
        admin.setArea("IT");
        admin.setVerifiedCode(true);
        admin.setVerifiedTerm(true);
    }

    @Test
    void findById_CustomerExists_ReturnsCustomer() {
        doNothing().when(validateExistUserUseCase).validateExist(customer.getId());
        when(registerRepository.findById(anyLong())).thenReturn(Optional.of(customer));

        Optional<User> result = findByIdUseCase.findById(customer.getId());

        assertNotNull(result);
        assertTrue(result.isPresent());

        assertEquals(customer.getId(), result.get().getId());
        assertEquals(customer.getEmail(), result.get().getEmail());
        assertEquals(customer.getName(), result.get().getName());

        verify(validateExistUserUseCase).validateExist(customer.getId());
        verify(registerRepository).findById(anyLong());
    }

    @Test
    void findById_AdminExists_ReturnsAdmin() {
        // Arrange
        doNothing().when(validateExistUserUseCase).validateExist(admin.getId());
        when(registerRepository.findById(anyLong())).thenReturn(Optional.of(admin));

        // Act
        Optional<User> result = findByIdUseCase.findById(admin.getId());

        // Assert
        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(admin.getId(), result.get().getId());
        assertEquals(admin.getEmail(), result.get().getEmail());
        assertEquals(admin.getName(), result.get().getName());
        assertTrue(result.get() instanceof Admin);

        verify(validateExistUserUseCase).validateExist(admin.getId());
        verify(registerRepository).findById(anyLong());
    }

    @Test
    void findById_UserNotExists_ThrowsException() {
        // Arrange
        UserNotFoundException exception = new UserNotFoundException(customer.getId());
        doThrow(exception).when(validateExistUserUseCase).validateExist(customer.getId());

        // Act & Assert
        UserNotFoundException thrownException = assertThrows(UserNotFoundException.class, () -> {
            findByIdUseCase.findById(customer.getId());
        });

        assertNotNull(thrownException);
        assertEquals(exception.getMessage(), thrownException.getMessage());
        assertEquals(customer.getId(), thrownException.getId());

        verify(validateExistUserUseCase).validateExist(customer.getId());
    }

    @Test
    void findById_RepositoryThrowsException_ThrowsException() {
        // Arrange
        doNothing().when(validateExistUserUseCase).validateExist(admin.getId());
        RuntimeException repositoryException = new RuntimeException("Database error");
        when(registerRepository.findById(anyLong())).thenThrow(repositoryException);

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            findByIdUseCase.findById(admin.getId());
        });

        assertNotNull(thrownException);
        assertEquals(repositoryException.getMessage(), thrownException.getMessage());

        verify(validateExistUserUseCase).validateExist(admin.getId());
        verify(registerRepository).findById(anyLong());
    }

    @Test
    void findById_NullId_HandlesCorrectly() {
        Long nullId = null;
        doNothing().when(validateExistUserUseCase).validateExist(nullId);
        when(registerRepository.findById(nullId)).thenReturn(Optional.empty());


        Exception thrException = assertThrows(Exception.class, () -> {
            findByIdUseCase.findById(nullId);
        });
       
        assertNotNull(thrException);

        verify(validateExistUserUseCase).validateExist(nullId);
        verify(registerRepository).findById(nullId);
    }
}
