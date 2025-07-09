package com.microservicesix.login.autheticationApi.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshTokenRequest {
    
    @NotBlank(message = "Refresh token es requerido")
    private String refreshToken;
}
