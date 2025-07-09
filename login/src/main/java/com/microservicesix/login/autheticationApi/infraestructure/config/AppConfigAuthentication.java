package com.microservicesix.login.autheticationApi.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import com.microservicesix.login.autheticationApi.application.service.AuthenticationService;
import com.microservicesix.login.autheticationApi.domain.port.in.IAuthenticateUser;
import com.microservicesix.login.autheticationApi.domain.port.in.ILogoutUser;
import com.microservicesix.login.autheticationApi.domain.port.in.IRefreshUserToken;
import com.microservicesix.login.autheticationApi.domain.port.in.IValidateToken;

@Configuration
public class AppConfigAuthentication {

    @Bean
    @Primary
    public AuthenticationService authenticationService(IAuthenticateUser authenticateUser, IRefreshUserToken refreshUserToken, ILogoutUser logoutUser, IValidateToken validateToken){
        return new AuthenticationService(authenticateUser, refreshUserToken, logoutUser, validateToken);
    }
}