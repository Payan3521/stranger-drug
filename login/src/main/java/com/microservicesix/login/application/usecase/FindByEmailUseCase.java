package com.microservicesix.login.application.usecase;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.microservicesix.login.domain.model.User;
import com.microservicesix.login.web.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindByEmailUseCase {
    
    private final RestTemplate restTemplate;

    @Value("${server.port:8082}")
    private String serverPort;

    private String getBaseUrl() {
        return "http://localhost:" + serverPort;
    }

    public User acceptLoginFindByEmailVerificate(String email){
        
        String url = getBaseUrl() + "/register/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        User user = new User();
        user.setEmail(email);
    
        HttpEntity<User> entity = new HttpEntity<>(user, headers);

        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(url, entity, ApiResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }

    }
}
