package com.microserviceone.users.registrationApi.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwicm9sIjoiQURNSU4ifQ.HwTopleWIIp9npTrGeh2s_uQInqfeyJgUhkxxEbf58A"); // JWT
            headers.set("X-App-Client-Key", "A1g2u3d4e5l6o7EAM_b1e2d3o4y5a6J5U6n7t8o9s3-2025-2006"); // Clave secreta
    
            HttpEntity<Void> entity = new HttpEntity<>(headers);
    
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
            );
    
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ObjectMapper mapper = new ObjectMapper();
                // Usa TypeReference para mapear la respuesta genérica
                ApiResponse<List<TermResponse>> apiResponse = mapper.readValue(
                    response.getBody(),
                    new TypeReference<ApiResponse<List<TermResponse>>>() {}
                );
                List<TermResponse> terms = apiResponse.getData();
                if (terms != null && !terms.isEmpty()) {
                    return terms.stream().map(TermResponse::getId).collect(Collectors.toList());
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