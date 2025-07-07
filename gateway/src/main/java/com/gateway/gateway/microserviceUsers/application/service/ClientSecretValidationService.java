package com.gateway.gateway.microserviceUsers.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.gateway.gateway.microserviceUsers.domain.port.IClientSecretValidationService;
import com.gateway.gateway.common.logging.LoggingService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientSecretValidationService implements IClientSecretValidationService {

    private final LoggingService loggingService;

    @Value("${app.client.secret-header-name:X-Client-Secret}")
    private String secretHeaderName;

    @Value("${app.client.secret-value}")
    private String secretValue;


    @Override
    public boolean isValidClientSecret(String clientSecret) {
        loggingService.logSecurity("ClientSecretValidationService: Iniciando validación de client secret");
        
        if (clientSecret == null || secretValue == null) {
            loggingService.logSecurityWarning("ClientSecretValidationService: Client secret o valor esperado es null - ACCESO DENEGADO");
            loggingService.logGatewayDebug("ClientSecretValidationService: Client secret recibido: {}, Valor esperado: {}", 
                clientSecret != null ? "PRESENTE" : "NULL", 
                secretValue != null ? "CONFIGURADO" : "NULL");
            return false;
        }

        boolean isValid = secretValue.equals(clientSecret);
        
        loggingService.logGatewayDebug("ClientSecretValidationService: Validación de client secret: {} - Header recibido: {}", 
            isValid ? "EXITOSA" : "FALLIDA", 
            clientSecret != null ? "***PRESENTE***" : "AUSENTE");
        
        if (!isValid) {
            loggingService.logSecurityWarning("ClientSecretValidationService: Client secret inválido - Valor esperado no coincide con el recibido");
            loggingService.logGatewayDebug("ClientSecretValidationService: Longitud del secret recibido: {}, Longitud del valor esperado: {}", 
                clientSecret.length(), secretValue.length());
        } else {
            loggingService.logSecurity("ClientSecretValidationService: Validación de client secret EXITOSA");
        }
        
        return isValid;
    }

    @Override
    public String getExpectedClientSecret() {
        loggingService.logGatewayDebug("ClientSecretValidationService: Obteniendo valor esperado del client secret");
        return secretValue;
    }

    public String getSecretHeaderName() {
        loggingService.logGatewayDebug("ClientSecretValidationService: Obteniendo nombre del header del client secret: {}", secretHeaderName);
        return secretHeaderName;
    }
}