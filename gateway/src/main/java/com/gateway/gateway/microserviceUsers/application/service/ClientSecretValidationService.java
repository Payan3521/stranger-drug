package com.gateway.gateway.microserviceUsers.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.gateway.gateway.microserviceUsers.domain.port.IClientSecretValidationService;

@Slf4j
@Service
public class ClientSecretValidationService implements IClientSecretValidationService {

    @Value("${app.client.secret-header-name:X-Client-Secret}")
    private String secretHeaderName;

    @Value("${app.client.secret-value}")
    private String secretValue;

    @Override
    public boolean isValidClientSecret(String clientSecret) {
        if (clientSecret == null || secretValue == null) {
            log.warn("Client secret o valor esperado es null - ACCESO DENEGADO");
            return false;
        }

        boolean isValid = secretValue.equals(clientSecret);
        log.debug("Validación de client secret: {} - Header recibido: {}", 
            isValid ? "EXITOSA" : "FALLIDA", 
            clientSecret != null ? "***PRESENTE***" : "AUSENTE");
        
        if (!isValid) {
            log.warn("Client secret inválido - Valor esperado no coincide con el recibido");
        }
        
        return isValid;
    }

    @Override
    public String getExpectedClientSecret() {
        return secretValue;
    }

    public String getSecretHeaderName() {
        return secretHeaderName;
    }
}