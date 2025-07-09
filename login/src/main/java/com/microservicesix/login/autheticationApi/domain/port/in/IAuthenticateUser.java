package com.microservicesix.login.autheticationApi.domain.port.in;

import com.microservicesix.login.autheticationApi.domain.model.AuthenticationResult;

public interface IAuthenticateUser {
    AuthenticationResult authenticate(String email, String password, String ipAddress, String userAgent);
}