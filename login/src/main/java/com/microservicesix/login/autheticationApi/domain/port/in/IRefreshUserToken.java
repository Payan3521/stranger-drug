package com.microservicesix.login.autheticationApi.domain.port.in;

import com.microservicesix.login.autheticationApi.domain.model.AuthenticationResult;

public interface IRefreshUserToken {
    AuthenticationResult refreshToken(String refreshToken);
}