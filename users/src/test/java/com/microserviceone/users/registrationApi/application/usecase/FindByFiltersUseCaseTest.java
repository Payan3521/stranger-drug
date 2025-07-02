package com.microserviceone.users.registrationApi.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;

@ExtendWith(MockitoExtension.class)
public class FindByFiltersUseCaseTest {
    @Mock
    private IRegisterRepository registerRepository;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private FindByFiltersUseCase findByFiltersUseCase;

    private Customer customer1;
    private Customer customer2;
    private Admin admin1;
    private List<User> mixedUsers;

    @BeforeEach
    void setUp() {
        customer1 = new Customer();
        customer1.setId(1L);
        customer1.setName("Juan");
        customer1.setLastName("Pérez");
        customer1.setEmail("juan.perez@gmail.com");
        customer1.setPassword("hashedPassword");
        customer1.setPhone("3127147814");
        customer1.setBirthDate(LocalDate.of(1990, 1, 1));
        customer1.setVerifiedCode(true);
        customer1.setVerifiedTerm(true);

        customer2 = new Customer();
        customer2.setId(2L);
        customer2.setName("María");
        customer2.setLastName("González");
        customer2.setEmail("maria.gonzalez@gmail.com");
        customer2.setPassword("hashedPassword");
        customer2.setPhone("3127147815");
        customer2.setBirthDate(LocalDate.of(1985, 5, 15));
        customer2.setVerifiedCode(true);
        customer2.setVerifiedTerm(true);

        admin1 = new Admin();
        admin1.setId(3L);
        admin1.setName("Carlos");
        admin1.setLastName("Admin");
        admin1.setEmail("carlos.admin@gmail.com");
        admin1.setPassword("hashedPassword");
        admin1.setPhone("3127147816");
        admin1.setArea("IT");
        admin1.setVerifiedTerm(true);
        admin1.setVerifiedCode(true);

        mixedUsers = Arrays.asList(customer1, customer2, admin1);
    }
    
    @Test
    void findByFilters_AllParametersProvided_ReturnsFilteredUsers(){

        // Arrange
        String name = "Juan";
        String lastName = "Pérez";
        String rol = "CUSTOMER";
        List<User> expectedUsers = Arrays.asList(customer1);

        when(registerRepository.findByFilters(name, lastName, rol)).thenReturn(expectedUsers);

        List<User> result = findByFiltersUseCase.findByFilters(name, lastName, rol);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customer1.getId(), result.get(0).getId());
        assertEquals(customer1.getName(), result.get(0).getName());
        assertEquals(customer1.getLastName(), result.get(0).getLastName());
        assertEquals(customer1.getPassword(), result.get(0).getPassword());
        assertEquals(customer1.getPhone(), result.get(0).getPhone());
        assertEquals(customer1.getRol(), result.get(0).getRol());

        assertTrue(result.get(0) instanceof Customer);

        Customer foundCustomer = (Customer) result.get(0);
        assertEquals(customer1.getBirthDate(), foundCustomer.getBirthDate());

