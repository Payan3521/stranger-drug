package com.microservicesix.login.autheticationApi.web.webMapper;

import org.springframework.stereotype.Component;
import com.microservicesix.login.autheticationApi.domain.model.AuthenticationResult;
import com.microservicesix.login.autheticationApi.web.dto.LoginResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationWebMapper {

    public LoginResponse toLoginResponse(AuthenticationResult result) {
        try {
            
            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                    .id(result.getUser().getId())
                    .name(result.getUser().getName())
                    .lastName(result.getUser().getLastName())
                    .email(result.getUser().getEmail())
                    .role(result.getUser().getRole().name())
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .accessToken(result.getAccessToken())
                    .refreshToken(result.getRefreshToken())
                    .tokenType(result.getTokenType())
                    .expiresIn(result.getExpiresIn())
                    .user(userInfo)
                    .scope(result.getScope())
                    .build();

            return response;
            
        } catch (Exception e) {
            throw e;
        }
    }
}