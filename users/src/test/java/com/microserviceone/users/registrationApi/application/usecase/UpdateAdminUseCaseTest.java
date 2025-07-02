package com.microserviceone.users.registrationApi.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.User.UserRole;

@ExtendWith(MockitoExtension.class)
public class UpdateAdminUseCaseTest {
    @Mock
    private PasswordEncripterUseCase passwordEncripterUseCase;

    @Mock
    private LoggingService loggingService;

    @InjectMocks
    private UpdateAdminUseCase updateAdminUseCase;

    private Admin adminDb;
    private Admin adminEntrante;
    private static final String HASHED_PASSWORD = "newHashedPassword";

    @BeforeEach
    void setUp() {
        adminDb = new Admin();
        adminDb.setId(1L);
        adminDb.setName("Admin");
        adminDb.setLastName("User");
        adminDb.setEmail("admin@gmail.com");
        adminDb.setPassword("oldHashedPassword");
        adminDb.setPhone("3127147814");
        adminDb.setArea("IT");
        adminDb.setRol(UserRole.ADMIN);
        adminDb.setVerifiedCode(true);
        adminDb.setVerifiedTerm(true);

        adminEntrante = new Admin();
        adminEntrante.setName("Super Admin");
        adminEntrante.setLastName("Super User");
        adminEntrante.setEmail("super.admin@gmail.com");
        adminEntrante.setPassword("newPassword123!");
        adminEntrante.setPhone("3127147815");
        adminEntrante.setArea("SYSTEMS");
    }

    @Test
    void updateAdmin_ValidUpdate_ReturnsUpdatedAdmin() {
        // Arrange
        when(passwordEncripterUseCase.encripter(adminEntrante.getPassword(), adminEntrante.getEmail()))
                .thenReturn(HASHED_PASSWORD);

        // Act
        Admin result = updateAdminUseCase.updateAdmin(adminDb, adminEntrante);

        // Assert
        assertNotNull(result);
        assertEquals(adminDb.getId(), result.getId()); // ID should remain the same
        assertEquals(adminEntrante.getName(), result.getName());
        assertEquals(adminEntrante.getLastName(), result.getLastName());
        assertEquals(adminEntrante.getEmail(), result.getEmail());
        assertEquals(HASHED_PASSWORD, result.getPassword());
        assertEquals(adminEntrante.getPhone(), result.getPhone());
        assertEquals(adminDb.getRol(), result.getRol()); // Role should remain the same
        assertEquals(adminEntrante.getArea(), result.getArea());
        assertTrue(result.isVerifiedCode());
        assertTrue(result.isVerifiedTerm());

        verify(passwordEncripterUseCase).encripter(adminEntrante.getPassword(), adminDb.getEmail());
    }

    @Test
    void updateAdmin_PasswordEncryptionFails_ThrowsException() {
        // Arrange
        RuntimeException encryptionException = new RuntimeException("Encryption failed");
        when(passwordEncripterUseCase.encripter(anyString(), anyString())).thenThrow(encryptionException);

        // Act & Assert
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            updateAdminUseCase.updateAdmin(adminDb, adminEntrante);
        });

        assertNotNull(thrownException);
        assertEquals(encryptionException.getMessage(), thrownException.getMessage());

        verify(passwordEncripterUseCase).encripter(adminEntrante.getPassword(), adminDb.getEmail());
    }

    @Test
    void updateAdmin_NullAdminDb_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            updateAdminUseCase.updateAdmin(null, adminEntrante);
        });
    }

    @Test
    void updateAdmin_NullAdminEntrante_ThrowsException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> {
            updateAdminUseCase.updateAdmin(adminDb, null);
        });
    }

    @Test
    void updateAdmin_RoleRemainsAdmin() {
        // Arrange
        when(passwordEncripterUseCase.encripter(adminEntrante.getPassword(), adminEntrante.getEmail()))
                .thenReturn(HASHED_PASSWORD);

        // Act
        Admin result = updateAdminUseCase.updateAdmin(adminDb, adminEntrante);

        // Assert
        assertEquals(UserRole.ADMIN, result.getRol(), "Role should remain ADMIN");

        verify(passwordEncripterUseCase).encripter(adminEntrante.getPassword(), adminDb.getEmail());
    }

    @Test
    void updateAdmin_IdPreserved() {
        // Arrange
        Long originalId = adminDb.getId();
        when(passwordEncripterUseCase.encripter(adminEntrante.getPassword(), adminEntrante.getEmail()))
                .thenReturn(HASHED_PASSWORD);

        // Act
        Admin result = updateAdminUseCase.updateAdmin(adminDb, adminEntrante);

        // Assert
        assertEquals(originalId, result.getId(), "ID should be preserved during update");

        verify(passwordEncripterUseCase).encripter(adminEntrante.getPassword(), adminDb.getEmail());
    }
}
