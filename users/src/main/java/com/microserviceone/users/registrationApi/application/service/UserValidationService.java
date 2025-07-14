package com.microserviceone.users.registrationApi.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import com.microserviceone.users.core.config.internalSecurity.InternalJwtService;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.application.exception.UserPrerequisitesNotMetException;
import com.microserviceone.users.registrationApi.web.dto.ApiResponse;
import com.microserviceone.users.termsAndConditionsApi.web.dto.AcceptedMultipleRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserValidationService {
    
    private final RestTemplate restTemplate;
    private final LoggingService loggingService;
    private final TermsRetrievalService termsRetrievalService;
    private final InternalJwtService internalJwtService;
    
    @Value("${server.port:8082}")
    private String serverPort;
    
    private String getBaseUrl() {
        return "http://localhost:" + serverPort;
    }
    
    /**
     * Acepta los términos y condiciones para un usuario recién registrado
     */
    public void acceptTermsAndConditions(Long userId, String userEmail) {
        try {
            loggingService.logInfo("Aceptando términos y condiciones para usuario ID: {}, Email: {}", userId, userEmail);
            
            String url = getBaseUrl() + "/terms/accept/multiple";
            
            // Crear el request para aceptar múltiples términos
            AcceptedMultipleRequest request = new AcceptedMultipleRequest();
            request.setUserId(userId);
            request.setUserEmail(userEmail);
            request.setTermIds(termsRetrievalService.getActiveTermIds());

            String jwt = internalJwtService.generateToken("user-service");

            // Solo Content-Type, sin autenticación
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(jwt);
    

            HttpEntity<AcceptedMultipleRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url, entity, ApiResponse.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                loggingService.logInfo("Términos y condiciones aceptados exitosamente para usuario ID: {}", userId);
            } else {
                loggingService.logError("Error al aceptar términos y condiciones para usuario ID: {}", userId);
                throw new UserPrerequisitesNotMetException("Error al aceptar términos y condiciones");
            }
            
        } catch (HttpClientErrorException e) {
            loggingService.logError("Error HTTP al aceptar términos y condiciones para usuario ID: {} - Status: {}", 
                userId, e.getStatusCode());
            throw new UserPrerequisitesNotMetException("Error al aceptar términos y condiciones: " + e.getMessage(), e);
        } catch (Exception e) {
            loggingService.logError("Error inesperado al aceptar términos y condiciones para usuario ID: {}", userId, e);
            throw new UserPrerequisitesNotMetException("Error al aceptar términos y condiciones: " + e.getMessage(), e);
        }
    }

    /**
     * Valida que el email haya sido verificado
     */
    public void validateEmailVerification(String email) {
        try {
            loggingService.logInfo("Validando verificación de email: {}", email);
            
            String url = getBaseUrl() + "/verification/status";
            SendCodeRequest request = new SendCodeRequest();
            request.setEmail(email);

            String jwt = internalJwtService.generateToken("user-service");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(jwt);
            
            // LOG PARA DEBUG
            System.out.println("[UserValidationService] Generated JWT: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");
            System.out.println("[UserValidationService] Calling URL: " + url);
            System.out.println("[UserValidationService] Request payload: " + request.getEmail());

            HttpEntity<SendCodeRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url, entity, ApiResponse.class);
            
            System.out.println("[UserValidationService] Response status: " + response.getStatusCode());
            System.out.println("[UserValidationService] Response body: " + response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK) {
                loggingService.logInfo("Email verificado exitosamente: {}", email);
            } else {
                loggingService.logError("Error al validar verificación de email: {}", email);
                throw new UserPrerequisitesNotMetException("El email no ha sido verificado");
            }
            
        } catch (HttpClientErrorException e) {
            loggingService.logError("Error HTTP al validar verificación de email: {} - Status: {}", 
                email, e.getStatusCode());
            System.out.println("[UserValidationService] HTTP Error: " + e.getResponseBodyAsString());
            throw new UserPrerequisitesNotMetException("El email no ha sido verificado: " + e.getMessage(), e);
        } catch (Exception e) {
            loggingService.logError("Error inesperado al validar verificación de email: {}", email, e);
            throw new UserPrerequisitesNotMetException("Error al validar verificación de email: " + e.getMessage(), e);
        }
    }
    
    /**
     * Valida solo la verificación de email (los términos se aceptan después del registro)
     */
    public void validateUserPrerequisites(String email) {
        loggingService.logInfo("Iniciando validación de email previa al registro para email: {}", email);
        
        // Validar verificación de email
        validateEmailVerification(email);
        
        loggingService.logInfo("Validación de email completada exitosamente para email: {}", email);
    }
    
    // Clase auxiliar para la petición de verificación de email
    public static class SendCodeRequest {
        private String email;
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
    }
} 