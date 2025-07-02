package com.microserviceone.users.registrationApi.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
public class DeleteUseCaseTest {
    @Mock
    private IRegisterRepository registerRepository;

    @Mock
    private ValidateExistUserUseCase validateExistUserUseCase;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private DeleteUseCase deleteUseCase;

    private Customer customer;
    private Admin admin;

    @BeforeEach
    void setUp(){
        customer = new Customer();

        customer.setId(1L);
        customer.setName("juan");
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
    void delete_CustomerExists_ReturnsDeletedCustomer(){
        doNothing().when(validateExistUserUseCase).validateExist(customer.getId());
        when(registerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        when(registerRepository.delete(anyLong())).thenReturn(Optional.of(customer));

        Optional<User> result = deleteUseCase.delete(customer.getId());

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(customer.getId(), result.get().getId());
        assertEquals(customer.getName(), result.get().getName());
        assertEquals(customer.getLastName(), result.get().getLastName());
        assertEquals(customer.getEmail(), result.get().getEmail());
        assertEquals(customer.getPassword(), result.get().getPassword());
        assertEquals(customer.getPhone(), result.get().getPhone());
        assertEquals(customer.getRol(), result.get().getRol());
        assertEquals(customer.isVerifiedCode(), result.get().isVerifiedCode());
        assertEquals(customer.isVerifiedTerm(), result.get().isVerifiedTerm());

        verify(validateExistUserUseCase).validateExist(customer.getId());
        verify(registerRepository).findById(anyLong());
        verify(registerRepository).delete(anyLong());
    }

    @Test
    void delete_AdminExists_ReturnsDeletedAdmin(){
        doNothing().when(validateExistUserUseCase).validateExist(admin.getId());
        when(registerRepository.findById(anyLong())).thenReturn(Optional.of(admin));
        when(registerRepository.delete(anyLong())).thenReturn(Optional.of(admin));

        Optional<User> result = deleteUseCase.delete(admin.getId());

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(admin.getId(), result.get().getId());
        assertEquals(admin.getName(), result.get().getName());
        assertEquals(admin.getLastName(), result.get().getLastName());
        assertEquals(admin.getEmail(), result.get().getEmail());
        assertEquals(admin.getPassword(), result.get().getPassword());
        assertEquals(admin.getPhone(), result.get().getPhone());
        assertEquals(admin.getRol(), result.get().getRol());
        assertEquals(admin.isVerifiedCode(), result.get().isVerifiedCode());
        assertEquals(admin.isVerifiedTerm(), result.get().isVerifiedTerm());

        verify(validateExistUserUseCase).validateExist(admin.getId());
        verify(registerRepository).findById(anyLong());
        verify(registerRepository).delete(anyLong());
    }

    @Test
    void delete_UserNotExists_ThrowsUserNotFoundException(){
        UserNotFoundException exception = new UserNotFoundException(customer.getId());

        doThrow(exception).when(validateExistUserUseCase).validateExist(customer.getId());

        UserNotFoundException excepcionSaliente = assertThrows(UserNotFoundException.class, () -> {
            deleteUseCase.delete(customer.getId());
        });

        assertNotNull(excepcionSaliente);
        assertEquals(exception.getMessage(), excepcionSaliente.getMessage());
        assertEquals(customer.getId(), excepcionSaliente.getId());

        verify(validateExistUserUseCase).validateExist(customer.getId());
        verify(registerRepository, never()).delete(customer.getId());
        verifyNoInteractions(registerRepository);
    }

    @Test
    void delete_RepositoryFindByIdThrowsException_ThrowsException(){
        doNothing().when(validateExistUserUseCase).validateExist(customer.getId());
        RuntimeException repositoryException = new RuntimeException("Database error on find");
        when(registerRepository.findById(anyLong())).thenThrow(repositoryException);

        RuntimeException throwException = assertThrows(RuntimeException.class, () -> {
            deleteUseCase.delete(customer.getId());
        });

        assertEquals(repositoryException.getMessage(), throwException.getMessage());

        verify(validateExistUserUseCase).validateExist(customer.getId());
        verify(registerRepository).findById(anyLong());
        verify(registerRepository, never()).delete(customer.getId());
    }
}
