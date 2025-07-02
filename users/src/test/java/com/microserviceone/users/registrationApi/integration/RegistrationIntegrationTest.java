package com.microserviceone.users.registrationApi.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import com.microserviceone.users.registrationApi.application.service.RegistrationService;
import com.microserviceone.users.registrationApi.domain.exception.AgeIllegalException;
import com.microserviceone.users.registrationApi.domain.model.Admin;
import com.microserviceone.users.registrationApi.domain.model.Customer;
import com.microserviceone.users.registrationApi.domain.model.User;
import com.microserviceone.users.registrationApi.domain.model.User.UserRole;
import com.microserviceone.users.registrationApi.domain.port.out.IRegisterRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class RegistrationIntegrationTest {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private IRegisterRepository registerRepository;

    private Customer customer;
    private Admin admin;

    @BeforeEach
    void setUp(){
        customer = new Customer();
        customer.setName("juan");
        customer.setLastName("perez");
        customer.setEmail("juanpereza3521@gmail.com");
        customer.setPassword("Password123!");
        customer.setPhone("3127147814");
        customer.setBirthDate(LocalDate.of(2006, 10, 10));
        customer.setRol(UserRole.CUSTOMER);
        customer.setVerifiedCode(true);
        customer.setVerifiedTerm(true);

        admin = new Admin();
        admin.setName("admin");
        admin.setLastName("admin");
        admin.setEmail("admin123@gmail.com");
        admin.setPassword("Password123!");
        admin.setPhone("3127147814");
        admin.setArea("SISTEMAS");
        admin.setRol(UserRole.ADMIN);
        admin.setVerifiedCode(true);
        admin.setVerifiedTerm(true);

        //verificar que se crearon correctamente

        assertNotNull(customer);
        assertNotNull(admin);
        assertEquals("juan", customer.getName());
        assertEquals("admin", admin.getName());
    }

    @Test
    void crearYBuscarCliente(){
        //act
        Customer savedCustomer = registrationService.save(customer);
        Optional<User> usuarioEncontrado = registerRepository.findByEmail(customer.getEmail());

        //assert
        assertNotNull(savedCustomer);
        assertNotNull(usuarioEncontrado);
        assertTrue(usuarioEncontrado.isPresent(), "El usuario debería estar presente");
        assertEquals(customer.getEmail(), usuarioEncontrado.get().getEmail(), "El email del usuario encontrado debería coincidir con el email del cliente guardado");
        assertEquals(customer.getName(), savedCustomer.getName(), "El nombre del cliente guardado debería coincidir con el nombre del cliente original");
        assertEquals(customer.getLastName(), savedCustomer.getLastName(), "El apellido del cliente guardado debería coincidir con el apellido del cliente original");
        assertEquals(customer.getPhone(), savedCustomer.getPhone(), "El teléfono del cliente guardado debería coincidir con el teléfono del cliente original");
        assertEquals(customer.getBirthDate(), savedCustomer.getBirthDate(), "La fecha de nacimiento del cliente guardado debería coincidir con la fecha de nacimiento del cliente original");
        assertEquals(customer.isVerifiedCode(), savedCustomer.isVerifiedCode(), "El código de verificación del cliente guardado debería coincidir con el código de verificación del cliente original");
        assertEquals(customer.isVerifiedTerm(), savedCustomer.isVerifiedTerm(), "El término de verificación del cliente guardado debería coincidir con el término de verificación del cliente original");
        assertNotNull(usuarioEncontrado.get().getPassword());
        assertNotEquals(savedCustomer, usuarioEncontrado);
        assertFalse(usuarioEncontrado.get().isAdmin(), "El usuario encontrado no debería ser un administrador");
        assertTrue(usuarioEncontrado.get() instanceof Customer, "El usuario encontrado debería ser una instancia de Customer");
    }

    @Test
    void crearYBuscarAdministrador(){
        //act
        Admin savedAdmin = registrationService.save(admin);
        Optional<User> usuarioEncontrado = registerRepository.findByEmail(admin.getEmail());

        //assert
        assertNotNull(savedAdmin);
        assertNotNull(usuarioEncontrado);
        assertTrue(usuarioEncontrado.isPresent(), "El usuario debería estar presente");
        assertEquals(admin.getEmail(), usuarioEncontrado.get().getEmail(), "El email del usuario encontrado debería coincidir con el email del administrador guardado");
        assertEquals(admin.getName(), savedAdmin.getName(), "El nombre del administrador guardado debería coincidir con el nombre del administrador original");
        assertEquals(admin.getLastName(), savedAdmin.getLastName(), "El apellido del administrador guardado debería coincidir con el apellido del administrador original");
        assertEquals(admin.getPhone(), savedAdmin.getPhone(), "El teléfono del administrador guardado debería coincidir con el teléfono del administrador original");
        assertEquals(admin.getArea(), savedAdmin.getArea(), "El área del administrador guardado debería coincidir con el área del administrador original");
        assertEquals(admin.isVerifiedCode(), savedAdmin.isVerifiedCode(), "El código de verificación del administrador guardado debería coincidir con el código de verificación del administrador original");
        assertEquals(admin.isVerifiedTerm(), savedAdmin.isVerifiedTerm(), "El término de verificación del administrador guardado debería coincidir con el término de verificación del administrador original");
        assertNotNull(usuarioEncontrado.get().getPassword());
        assertNotEquals(savedAdmin, usuarioEncontrado);
        assertTrue(usuarioEncontrado.get().isAdmin(), "El usuario encontrado debería ser un administrador");
        assertTrue(usuarioEncontrado.get() instanceof Admin, "El usuario encontrado debería ser una instancia de Admin");
    }

    @Test
    void validarEdadMinimaCliente(){
        LocalDate fechaMenorDeEdad = LocalDate.now().minusYears(17);

        customer.setBirthDate(fechaMenorDeEdad);

        assertThrows(AgeIllegalException.class, ()->{
            registrationService.save(customer);
        });
    }

    @Test
    void buscarPorId_Cliente_Success() {
        Customer savedCustomer = registrationService.save(customer);
        Optional<User> found = registrationService.findById(savedCustomer.getId());
        assertTrue(found.isPresent());
        assertEquals(savedCustomer.getId(), found.get().getId());
        assertEquals(savedCustomer.getEmail(), found.get().getEmail());
        assertTrue(found.get() instanceof Customer);
    }

    @Test
    void buscarPorId_NoExiste() {
        assertThrows(com.microserviceone.users.registrationApi.application.exception.UserNotFoundException.class, () -> registrationService.findById(999999L));
    }

    @Test
    void buscarPorFiltros_Success() {
        registrationService.save(customer);
        registrationService.save(admin);
        java.util.List<User> result = registrationService.findByFilters("juan", "perez", "CUSTOMER");
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.get(0) instanceof Customer);
        assertEquals("juan", result.get(0).getName());
    }

    @Test
    void buscarPorFiltros_NoResultados() {
        java.util.List<User> result = registrationService.findByFilters("noexiste", "noexiste", "CUSTOMER");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void actualizarCliente_Success() {
        Customer savedCustomer = registrationService.save(customer);
        savedCustomer.setName("NuevoNombre");
        savedCustomer.setPhone("3000000000");
        Optional<User> updated = registrationService.update(savedCustomer.getId(), savedCustomer);
        assertTrue(updated.isPresent());
        assertEquals("NuevoNombre", updated.get().getName());
        assertEquals("3000000000", updated.get().getPhone());
    }

    @Test
    void actualizarCliente_NoExiste() {
        customer.setName("NuevoNombre");
        assertThrows(com.microserviceone.users.registrationApi.application.exception.UserNotFoundException.class, () -> registrationService.update(999999L, customer));
    }

    @Test
    void eliminarCliente_Success() {
        Customer savedCustomer = registrationService.save(customer);
        Optional<User> deleted = registrationService.delete(savedCustomer.getId());
        assertTrue(deleted.isPresent());
        assertEquals(savedCustomer.getId(), deleted.get().getId());
        // Verificar que ya no existe
        Optional<User> afterDelete = registerRepository.findById(savedCustomer.getId());
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    void eliminarCliente_NoExiste() {
        assertThrows(com.microserviceone.users.registrationApi.application.exception.UserNotFoundException.class, () -> registrationService.delete(999999L));
    }
}