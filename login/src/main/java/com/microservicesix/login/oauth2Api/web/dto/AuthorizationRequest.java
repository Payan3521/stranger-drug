package com.microservicesix.login.oauth2Api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationRequest {
    private String responseType; // "code"
    private String clientId;
    private String redirectUri;
    private String scope;
    private String state;
}
