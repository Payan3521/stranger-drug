package com.microserviceone.users.registrationApi.integration;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.AdminEntity;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.CustomerEntity;
import com.microserviceone.users.registrationApi.infraestructure.persistance.entity.UserEntity;
import com.microserviceone.users.registrationApi.infraestructure.persistance.repository.ORMregister;
import com.microserviceone.users.registrationApi.web.dto.AdminRequest;
import com.microserviceone.users.registrationApi.web.dto.ApiResponse;
import com.microserviceone.users.registrationApi.web.dto.CustomerRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RegistrationIntegrationSistema {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ORMregister ormRegister;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp(){
        baseUrl = "http://localhost:" + port + "/register";
        // Aquí puedes agregar más configuraciones necesarias antes de cada prueba
        ormRegister.deleteAll(); // Limpia la base de datos antes de cada prueba
    }

    @Test
    void registerCustomer_CompleteFlow_Success(){
        CustomerRequest customerRequest = new CustomerRequest();

        customerRequest.setName("John");
        customerRequest.setLastName("Doe");
        customerRequest.setEmail("pablobedoya3521@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147814");
        customerRequest.setBirthDate(LocalDate.of(2006, 10, 10));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        //Act

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        //asserts http response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Customer registered successfully", response.getBody().getMessage());

        //assert Database State
        Optional<UserEntity> savedUser = ormRegister.findByEmail(customerRequest.getEmail());
        assertTrue(savedUser.isPresent());
        assertEquals(customerRequest.getName(), savedUser.get().getName());
        assertEquals(customerRequest.getLastName(), savedUser.get().getLastName());
        assertEquals(customerRequest.getEmail(), savedUser.get().getEmail());
        assertEquals("CUSTOMER", savedUser.get().getRol());
        assertNotEquals(customerRequest.getPassword(), savedUser.get().getPassword());
        assertEquals(customerRequest.getPhone(), savedUser.get().getPhone());

        //verify it's CustomerEntity with birthDate
        assertTrue(savedUser.get() instanceof CustomerEntity);
        assertEquals(customerRequest.getBirthDate(), ((CustomerEntity) savedUser.get()).getBirthDate());
    }

    @Test
    void registerCustomer_DuplicateEmail_ReturnsConflict(){
        CustomerEntity existingCustomer = new CustomerEntity();
        existingCustomer.setName("John");
        existingCustomer.setLastName("Doe");
        existingCustomer.setEmail("pablobedoya352@gmail.com");
        existingCustomer.setPassword("Password123!");
        existingCustomer.setPhone("3127147814");
        existingCustomer.setVerifiedCode(true);
        existingCustomer.setVerifiedTerm(true);
        existingCustomer.setRol("CUSTOMER");
        existingCustomer.setBirthDate(LocalDate.of(2000, 1, 1));


        ormRegister.save(existingCustomer);
        ormRegister.flush();

        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("John");
        customerRequest.setLastName("Doe");
        customerRequest.setEmail("pablobedoya352@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147814");
        customerRequest.setBirthDate(LocalDate.of(2006, 10, 10));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertEquals("El usuario con email pablobedoya352@gmail.com ya está registrado", response.getBody().getMessage());
    }

    @Test
    void registerAdmin_CompleteFlow_Success() {

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("Alice");
        adminRequest.setLastName("Smith");
        adminRequest.setEmail("alice.smith@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147815");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Admin registered successfully", response.getBody().getMessage());
        // Puedes agregar más asserts para verificar en la base de datos si lo deseas
        //assert Database State
        Optional<UserEntity> savedUser = ormRegister.findByEmail(adminRequest.getEmail());
        assertTrue(savedUser.isPresent());
        assertEquals(adminRequest.getName(), savedUser.get().getName());
        assertEquals(adminRequest.getLastName(), savedUser.get().getLastName());
        assertEquals(adminRequest.getEmail(), savedUser.get().getEmail());
        assertEquals("ADMIN", savedUser.get().getRol());
        assertNotEquals(adminRequest.getPassword(), savedUser.get().getPassword());
        assertEquals(adminRequest.getPhone(), savedUser.get().getPhone());

        //verify it's AdminEntity with area
        assertTrue(savedUser.get() instanceof AdminEntity);
        assertEquals(adminRequest.getArea(), ((AdminEntity) savedUser.get()).getArea());
    }

    @Test
    void registerAdmin_DuplicateEmail_ReturnsConflict() {
        AdminEntity existingAdmin = new AdminEntity();
        existingAdmin.setName("Alice");
        existingAdmin.setLastName("Smith");
        existingAdmin.setEmail("alice.smith@gmail.com");
        existingAdmin.setPassword("Password123!");
        existingAdmin.setPhone("3127147815");
        existingAdmin.setArea("IT");
        existingAdmin.setRol("ADMIN");
        existingAdmin.setVerifiedCode(true);
        existingAdmin.setVerifiedTerm(true);

        ormRegister.save(existingAdmin);
        ormRegister.flush();

        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("Alice");
        adminRequest.setLastName("Smith");
        adminRequest.setEmail("alice.smith@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147815");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().contains("ya está registrado"));
    }

    @Test
    void registerCustomer_AgeNotAllowed_ReturnsBadRequest() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("Young");
        customerRequest.setLastName("User");
        customerRequest.setEmail("young.user@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147816");
        customerRequest.setBirthDate(LocalDate.now().minusYears(17)); // 17 años

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().toLowerCase().contains("edad"));
    }

    @Test
    void registerCustomer_Exact18Years_Success() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("Eighteen");
        customerRequest.setLastName("User");
        customerRequest.setEmail("eighteen.user@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147817");
        customerRequest.setBirthDate(LocalDate.now().minusYears(18));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void registerCustomer_VeryOldYears_Success() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("Old");
        customerRequest.setLastName("User");
        customerRequest.setEmail("old.user@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147820");
        customerRequest.setBirthDate(LocalDate.now().minusYears(100)); // 100 años

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    // Pruebas para validaciones de email (Customer y Admin)
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void registerCustomer_InvalidEmail_ReturnsBadRequest(String invalidEmail) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail(invalidEmail.equals("null") ? null : invalidEmail);
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147821");
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void registerAdmin_InvalidEmail_ReturnsBadRequest(String invalidEmail) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail(invalidEmail.equals("null") ? null : invalidEmail);
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147822");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "test@yahoo.com",
        "test@hotmail.com",
        "test@outlook.com",
        "test@gmail.co",
        "test@gmail.comm",
        "test@.com",
        "test@gmail",
        "test@gmail.",
        "test@.gmail.com",
        "test@gmail..com"
    })
    void registerCustomer_NonGmailEmail_ReturnsBadRequest(String invalidEmail) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail(invalidEmail);
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147823");
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "test@yahoo.com",
        "test@hotmail.com",
        "test@outlook.com",
        "test@gmail.co",
        "test@gmail.comm",
        "test@.com",
        "test@gmail",
        "test@gmail.",
        "test@.gmail.com",
        "test@gmail..com"
    })
    void registerAdmin_NonGmailEmail_ReturnsBadRequest(String invalidEmail) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail(invalidEmail);
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147824");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a@gmail.com", "ab@gmail.com", "abc@gmail.com", "abcd@gmail.com", "abcde@gmail.com"})
    void registerCustomer_ShortEmail_ReturnsBadRequest(String shortEmail) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail(shortEmail);
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147825");
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {"a@gmail.com", "ab@gmail.com", "abc@gmail.com", "abcd@gmail.com", "abcde@gmail.com"})
    void registerAdmin_ShortEmail_ReturnsBadRequest(String shortEmail) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail(shortEmail);
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147826");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    // Pruebas para validaciones de nombre (Admin)
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void registerAdmin_InvalidName_ReturnsBadRequest(String invalidName) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName(invalidName.equals("null") ? null : invalidName);
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147827");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "B", "C"})
    void registerAdmin_ShortName_ReturnsBadRequest(String shortName) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName(shortName);
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147828");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    // Pruebas para validaciones de teléfono (Admin)
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void registerAdmin_InvalidPhone_ReturnsBadRequest(String invalidPhone) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone(invalidPhone.equals("null") ? null : invalidPhone);
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123", 
        "312714781", 
        "3127147819123", 
        "+573127147814", 
        "312714781a",
        "312714781@",
        "312714781 ",
        " 3127147814"
    })
    void registerAdmin_InvalidPhoneFormat_ReturnsBadRequest(String invalidPhone) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone(invalidPhone);
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    // Pruebas para validaciones de área (Admin)
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void registerAdmin_InvalidArea_ReturnsBadRequest(String invalidArea) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147829");
        adminRequest.setArea(invalidArea.equals("null") ? null : invalidArea);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "B", "C"})
    void registerAdmin_ShortArea_ReturnsBadRequest(String shortArea) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147830");
        adminRequest.setArea(shortArea);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void registerAdmin_LongArea_ReturnsBadRequest() {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147831");
        // Crear un área de más de 100 caracteres
        adminRequest.setArea("A".repeat(101));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    // Pruebas para validaciones de password (Admin)
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void registerAdmin_InvalidPassword_ReturnsBadRequest(String invalidPassword) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        adminRequest.setPassword(invalidPassword.equals("null") ? null : invalidPassword);
        adminRequest.setPhone("3127147832");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1234567", // Muy corta
        "password", // Sin mayúsculas, números ni caracteres especiales
        "PASSWORD", // Sin minúsculas, números ni caracteres especiales
        "Password", // Sin números ni caracteres especiales
        "Password123", // Sin caracteres especiales
        "password123!", // Sin mayúsculas
        "PASSWORD123!", // Sin minúsculas
        "Password!", // Sin números
        "Pass123" // Muy corta
    })
    void registerAdmin_WeakPassword_ReturnsBadRequest(String weakPassword) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        adminRequest.setPassword(weakPassword);
        adminRequest.setPhone("3127147833");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    // Pruebas para validaciones de password (Customer)
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void registerCustomer_InvalidPassword_ReturnsBadRequest(String invalidPassword) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        customerRequest.setPassword(invalidPassword.equals("null") ? null : invalidPassword);
        customerRequest.setPhone("3127147834");
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "1234567", // Muy corta
        "password", // Sin mayúsculas, números ni caracteres especiales
        "PASSWORD", // Sin minúsculas, números ni caracteres especiales
        "Password", // Sin números ni caracteres especiales
        "Password123", // Sin caracteres especiales
        "password123!", // Sin mayúsculas
        "PASSWORD123!", // Sin minúsculas
        "Password!", // Sin números
        "Pass123" // Muy corta
    })
    void registerCustomer_WeakPassword_ReturnsBadRequest(String weakPassword) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        customerRequest.setPassword(weakPassword);
        customerRequest.setPhone("3127147835");
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    // Pruebas para validaciones de lastName (Customer)
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void registerCustomer_InvalidLastName_ReturnsBadRequest(String invalidLastName) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName(invalidLastName.equals("null") ? null : invalidLastName);
        customerRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147836");
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "B", "C"})
    void registerCustomer_ShortLastName_ReturnsBadRequest(String shortLastName) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName(shortLastName);
        customerRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147837");
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    // Pruebas para validaciones de lastName (Admin)
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void registerAdmin_InvalidLastName_ReturnsBadRequest(String invalidLastName) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName(invalidLastName.equals("null") ? null : invalidLastName);
        adminRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147838");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "B", "C"})
    void registerAdmin_ShortLastName_ReturnsBadRequest(String shortLastName) {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName(shortLastName);
        adminRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147839");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    // Pruebas para validaciones de nombre (Customer)
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void registerCustomer_InvalidName_ReturnsBadRequest(String invalidName) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName(invalidName.equals("null") ? null : invalidName);
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147840");
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "B", "C"})
    void registerCustomer_ShortName_ReturnsBadRequest(String shortName) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName(shortName);
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147841");
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    // Pruebas para validaciones de teléfono (Customer)
    @ParameterizedTest
    @ValueSource(strings = {"", " ", "null"})
    void registerCustomer_InvalidPhone_ReturnsBadRequest(String invalidPhone) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone(invalidPhone.equals("null") ? null : invalidPhone);
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "123", 
        "312714781", 
        "3127147819123", 
        "+573127147814", 
        "312714781a",
        "312714781@",
        "312714781 ",
        " 3127147814",
        "2127147814", // No empieza con 3
        "4127147814", // No empieza con 3
        "5127147814"  // No empieza con 3
    })
    void registerCustomer_InvalidPhoneFormat_ReturnsBadRequest(String invalidPhone) {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail("test" + System.currentTimeMillis() + "@gmail.com");
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone(invalidPhone);
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    // Pruebas para casos edge de email válido
    @Test
    void registerCustomer_ValidGmailWithSpecialChars_Success() {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setName("ValidName");
        customerRequest.setLastName("ValidLastName");
        customerRequest.setEmail("test.user+tag@gmail.com"); // Email con punto y +
        customerRequest.setPassword("Password123!");
        customerRequest.setPhone("3127147842");
        customerRequest.setBirthDate(LocalDate.now().minusYears(20));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/customer", request, ApiResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void registerAdmin_ValidGmailWithSpecialChars_Success() {
        AdminRequest adminRequest = new AdminRequest();
        adminRequest.setName("ValidName");
        adminRequest.setLastName("ValidLastName");
        adminRequest.setEmail("test.user+tag@gmail.com"); // Email con punto y +
        adminRequest.setPassword("Password123!");
        adminRequest.setPhone("3127147843");
        adminRequest.setArea("IT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(adminRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(baseUrl + "/admin", request, ApiResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
    }

    @Test
    void findUserById_Success() {
        // Arrange: crear y guardar un usuario
        CustomerEntity customer = new CustomerEntity();
        customer.setName("Find");
        customer.setLastName("ById");
        customer.setEmail("findbyid@gmail.com");
        customer.setPassword("Password123!");
        customer.setPhone("3127147850");
        customer.setBirthDate(LocalDate.of(1995, 5, 5));
        customer.setRol("CUSTOMER");
        customer.setVerifiedCode(true);
        customer.setVerifiedTerm(true);

        CustomerEntity saved = ormRegister.save(customer);
        ormRegister.flush();

        // Act
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(baseUrl + "/id/" + saved.getId(), ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().toLowerCase().contains("success"));
    }

    @Test
    void findUserById_NotFound() {
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(baseUrl + "/id/999999", ApiResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void findUsersByFilters_Success() {
        // Arrange: crear varios usuarios
        CustomerEntity customer1 = new CustomerEntity();
        customer1.setName("Filter");
        customer1.setLastName("Test");
        customer1.setEmail("filter1@gmail.com");
        customer1.setPassword("Password123!");
        customer1.setPhone("3127147851");
        customer1.setBirthDate(LocalDate.of(1990, 1, 1));
        customer1.setRol("CUSTOMER");
        customer1.setVerifiedCode(true);
        customer1.setVerifiedTerm(true);
        ormRegister.save(customer1);
        ormRegister.flush();

        // Act
        String url = baseUrl + "?name=Filter&lastName=Test&rol=CUSTOMER";
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().toLowerCase().contains("success"));
    }

    @Test
    void findUsersByFilters_NoContent() {
        String url = baseUrl + "?name=NoExiste&lastName=NoExiste&rol=CUSTOMER";
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(url, ApiResponse.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void updateCustomer_Success() {
        // Arrange: crear y guardar un customer
        CustomerEntity customer = new CustomerEntity();
        customer.setName("Update");
        customer.setLastName("Customer");
        customer.setEmail("updatecustomer@gmail.com");
        customer.setPassword("Password123!");
        customer.setPhone("3127147852");
        customer.setBirthDate(LocalDate.of(1992, 2, 2));
        customer.setRol("CUSTOMER");
        customer.setVerifiedCode(true);
        customer.setVerifiedTerm(true);
        ormRegister.save(customer);
        ormRegister.flush();

        // Nuevo request para actualizar
        CustomerRequest updateRequest = new CustomerRequest();
        updateRequest.setName("Updated");
        updateRequest.setLastName("Customer");
        updateRequest.setEmail("updatecustomer@gmail.com");
        updateRequest.setPassword("NewPassword123!");
        updateRequest.setPhone("3127147853");
        updateRequest.setBirthDate(LocalDate.of(1992, 2, 2));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(updateRequest, headers);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate
            .exchange(baseUrl + "/customer/" + customer.getId(), org.springframework.http.HttpMethod.PUT, request, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().toLowerCase().contains("success"));
        // Verificar en la base de datos
        Optional<UserEntity> updated = ormRegister.findById(customer.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated", updated.get().getName());
        assertEquals("3127147853", updated.get().getPhone());
    }

    @Test
    void updateCustomer_NotFound() {
        CustomerRequest updateRequest = new CustomerRequest();
        updateRequest.setName("Updated");
        updateRequest.setLastName("Customer");
        updateRequest.setEmail("notfound@gmail.com");
        updateRequest.setPassword("NewPassword123!");
        updateRequest.setPhone("3127147854");
        updateRequest.setBirthDate(LocalDate.of(1992, 2, 2));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerRequest> request = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate
            .exchange(baseUrl + "/customer/999999", org.springframework.http.HttpMethod.PUT, request, ApiResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void updateAdmin_Success() {
        // Arrange: crear y guardar un admin
        AdminEntity admin = new AdminEntity();
        admin.setName("Update");
        admin.setLastName("Admin");
        admin.setEmail("updateadmin@gmail.com");
        admin.setPassword("Password123!");
        admin.setPhone("3127147855");
        admin.setArea("IT");
        admin.setRol("ADMIN");
        admin.setVerifiedCode(true);
        admin.setVerifiedTerm(true);
        ormRegister.save(admin);
        ormRegister.flush();

        // Nuevo request para actualizar
        AdminRequest updateRequest = new AdminRequest();
        updateRequest.setName("Updated");
        updateRequest.setLastName("Admin");
        updateRequest.setEmail("updateadmin@gmail.com");
        updateRequest.setPassword("NewPassword123!");
        updateRequest.setPhone("3127147856");
        updateRequest.setArea("SISTEMAS");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(updateRequest, headers);

        // Act
        ResponseEntity<ApiResponse> response = restTemplate
            .exchange(baseUrl + "/admin/" + admin.getId(), org.springframework.http.HttpMethod.PUT, request, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().toLowerCase().contains("success"));
        // Verificar en la base de datos
        Optional<UserEntity> updated = ormRegister.findById(admin.getId());
        assertTrue(updated.isPresent());
        assertEquals("Updated", updated.get().getName());
        assertEquals("3127147856", updated.get().getPhone());
        assertEquals("SISTEMAS", ((AdminEntity)updated.get()).getArea());
    }

    @Test
    void updateAdmin_NotFound() {
        AdminRequest updateRequest = new AdminRequest();
        updateRequest.setName("Updated");
        updateRequest.setLastName("Admin");
        updateRequest.setEmail("notfoundadmin@gmail.com");
        updateRequest.setPassword("NewPassword123!");
        updateRequest.setPhone("3127147857");
        updateRequest.setArea("SISTEMAS");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AdminRequest> request = new HttpEntity<>(updateRequest, headers);

        ResponseEntity<ApiResponse> response = restTemplate
            .exchange(baseUrl + "/admin/999999", org.springframework.http.HttpMethod.PUT, request, ApiResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void deleteUser_Success() {
        // Arrange: crear y guardar un usuario
        CustomerEntity customer = new CustomerEntity();
        customer.setName("Delete");
        customer.setLastName("User");
        customer.setEmail("deleteuser@gmail.com");
        customer.setPassword("Password123!");
        customer.setPhone("3127147858");
        customer.setBirthDate(LocalDate.of(1993, 3, 3));
        customer.setRol("CUSTOMER");
        customer.setVerifiedCode(true);
        customer.setVerifiedTerm(true);
        ormRegister.save(customer);
        ormRegister.flush();

        // Act
        ResponseEntity<ApiResponse> response = restTemplate
            .exchange(baseUrl + "/id/" + customer.getId(), org.springframework.http.HttpMethod.DELETE, null, ApiResponse.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getMessage().toLowerCase().contains("success"));
        // Verificar que ya no existe en la base de datos
        Optional<UserEntity> deleted = ormRegister.findById(customer.getId());
        assertTrue(deleted.isEmpty());
    }

    @Test
    void deleteUser_NotFound() {
        ResponseEntity<ApiResponse> response = restTemplate
            .exchange(baseUrl + "/id/999999", org.springframework.http.HttpMethod.DELETE, null, ApiResponse.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isSuccess());
    }
}