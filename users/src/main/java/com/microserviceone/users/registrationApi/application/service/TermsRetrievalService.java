package com.microserviceone.users.registrationApi.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microserviceone.users.core.logging.LoggingService;
import com.microserviceone.users.registrationApi.web.dto.ApiResponse;
import com.microserviceone.users.termsAndConditionsApi.web.dto.TermResponse;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TermsRetrievalService {
    
    private final RestTemplate restTemplate;
    private final LoggingService loggingService;
    private final ObjectMapper objectMapper;
    
    @Value("${server.port:8082}")
    private String serverPort;
    
    private String getBaseUrl() {
        return "http://localhost:" + serverPort;
    }
    
    /**
     * Obtiene los IDs de todos los términos activos
     */
    public List<Long> getActiveTermIds() {
        try {
            loggingService.logDebug("Obteniendo términos activos");
            
            String url = getBaseUrl() + "/terms";
            loggingService.logDebug("URL llamada: {}", url);

            // Sin cabeceras de autenticación
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            loggingService.logDebug("Status Code: {}", response.getStatusCode());
            loggingService.logDebug("Response Body: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ApiResponse<List<TermResponse>> apiResponse = objectMapper.readValue(
                    response.getBody(),
                    new TypeReference<ApiResponse<List<TermResponse>>>() {}
                );
                
                loggingService.logDebug("ApiResponse success: {}", apiResponse.isSuccess());
                loggingService.logDebug("ApiResponse message: {}", apiResponse.getMessage());
                
                List<TermResponse> terms = apiResponse.getData();
                loggingService.logDebug("Terms obtenidos: {}", terms != null ? terms.size() : 0);
                
                if (terms != null && !terms.isEmpty()) {
                    for (TermResponse term : terms) {
                        loggingService.logDebug("Término encontrado - ID: {}, Título: {}, Activo: {}", 
                            term.getId(), term.getTitle(), term.isActive());
                    }
                    
                    List<Long> termIds = terms.stream().map(TermResponse::getId).collect(Collectors.toList());
                    loggingService.logDebug("IDs de términos activos: {}", termIds);
                    return termIds;
                }
            }
            
            loggingService.logWarning("No se encontraron términos activos, usando término por defecto");
            return List.of(1L);

        } catch (Exception e) {
            loggingService.logError("Error al obtener términos activos, usando término por defecto", e);
            return List.of(1L);
        }
    }
} 