        verify(registerRepository).findByFilters(name, lastName, rol);
    }

    @Test
    void findByFilters_OnlyNameProvided_ReturnsMatchingUsers(){
        // Arrange
        String name = "Juan";
        String lastName = null;
        String rol = null;
        List<User> expectedUsers = Arrays.asList(customer1);

        when(registerRepository.findByFilters(name, lastName, rol)).thenReturn(expectedUsers);

        List<User> result = findByFiltersUseCase.findByFilters(name, lastName, rol);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customer1.getId(), result.get(0).getId());
        assertEquals(customer1.getName(), result.get(0).getName());
        assertEquals(customer1.getLastName(), result.get(0).getLastName());
        assertEquals(customer1.getPassword(), result.get(0).getPassword());
        assertEquals(customer1.getPhone(), result.get(0).getPhone());
        assertEquals(customer1.getRol(), result.get(0).getRol());

        assertTrue(result.get(0) instanceof Customer);

        Customer foundCustomer = (Customer) result.get(0);
        assertEquals(customer1.getBirthDate(), foundCustomer.getBirthDate());

        verify(registerRepository).findByFilters(name, lastName, rol);
    }

    @Test
    void findByFilters_NoParametersProvided_ReturnsAllUsers() {
        // Arrange
        String name = null;
        String lastName = null;
        String rol = null;

        when(registerRepository.findByFilters(name, lastName, rol)).thenReturn(mixedUsers);

        // Act
        List<User> result = findByFiltersUseCase.findByFilters(name, lastName, rol);

        assertNotNull(result);
        assertEquals(3, result.size());

        verify(registerRepository).findByFilters(name, lastName, rol);
    }

    @Test
    void findByFilters_NoMatchingUsers_ReturnsEmptyList() {
        // Arrange
        String name = "NoExiste";
        String lastName = "Tampoco";
        String rol = "INVALID_ROLE";
        List<User> emptyList = Collections.emptyList();

        when(registerRepository.findByFilters(name, lastName, rol)).thenReturn(emptyList);

        // Act
        List<User> result = findByFiltersUseCase.findByFilters(name, lastName, rol);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.size());

        verify(registerRepository).findByFilters(name, lastName, rol);

    }

    @Test
    void findByFilters_RepositoryThrowsException_ThrowsException() {
        // Arrange
        String name = "Juan";
        String lastName = "Pérez";
        String rol = "CUSTOMER";
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(registerRepository.findByFilters(anyString(), anyString(), anyString())).thenThrow(repositoryException);

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            findByFiltersUseCase.findByFilters(name, lastName, rol);
        });

        assertNotNull(thrownException);
        assertEquals(repositoryException.getMessage(), thrownException.getMessage());

        verify(registerRepository).findByFilters(name, lastName, rol);
    }

    @Test
    void findByFilters_EmptyStringParameters_HandlesCorrectly() {
        // Arrange
        String name = "";
        String lastName = "";
        String rol = "";
        List<User> expectedUsers = Arrays.asList(customer1);

        when(registerRepository.findByFilters(name, lastName, rol)).thenReturn(expectedUsers);

        // Act
        List<User> result = findByFiltersUseCase.findByFilters(name, lastName, rol);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customer1.getId(), result.get(0).getId());
        assertEquals(customer1.getName(), result.get(0).getName());
        assertEquals(customer1.getLastName(), result.get(0).getLastName());
        assertEquals(customer1.getPassword(), result.get(0).getPassword());
        assertEquals(customer1.getPhone(), result.get(0).getPhone());
        assertEquals(customer1.getRol(), result.get(0).getRol());

        assertTrue(result.get(0) instanceof Customer);

        Customer foundCustomer = (Customer) result.get(0);
        assertEquals(customer1.getBirthDate(), foundCustomer.getBirthDate());

        verify(registerRepository).findByFilters(name, lastName, rol);
    }

    @Test
    void findByFilters_CaseInsensitiveSearch_ReturnsMatchingUsers() {
        // Arrange
        String name = "JUAN"; // Uppercase
        String lastName = "pérez"; // Lowercase
        String rol = "Customer"; // Mixed case
        List<User> expectedUsers = Arrays.asList(customer1);

        when(registerRepository.findByFilters(name, lastName, rol)).thenReturn(expectedUsers);

        // Act
        List<User> result = findByFiltersUseCase.findByFilters(name, lastName, rol);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(customer1.getId(), result.get(0).getId());
        assertEquals(customer1.getName(), result.get(0).getName());
        assertEquals(customer1.getLastName(), result.get(0).getLastName());
        assertEquals(customer1.getPassword(), result.get(0).getPassword());
        assertEquals(customer1.getPhone(), result.get(0).getPhone());
        assertEquals(customer1.getRol(), result.get(0).getRol());

        assertTrue(result.get(0) instanceof Customer);

        Customer foundCustomer = (Customer) result.get(0);
        assertEquals(customer1.getBirthDate(), foundCustomer.getBirthDate());


        verify(registerRepository).findByFilters(name, lastName, rol);
    }
}