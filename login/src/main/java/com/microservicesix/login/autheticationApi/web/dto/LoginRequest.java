package com.microservicesix.login.autheticationApi.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe tener un formato válido")
    @Size(max = 100, message = "Email no puede exceder 100 caracteres")
    private String email;
    
    @NotBlank(message = "Contraseña es requerida")
    @Size(min = 8, max = 128, message = "Contraseña debe tener entre 8 y 128 caracteres")
    private String password;
}
