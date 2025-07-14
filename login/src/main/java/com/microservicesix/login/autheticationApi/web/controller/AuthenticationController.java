package com.microservicesix.login.autheticationApi.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microservicesix.login.autheticationApi.application.service.AuthenticationService;
import com.microservicesix.login.autheticationApi.domain.model.AuthenticationResult;
import com.microservicesix.login.autheticationApi.web.dto.ApiResponse;
import com.microservicesix.login.autheticationApi.web.dto.LoginRequest;
import com.microservicesix.login.autheticationApi.web.dto.LoginResponse;
import com.microservicesix.login.autheticationApi.web.dto.LogoutRequest;
import com.microservicesix.login.autheticationApi.web.dto.RefreshTokenRequest;
import com.microservicesix.login.autheticationApi.web.webMapper.AuthenticationWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API para autenticación de usuarios")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthenticationWebMapper webMapper;

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve tokens JWT")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Usuario no activo"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Demasiados intentos fallidos")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request) {
        try {
            
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            AuthenticationResult result = authenticationService.authenticate(
                    loginRequest.getEmail(),
                    loginRequest.getPassword(),
                    ipAddress,
                    userAgent
            );
            
            LoginResponse response = webMapper.toLoginResponse(result);
            
            
            
            return ResponseEntity.ok(ApiResponse.success("Login exitoso", response));
            
        } catch (Exception e) {
            throw e;
        }
    }

    @Operation(summary = "Renovar token", description = "Renueva el access token usando el refresh token")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token renovado exitosamente",
            content = @Content(schema = @Schema(implementation = LoginResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token inválido")
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest refreshRequest) {
        try {
            
            AuthenticationResult result = authenticationService.refreshToken(refreshRequest.getRefreshToken());
            LoginResponse response = webMapper.toLoginResponse(result);
            
            
            
            return ResponseEntity.ok(ApiResponse.success("Token renovado exitosamente", response));
            
        } catch (Exception e) {
            
            throw e;
        }
    }

    @Operation(summary = "Cerrar sesión", description = "Revoca el refresh token del usuario")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout exitoso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token inválido")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        try {
            
            
            authenticationService.logout(logoutRequest.getRefreshToken());
            
            
            
            return ResponseEntity.ok(ApiResponse.success("Logout exitoso"));
            
        } catch (Exception e) {
            throw e;
        }
    }

    @Operation(summary = "Validar token", description = "Valida si un access token es válido")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Token válido"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<Boolean>> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Token no proporcionado"));
            }
            
            String token = authHeader.substring(7);
            boolean isValid = authenticationService.validateToken(token);
            
            if (isValid) {
                return ResponseEntity.ok(ApiResponse.success("Token válido", true));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Token inválido", false));
            }
            
        } catch (Exception e) {
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Error al validar token", false));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
