package com.microservicesix.login.autheticationApi.domain.port.in;

public interface ILogoutUser {
    void logout(String refreshToken);
}
