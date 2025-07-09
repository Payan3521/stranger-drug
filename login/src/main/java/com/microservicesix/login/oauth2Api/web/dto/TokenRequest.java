package com.microservicesix.login.oauth2Api.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenRequest {
    
    @NotBlank(message = "Grant type es requerido")
    private String grantType; // "authorization_code"
    
    @NotBlank(message = "Code es requerido")
    private String code;
    
    @NotBlank(message = "Redirect URI es requerido")
    private String redirectUri;
    
    @NotBlank(message = "Client ID es requerido")
    private String clientId;
    
    private String clientSecret; // Opcional para public clients
}
