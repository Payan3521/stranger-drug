package com.microservicesix.login.autheticationApi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResult {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private User user;
    private String scope;

    public static AuthenticationResult success(String accessToken, String refreshToken, Long expiresIn, User user) {
        return AuthenticationResult.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(expiresIn)
        .user(user)
        .scope("read write")
        .build();
    }

}